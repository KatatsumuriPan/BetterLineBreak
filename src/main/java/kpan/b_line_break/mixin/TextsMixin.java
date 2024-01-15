package kpan.b_line_break.mixin;

import kpan.b_line_break.LineBreakingUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Texts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(Texts.class)
public class TextsMixin {

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public static List<Text> wrapLines(Text text, int width, TextRenderer textRenderer, boolean bl, boolean forceColor) {
		return LineBreakingUtil.wrapLines(text, width, textRenderer, bl, forceColor);
	}
}
