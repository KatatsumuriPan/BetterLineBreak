package kpan.b_line_break.config;

import kpan.b_line_break.ModMain;
import kpan.b_line_break.ModTagsGenerated;
import kpan.b_line_break.config.core.gui.ModGuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class ModGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {

	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new ModGuiConfig(parentScreen, ModMain.defaultConfig.getRootCategory().getOrderedElements(), null, false, false, ModTagsGenerated.MODNAME, null);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

}
