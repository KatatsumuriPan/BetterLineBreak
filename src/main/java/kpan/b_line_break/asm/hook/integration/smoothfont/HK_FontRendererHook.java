package kpan.b_line_break.asm.hook.integration.smoothfont;

import bre.smoothfont.FontRendererHook;
import kpan.b_line_break.asm.hook.HK_FontRenderer;

public class HK_FontRendererHook {

	public static int sizeStringToWidthFloatHook(FontRendererHook self, String str, int wrapWidth) {
		return HK_FontRenderer.sizeStringToWidth(self.fontRenderer, str, wrapWidth);
	}
}
