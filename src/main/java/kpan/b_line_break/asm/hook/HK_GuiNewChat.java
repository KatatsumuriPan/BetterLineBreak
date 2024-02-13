package kpan.b_line_break.asm.hook;

import kpan.b_line_break.LineBreakingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;

public class HK_GuiNewChat {
    public static ArrayList<IChatComponent> splitText(IChatComponent textComponent, int maxTextLength) {
        return (ArrayList<IChatComponent>) LineBreakingUtil.wrapLines(textComponent, maxTextLength, Minecraft.getMinecraft().fontRenderer, false, false);
    }
}
