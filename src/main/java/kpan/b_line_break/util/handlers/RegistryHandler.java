package kpan.b_line_break.util.handlers;

import kpan.b_line_break.ModMain;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@EventBusSubscriber
public class RegistryHandler {

	public static void preInitRegistries(@SuppressWarnings("unused") FMLPreInitializationEvent event) {
		ModMain.proxy.registerOnlyClient();
	}

	public static void initRegistries() {
	}

	public static void postInitRegistries() {
	}

	public static void serverRegistries(@SuppressWarnings("unused") FMLServerStartingEvent event) {
	}

//	@SubscribeEvent
//	public static void onEnchantmentRegister(RegistryEvent.Register<Enchantment> event) {
//	}

//	@SubscribeEvent
//	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
//	}

//	@SubscribeEvent
//	public static void onDataSerializerRegister(RegistryEvent.Register<DataSerializerEntry> event) {
//	}

}
