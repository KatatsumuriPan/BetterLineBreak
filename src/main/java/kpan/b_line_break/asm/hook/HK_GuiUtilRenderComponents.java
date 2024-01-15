package kpan.b_line_break.asm.hook;

import kpan.b_line_break.LineBreakingUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class HK_GuiUtilRenderComponents {
	public static final Class<?> c = GuiUtilRenderComponents.class;

	public static List<ITextComponent> splitText(ITextComponent textComponent, int maxTextLenght, FontRenderer fontRendererIn, boolean p_178908_3_, boolean forceTextColor) {
		return LineBreakingUtil.wrapLines(textComponent, maxTextLenght, fontRendererIn, p_178908_3_, forceTextColor);
	}
}
