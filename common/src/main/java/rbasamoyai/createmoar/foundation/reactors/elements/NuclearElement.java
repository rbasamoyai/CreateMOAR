package rbasamoyai.createmoar.foundation.reactors.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import rbasamoyai.createmoar.CreateMOAR;
import rbasamoyai.createmoar.utils.MOARUtils;

public class NuclearElement {

	private final ResourceLocation id;
    private boolean invalid = false;

	private int atomicMass;
	private double density;
    private boolean isGas;

	private double moderationScalar;

	@Nullable private NuclearElement neutronCaptureProduct;
    private boolean destroyedByNeutronCapture;

	@Nullable private FissionProperties fissionProperties;
	@Nullable private DecayProperties decayProperties;
	@Nullable private AreaProperties thermalAreaProperties;
	@Nullable private AreaProperties fastAreaProperties;

	public NuclearElement(ResourceLocation id) {
		this.id = id;
	}

	public NuclearElement(ResourceLocation id, int atomicMass, double density, boolean isGas,
                          boolean destroyedByNeutronCapture,
                          @Nullable NuclearElement neutronCaptureProduct,
                          @Nullable FissionProperties fissionProperties,
                          @Nullable DecayProperties decayProperties,
                          @Nullable AreaProperties thermalAreaProperties,
                          @Nullable AreaProperties fastAreaProperties) {
		this(id);
		this.atomicMass = atomicMass;
		this.density = density * 1000; // g/cm^3 => g/dm^3 = g/L = g/mb = g/(81 droplets)
        this.isGas = isGas;
        this.destroyedByNeutronCapture = destroyedByNeutronCapture || neutronCaptureProduct != null;
		this.neutronCaptureProduct = neutronCaptureProduct;
		this.fissionProperties = fissionProperties;
		this.decayProperties = decayProperties;
		this.thermalAreaProperties = thermalAreaProperties;
		this.fastAreaProperties = fastAreaProperties;
		this.moderationScalar = calculateModerationScalar(this.atomicMass);
	}

    public static double calculateModerationScalar(int atomicMass) {
        double q = atomicMass - 1;
        double slowingDownDecrement = atomicMass == 1 ? 1 : 1 + ((q * q) / (2 * atomicMass)) * Math.log(q / (atomicMass + 1));
        return slowingDownDecrement / 13.8; // ln 2 MeV ~= 13.8
    }

	public ResourceLocation id() { return this.id; }

	public int atomicMass() { return this.atomicMass; }
	public double density() { return this.density; }
    public boolean isGas() { return this.isGas; }
    public boolean isDestroyedByNeutronCapture() { return this.destroyedByNeutronCapture; }

    /**
     * @param volume in dm^3 (= 1000 cm^3 = 1 L = 1 mB = 81 droplets)
     * @return the substance amount in moles (where 1 mol ≈ 6.022 x 10^23 molecules)
     * @implNote This should not be used if {@link #isGas()} returns true.
     */
    public double getAmountFromVolume(double volume) {
        return this.density / (double) this.atomicMass * volume;
    }

	public double moderationScalar() { return moderationScalar; }
	@Nullable public NuclearElement neutronCaptureProduct() { return this.neutronCaptureProduct; }
	@Nullable public FissionProperties fissionProperties() { return this.fissionProperties; }
	@Nullable public DecayProperties decayProperties() { return this.decayProperties; }
	@Nullable public AreaProperties thermalAreaProperties() { return this.thermalAreaProperties; }
	@Nullable public AreaProperties fastAreaProperties() { return this.fastAreaProperties; }

    public void invalidate() { this.invalid = true; }
    public boolean isInvalid() { return this.invalid; }

	public void readJson(JsonObject obj, String id) {
		this.atomicMass = getOrWarn(obj, "atomic_mass", id, 56, JsonElement::getAsInt);
        this.isGas = GsonHelper.getAsBoolean(obj, "is_gas", false);
        if (this.isGas) {
            this.density = 0;
            if (obj.has("density"))
                CreateMOAR.LOGGER.warn("Nuclear material {} cannot specify both gas state and density", id);
        } else {
            this.density = getOrWarn(obj, "density", id, 1.0, JsonElement::getAsDouble);
            if (this.density <= 0) {
                CreateMOAR.LOGGER.warn("Nuclear material {} has an invalid density, will be set to 1 g/cm^3 (was {} g/cm^3)", id, this.density);
                this.density = 1;
            }
            //this.density *= 1000; // Uncomment for "realistic scale" (with expected values passed being in dm)
        }

		if (obj.has("neutron_capture_product")) {
			this.neutronCaptureProduct = NuclearElementsHandler.get(MOARUtils.location(obj.get("neutron_capture_product").getAsString()));
            this.destroyedByNeutronCapture = true;
		} else {
            this.destroyedByNeutronCapture = GsonHelper.getAsBoolean(obj, "destroy_on_neutron_capture", false);
        }
		this.fissionProperties = getOrNull(obj, "fission_properties", obj1 -> FissionProperties.fromJson(obj1, id));
		this.decayProperties = getOrNull(obj, "decay_properties", obj1 -> DecayProperties.fromJson(obj1, id));
		this.thermalAreaProperties = getOrNull(obj, "thermal_area_properties", AreaProperties::fromJson);
		this.fastAreaProperties = getOrNull(obj, "fast_area_properties", AreaProperties::fromJson);

        this.moderationScalar = calculateModerationScalar(this.atomicMass);
	}

