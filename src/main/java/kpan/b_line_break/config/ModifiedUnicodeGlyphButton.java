/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package kpan.b_line_break.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

/**
 * This class provides a button that shows a string glyph at the beginning. The glyph can be scaled using the glyphScale parameter.
 *
 * @author bspkrs
 */
public class ModifiedUnicodeGlyphButton extends ExtendedButton {
	public String glyph;
	public float glyphScale;

	public ModifiedUnicodeGlyphButton(int xPos, int yPos, int width, int height, ITextComponent displayString, String glyph, float glyphScale, IPressable handler) {
		super(xPos, yPos, width, height, displayString, handler);
		this.glyph = glyph;
		this.glyphScale = glyphScale;
	}

	@Override
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partial) {
		if (visible) {
			Minecraft mc = Minecraft.getInstance();
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			int k = getYImage(isHovered);
			GuiUtils.drawContinuousTexturedBox(mStack, Button.WIDGETS_LOCATION, x, y, 0, 46 + k * 20, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());
			renderBg(mStack, mc, mouseX, mouseY);

			ITextComponent buttonText = getMessage();//createNarrationMessageを使うのはさすがにアホ
			int glyphWidth = mc.font.width(glyph);
			float scaledGlyphWidth = glyphWidth * glyphScale;
			int strWidth = mc.font.width(buttonText);
			int ellipsisWidth = mc.font.width("...");
			float totalWidth = strWidth + scaledGlyphWidth;

			if (totalWidth > width - 6 && totalWidth > ellipsisWidth) {
				buttonText = new StringTextComponent(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString().trim() + "...");
				strWidth = mc.font.width(buttonText);
				totalWidth = strWidth + scaledGlyphWidth;
			}

			mStack.pushPose();
			mStack.scale(glyphScale, glyphScale, 1.0F);
			float x = this.x + (width - totalWidth) / 2f;
			float y = this.y + (height - mc.font.lineHeight * glyphScale + 1) / 2f;
			drawString(mStack, mc.font, new StringTextComponent(glyph),
					(int) (x / glyphScale),
					(int) (y / glyphScale), getFGColor());
			mStack.popPose();

			drawString(mStack, mc.font, buttonText, (int) (this.x + (width - totalWidth) / 2f + scaledGlyphWidth),
					(int) (this.y + (height - mc.font.lineHeight + 1) / 2f), getFGColor());

		}
	}
}
