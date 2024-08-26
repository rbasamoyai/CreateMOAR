package rbasamoyai.createmoar.content.reactors.fuel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;

import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElement;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.ConstantElementView;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;
import rbasamoyai.createmoar.index.MOARNuclearElements;
import rbasamoyai.createmoar.utils.MOARUtils;

public class FuelBundleBlockEntity extends SyncedBlockEntity {

	private final Map<NuclearElement, Double> contentsByVolume = new Reference2DoubleOpenHashMap<>();
    private final Map<NuclearElement, Double> productsByAmount = new Reference2DoubleOpenHashMap<>();
	private float maxCapacity;

	public FuelBundleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.maxCapacity = defaultMaxFuelRodCapacityVolume();
		this.contentsByVolume.put(MOARNuclearElements.URANIUM_238, this.maxCapacity * 0.98);
		this.contentsByVolume.put(MOARNuclearElements.URANIUM_235, this.maxCapacity * 0.02);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putFloat("MaxCapacity", this.maxCapacity);

        CompoundTag contentsTag = new CompoundTag();
        MOARUtils.writeElementsToNbt(contentsTag, this.contentsByVolume);
		tag.put("Contents", contentsTag);

        CompoundTag productsTag = new CompoundTag();
        MOARUtils.writeElementsToNbt(productsTag, this.productsByAmount);
        tag.put("Products", productsTag);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.maxCapacity = Math.max(1, tag.getFloat("MaxCapacity"));

		this.contentsByVolume.clear();
        MOARUtils.loadElementsFromNbt(tag.getCompound("Contents"), this.contentsByVolume);
		this.productsByAmount.clear();
        MOARUtils.loadElementsFromNbt(tag.getCompound("Products"), this.productsByAmount);
	}

	public void tick() {

	}

	public InteractionResult onFillBlock(ItemStack stack) {
        if (this.level == null)
            return InteractionResult.PASS;
        // TODO: fuel contents of fill stack
		Map<NuclearElement, Double> otherFuel = new HashMap<>();
		otherFuel.put(MOARNuclearElements.URANIUM_238, 980d);
		otherFuel.put(MOARNuclearElements.URANIUM_235, 20d);

		if (getFilledCapacity(otherFuel) + getFilledCapacity(this.contentsByVolume) > this.maxCapacity)
            return InteractionResult.PASS;
		if (!this.level.isClientSide) {
            for (Map.Entry<NuclearElement, Double> entry : otherFuel.entrySet())
                this.contentsByVolume.merge(entry.getKey(), entry.getValue(), Double::sum);
			this.notifyUpdate();
		}
		return InteractionResult.sidedSuccess(this.level.isClientSide);
	}

    public List<NuclearElementView> getElementViews() {
        double volume = this.getBlockState().getBlock() instanceof FuelBundleBlock bundle
            ? bundle.getRodVolume(this.getBlockState()) : 1000;
        List<NuclearElementView> list = MOARUtils.getFuelElements(this.contentsByVolume, this.productsByAmount, volume, this::addProducts);
        list.add(ConstantElementView.fromVolume(MOARNuclearElements.IRON_56, 100, this::addProducts));
        return list;
    }

    public void addProducts(NuclearElement element, double amount) {
        this.contentsByVolume.merge(element, amount, Double::sum);
    }

	public static double getFilledCapacity(Map<NuclearElement, Double> map) {
        double sum = 0;
		for (Double d : map.values())
            sum += d;
        return sum;
	}

	public static float defaultMaxFuelRodCapacityVolume() { return 1000; } // TODO config

}
