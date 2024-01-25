package kpan.b_line_break.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TextComponentUtil {

	public static List<ITextProperties> splitLines(ITextProperties iTextProperties) {
		return Minecraft.getInstance().font.getSplitter().splitLines(iTextProperties, Integer.MAX_VALUE, Style.EMPTY);
	}
}
