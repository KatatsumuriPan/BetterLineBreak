package kpan.b_line_break.config.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kpan.b_line_break.ModMain;
import net.minecraft.client.resources.I18n;

public class CommentLocalizer {

    public static String tryLocalize(String localizationKey, String defaultValue) {
        if (!ModMain.proxy.hasClientSide())
            return defaultValue;

        String localized = format(localizationKey);
        if (localizationKey.equals(localized))
            return defaultValue;
        else
            return localized;
    }

    @SideOnly(Side.CLIENT)
    private static String format(String localizationKey) {
        return I18n.format(localizationKey);
    }
}
