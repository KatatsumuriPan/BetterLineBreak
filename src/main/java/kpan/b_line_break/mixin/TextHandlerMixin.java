package kpan.b_line_break.mixin;

import kpan.b_line_break.LineBreakingUtil;
import kpan.b_line_break.TextHandlerAccessor;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextHandler.LineWrappingConsumer;
import net.minecraft.client.font.TextHandler.WidthRetriever;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BiConsumer;

@Mixin(TextHandler.class)
public abstract class TextHandlerMixin implements TextHandlerAccessor {

	@Final
	@Shadow
	WidthRetriever widthRetriever;


	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public int getEndingIndex(String text, int maxWidth, Style style) {
		return LineBreakingUtil.getEndingIndex(this,text,maxWidth,style);
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void wrapLines(String text, int maxWidth, Style resetStyle, boolean retainTrailingWordSplit, LineWrappingConsumer consumer) {
		LineBreakingUtil.wrapLines(this, text, maxWidth, resetStyle, retainTrailingWordSplit, consumer);
	}


	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public void wrapLines(StringVisitable stringVisitable, int maxWidth, Style resetStyle, BiConsumer<StringVisitable, Boolean> biConsumer) {
		LineBreakingUtil.wrapLines(this, stringVisitable, maxWidth, resetStyle, biConsumer);
	}

	@Override
	public WidthRetriever betterLineBreak$getWidthRetriever() {
		return widthRetriever;
	}

}
