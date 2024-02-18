package kpan.b_line_break.mixin;

import kpan.b_line_break.LineBreakingUtil;
import kpan.b_line_break.StringSplitterAccessor;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.StringSplitter.WidthProvider;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BiConsumer;

@Mixin(StringSplitter.class)
public abstract class StringSplitterMixin implements StringSplitterAccessor {

	@Final
	@Shadow
	WidthProvider widthProvider;

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public int findLineBreak(String text, int maxWidth, Style style) {
		return LineBreakingUtil.findLineBreak(this, text, maxWidth, style);
	}


	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void splitLines(String text, int maxWidth, Style resetStyle, boolean retainTrailingWordSplit, StringSplitter.LinePosConsumer consumer) {
		LineBreakingUtil.splitLines(this, text, maxWidth, resetStyle, retainTrailingWordSplit, consumer);
	}


	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void splitLines(FormattedText stringVisitable, int maxWidth, Style resetStyle, BiConsumer<FormattedText, Boolean> biConsumer) {
		LineBreakingUtil.splitLines(this, stringVisitable, maxWidth, resetStyle, biConsumer);
	}

	@Override
	public WidthProvider betterLineBreak$getWidthProvider() {
		return widthProvider;
	}

}
