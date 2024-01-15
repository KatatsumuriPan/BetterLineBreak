package kpan.b_line_break.mixin;

import kpan.b_line_break.LineBreakingUtil;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public int getCharacterCountForWidth(String text, int offset) {
		return LineBreakingUtil.getCharacterCountForWidth((TextRenderer) (Object) this, text, offset);
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public int findWordEdge(String text, int direction, int position, boolean skipWhitespaceToRightOfWord) {
		return LineBreakingUtil.findWordEdge(text, direction, position, skipWhitespaceToRightOfWord);
	}

}
