package rbasamoyai.createmoar.datagen;

public class MOARDatagenCommon {

	public static final MOARDatagenPlatform PLATFORM = MOARDatagenPlatform.getPlatform(System.getProperty("createmoar.datagen.platform"));
	public static final int FLUID_MULTIPLIER = PLATFORM.fluidMultiplier();

	public static void init() {}

}
