package rbasamoyai.createmoar.fabric;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import rbasamoyai.createmoar.CreateMOAR;
import rbasamoyai.createmoar.datagen.MOARDatagenCommon;

public class CreateMOARDataFabric implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		Path cbcResources = Paths.get(System.getProperty(ExistingFileHelper.EXISTING_RESOURCES));
		ExistingFileHelper helper = new ExistingFileHelper(
			Set.of(cbcResources), Set.of("create"), false, null, null
		);
		CreateMOAR.REGISTRATE.setupDatagen(generator.createPack(), helper);
		MOARDatagenCommon.init();

		FabricDataGenerator.Pack modDatapack = generator.createPack();

//		MOARCraftingRecipeProvider.register();
//
//		MOARLangGen.prepare();
//		modDatapack.addProvider((Factory<MOARSoundEvents.SoundEntryProvider>) MOARSoundEvents::provider);
//		MOARSoundEvents.registerLangEntries();
//		MOARPonderTags.register();
//		MOARPonderIndex.register();
//		MOARPonderIndex.registerLang();
//
//		modDatapack.addProvider((Factory<MOARBlockPartialsGen>) output -> new MOARBlockPartialsGen(output, helper));
//
//		modDatapack.addProvider(MOARCompactingRecipeProvider::new);
//		modDatapack.addProvider(MeltingRecipeProvider::new);
//		modDatapack.addProvider(MOARMixingRecipeProvider::new);
//		modDatapack.addProvider(MOARMillingRecipeProvider::new);
//		modDatapack.addProvider(MOARSequencedAssemblyRecipeProvider::new);
//		modDatapack.addProvider(MOARCuttingRecipeProvider::new);
	}

}