	@Override
	public String toString() {
		return "NuclearElement" + "[" + this.id + "]@" + this.hashCode();
	}

	private static <T> T getOrWarn(JsonObject obj, String key, String id, T defValue, Function<JsonElement, T> func) {
		if (!obj.has(key)) {
			CreateMOAR.LOGGER.warn("Nuclear material {} is missing value '{}', will be set to {}", id, key, defValue);
			return defValue;
		}
		return func.apply(obj.get(key));
	}

	private static <T> T getOrNull(JsonObject obj, String key, Function<JsonObject, T> func) {
		return obj.has(key) ? func.apply(obj.getAsJsonObject(key)) : null;
	}

	public record FissionProperties(Map<NuclearElement, Double> products, double releasedEnergy, double neutronMultiplier, double spontaneousFissionRate) {
		public static FissionProperties fromJson(JsonObject obj, String id) {
			JsonObject productsObj = getOrWarn(obj, "products", id, new JsonObject(), JsonElement::getAsJsonObject);
			Map<NuclearElement, Double> products = new HashMap<>();
			for (Map.Entry<String, JsonElement> entry : productsObj.entrySet()) {
				NuclearElement nukeEl = NuclearElementsHandler.get(MOARUtils.location(entry.getKey()));
				if (nukeEl == null)
                    continue;
				products.put(nukeEl, entry.getValue().getAsDouble());
			}

			double releasedEnergy = obj.has("released_energy") ? Math.max(0, obj.get("released_energy").getAsDouble()) : 0;
			double neutronMultiplier = obj.has("neutron_multiplier") ? Math.max(0, obj.get("neutron_multiplier").getAsDouble()) : 0;
			double spontaneousFissionRate = obj.has("spontaneous_fission_rate") ? Math.max(0, obj.get("spontaneous_fission_rate").getAsDouble()) : 0;
			return new FissionProperties(products, releasedEnergy, neutronMultiplier,spontaneousFissionRate);
		}
	}

	public record DecayProperties(Map<NuclearElement, Double> products, double halfLife, double releasedEnergy, double neutronsProduced) {
		public static DecayProperties fromJson(JsonObject obj, String id) {
			JsonObject productsObj = GsonHelper.getAsJsonObject(obj, "products");
			Map<NuclearElement, Double> products = new HashMap<>();
			for (Map.Entry<String, JsonElement> entry : productsObj.entrySet()) {
				NuclearElement nukeEl = NuclearElementsHandler.get(MOARUtils.location(entry.getKey()));
				if (nukeEl == null)
                    continue;
				products.put(nukeEl, entry.getValue().getAsDouble());
			}
			double halfLife = obj.has("half_life") ? Math.max(0.01, obj.get("half_life").getAsDouble()) : 0.01;
            double releasedEnergy = obj.has("released_energy") ? Math.max(0, obj.get("released_energy").getAsDouble()) : 0;
            double neutronsProduced = obj.has("neutrons_produced") ? Math.max(0, obj.get("neutrons_produced").getAsDouble()) : 0;
			return new DecayProperties(products, halfLife, releasedEnergy, neutronsProduced);
		}

        public double decayConstant() { return 0.693 / this.halfLife; }

        public double meanTime() { return this.halfLife / 0.693; }
	}

	public record AreaProperties(double scattering, double absorption, double fission) {
		public static AreaProperties fromJson(JsonObject obj) {
			double scattering = obj.has("scattering") ? Math.max(0, obj.get("scattering").getAsDouble()) : 0;
			double absorption = obj.has("absorption") ? Math.max(0, obj.get("absorption").getAsDouble()) : 0;
			double fission = obj.has("fission") ? Math.max(0, obj.get("fission").getAsDouble()) : 0;
			return new AreaProperties(scattering, absorption, fission);
		}
	}

	public static void getAllElements(CompoundTag tag, BiConsumer<NuclearElement, Double> cons) {
		for (String key : tag.getAllKeys()) {
			NuclearElement element = NuclearElementsHandler.get(MOARUtils.location(key));
			if (element == null || !(tag.get(key) instanceof NumericTag numeric))
                continue;
			cons.accept(element, numeric.getAsDouble());
		}
	}

}
