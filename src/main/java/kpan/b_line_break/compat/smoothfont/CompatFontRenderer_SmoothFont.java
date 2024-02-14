package kpan.b_line_break.compat.smoothfont;

import bre.smoothfont.FontRendererHook;
import bre.smoothfont.FontUtils;
import bre.smoothfont.asm.CorePlugin;
import kpan.b_line_break.compat.optifine.CompatFontRenderer_Optifine;
import net.minecraft.client.gui.FontRenderer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class CompatFontRenderer_SmoothFont {

    private static final MethodHandle fontRendererHook;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            {
                Field f = FontRenderer.class.getField("fontRendererHook");
                fontRendererHook = lookup.unreflectGetter(f);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO:まだまだ足りない

    public static float getCharWidthFloat(FontRenderer fontRenderer, char ch) {
        return getFontRendererHook(fontRenderer).getCharWidthFloat(ch);
    }

    public static float getOffsetBold(FontRenderer fontRenderer, char ch) {
        if (CorePlugin.optifineExist) {
            int defaultGlyph = FontUtils.getDefaultGlyphIndex(ch);
            FontRendererHook fontRendererHook = getFontRendererHook(fontRenderer);
            return (defaultGlyph == -1 || fontRendererHook.thinFontFlag) ? 0.5F : CompatFontRenderer_Optifine.getOffsetBold(fontRenderer);
        } else {
            return 1;
        }
    }

    public static FontRendererHook getFontRendererHook(FontRenderer fontRenderer) {
        try {
            return (FontRendererHook) fontRendererHook.invokeExact(fontRenderer);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
