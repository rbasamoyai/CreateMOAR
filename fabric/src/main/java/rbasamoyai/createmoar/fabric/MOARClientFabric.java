package rbasamoyai.createmoar.fabric;

import net.fabricmc.api.ClientModInitializer;
import rbasamoyai.createmoar.MOARClient;

public class MOARClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MOARClient.onClientSetup();
    }

}
