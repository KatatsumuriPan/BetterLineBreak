package kpan.b_line_break.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import kpan.b_line_break.BetterLineBreak;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return parent -> AutoConfig.getConfigScreen(ModConfig.class, parent).get();
	}
	@Override
	public String getModId() {
		return BetterLineBreak.MOD_ID;
	}
}