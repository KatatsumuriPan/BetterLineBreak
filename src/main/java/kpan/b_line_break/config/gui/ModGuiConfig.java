package kpan.b_line_break.config.gui;

import cpw.mods.fml.client.config.GuiConfig;
import kpan.b_line_break.config.ConfigHolder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import static kpan.b_line_break.ModReference.MODID;

public class ModGuiConfig extends GuiConfig {

    public ModGuiConfig(GuiScreen parentScreen) {
        super(
            parentScreen,
            new ConfigElement(ConfigHolder.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
            MODID,
            false,
            false,
            GuiConfig.getAbridgedConfigPath(ConfigHolder.config.toString())
        );
    }
}
