package kpan.b_line_break.compat;

import kpan.b_line_break.asm.compat.CompatOptifine;
import kpan.b_line_break.asm.compat.CompatSmoothFont;
import kpan.b_line_break.compat.optifine.CompatFontRenderer_Optifine;
import kpan.b_line_break.compat.smoothfont.CompatFontRenderer_SmoothFont;
import net.minecraft.client.gui.FontRenderer;

public class CompatFontRenderer {

	public static float getCharWidthFloat(FontRenderer fontRenderer, char ch) {
		if (CompatSmoothFont.isLoaded())
			return CompatFontRenderer_SmoothFont.getCharWidthFloat(fontRenderer, ch);
		else if (CompatOptifine.isLoaded())
			return CompatFontRenderer_Optifine.getCharWidthFloat(fontRenderer, ch);
		else
			return fontRenderer.getCharWidth(ch);
	}

	public static float getOffsetBold(FontRenderer fontRenderer, char ch) {
		if (CompatSmoothFont.isLoaded())
			return CompatFontRenderer_SmoothFont.getOffsetBold(fontRenderer, ch);
		else if (CompatOptifine.isLoaded())
			return CompatFontRenderer_Optifine.getOffsetBold(fontRenderer);
		else
			return 1;
	}
}
