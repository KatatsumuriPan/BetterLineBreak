package kpan.b_line_break;

import kpan.b_line_break.config.ConfigHolder;
import kpan.b_line_break.config.ConfigScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModReference.MOD_ID)
public class ModMain {
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();

	public ModMain() {

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
		ModLoadingContext.get().registerConfig(Type.CLIENT, ConfigHolder.FORGE_CONFIG_SPEC);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
				() -> (mc, screen) -> new ConfigScreen(screen, ConfigHolder.FORGE_CONFIG_SPEC, "main"));
	}

}
