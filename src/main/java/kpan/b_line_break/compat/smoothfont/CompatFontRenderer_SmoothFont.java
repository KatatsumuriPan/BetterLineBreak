package kpan.b_line_break.compat.smoothfont;

import net.minecraft.client.gui.FontRenderer;
import org.apache.commons.lang3.NotImplementedException;

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
        throw new NotImplementedException("");
    }

    public static float getOffsetBold(FontRenderer fontRenderer, char ch) {
        throw new NotImplementedException("");
//        if (CorePlugin.optifineExist) {
//            int defaultGlyph = FontUtils.getDefaultGlyphIndex(ch);
//            FontRendererHook fontRendererHook = getFontRendererHook(fontRenderer);
//            return (defaultGlyph == -1 || fontRendererHook.thinFontFlag) ? 0.5F : CompatFontRenderer_Optifine.getOffsetBold(fontRenderer);
//        } else {
//            return 1;
//        }
    }

//    public static FontRendererHook getFontRendererHook(FontRenderer fontRenderer) {
//        try {
//            return (FontRendererHook) fontRendererHook.invokeExact(fontRenderer);
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//    }

}
