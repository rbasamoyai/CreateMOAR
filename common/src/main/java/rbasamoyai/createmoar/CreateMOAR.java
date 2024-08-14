package rbasamoyai.createmoar;

import com.mojang.logging.LogUtils;

import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;

import rbasamoyai.createmoar.network.MOARNetwork;
import rbasamoyai.createmoar.utils.MOARUtils;

public class CreateMOAR
{
	public static final String MOD_ID = "createmoar";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

	public static void init() {
        MOARNetwork.init();
	}

    public static ResourceLocation path(String path) { return MOARUtils.location(MOD_ID, path); }

}
