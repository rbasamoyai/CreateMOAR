package rbasamoyai.createmoar;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.ResourceLocation;
import rbasamoyai.createmoar.foundation.reactors.saved_data.GlobalReactorManager;
import rbasamoyai.createmoar.index.MOARBlockEntities;
import rbasamoyai.createmoar.index.MOARBlocks;
import rbasamoyai.createmoar.index.MOARItems;
import rbasamoyai.createmoar.index.MOARNuclearElements;
import rbasamoyai.createmoar.index.ModGroup;
import rbasamoyai.createmoar.network.MOARNetwork;
import rbasamoyai.createmoar.utils.MOARUtils;

public class CreateMOAR
{
	public static final String MOD_ID = "createmoar";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public static final GlobalReactorManager REACTOR_MANAGER = new GlobalReactorManager();

	public static void init() {
        MOARNetwork.init();

        ModGroup.register();
        MOARBlocks.register();
        MOARItems.register();
        MOARBlockEntities.register();
        MOARNuclearElements.register();
	}

    public static ResourceLocation path(String path) { return MOARUtils.location(MOD_ID, path); }

}
