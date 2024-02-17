package kpan.b_line_break.asm.hook.integration.betterquesting;

import kpan.b_line_break.LineBreakingUtil;
import net.minecraft.client.gui.FontRenderer;

public class HK_RenderUtils {

	public static int sizeStringToWidth(String str, int wrapWidth, FontRenderer fontRenderer) {
		return LineBreakingUtil.getCharacterCountForWidth(fontRenderer, str, wrapWidth);
	}
	public static int getStringWidth(String text, FontRenderer font) {
		return font.getStringWidth(text);
	}

}
