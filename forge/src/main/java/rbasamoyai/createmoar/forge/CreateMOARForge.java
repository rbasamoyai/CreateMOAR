package rbasamoyai.createmoar.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import rbasamoyai.createmoar.CreateMOAR;
import rbasamoyai.createmoar.utils.EnvExecute;

@Mod(CreateMOAR.MOD_ID)
public class CreateMOARForge {

    public CreateMOARForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext mlContext = ModLoadingContext.get();

        CreateMOAR.REGISTRATE.registerEventListeners(modBus);
        CreateMOAR.init();

        modBus.addListener(this::onCommonSetup);

        MOARCommonEventsForge.register(forgeBus);

        EnvExecute.executeOnClient(() -> () -> MOARClientForge.onCtor(modBus, forgeBus));
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

    }


}
