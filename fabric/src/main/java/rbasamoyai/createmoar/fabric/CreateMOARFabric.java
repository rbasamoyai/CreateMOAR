package rbasamoyai.createmoar.fabric;

import rbasamoyai.createmoar.CreateMOAR;
import net.fabricmc.api.ModInitializer;

public class CreateMOARFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CreateMOAR.init();
        CreateMOAR.REGISTRATE.register();

        MOARCommonEventsFabric.register();
    }

}
