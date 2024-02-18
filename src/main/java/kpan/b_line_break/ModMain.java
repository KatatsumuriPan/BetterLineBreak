package kpan.b_line_break;

import kpan.b_line_break.config.ConfigHolder;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModReference.MODID)
public class ModMain {
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger(ModReference.MODNAME);

	public ModMain() {
		ModLoadingContext.get().registerConfig(Type.CLIENT, ConfigHolder.FORGE_CONFIG_SPEC);
	}

}
