package kpan.b_line_break.asm.hook;

import kpan.b_line_break.LineBreakingUtil;
import net.minecraft.client.gui.FontRenderer;

public class HK_FontRenderer {

	public static int sizeStringToWidth(FontRenderer self, String str, int wrapWidth) {
		return LineBreakingUtil.getCharacterCountForWidth(self, str, wrapWidth);
	}

}
