package kpan.b_line_break;

import kpan.b_line_break.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterLineBreak implements ModInitializer {
	public static final String MOD_ID = "better_line_break";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ModConfig config;

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}
}