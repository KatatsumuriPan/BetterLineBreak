package kpan.b_line_break.config;

import cpw.mods.fml.client.IModGuiFactory;
import kpan.b_line_break.config.core.gui.ModGuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

public class ModGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {

    }
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ModGuiConfig.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

}
