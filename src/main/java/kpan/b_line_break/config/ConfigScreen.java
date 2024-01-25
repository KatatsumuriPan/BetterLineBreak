package kpan.b_line_break.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import kpan.b_line_break.Modmain;
import kpan.b_line_break.config.ConfigEntries.AbstractGuiConfigEntry;
import net.minecraft.client.gui.screen.AlertScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigScreen extends Screen {
	public static final String UNDO_CHAR = "↶";//21B6
	public static final String RESET_CHAR = "☄";//2604
	public static final String VALID = "✔";//2714
	public static final String INVALID = "✕";//2715


	private final Screen parent;
	@Nullable
	public ITextComponent titleLine2;
	public final List<String> categoryPath;
	public final ForgeConfigSpec forgeConfigSpec;
	public ConfigEntries entryList;
	protected Button btnDefaultAll;
	protected Button btnUndoAll;
	protected CheckboxButton chkApplyGlobally;
	/**
	 * When set to a non-null value the OnConfigChanged and PostConfigChanged events will be posted when the Done button is pressed
	 * if any configElements were changed (includes child screens). If not defined, the events will be posted if the parent gui is null
	 * or if the parent gui is not an instance of GuiConfig.
	 */
	@Nullable
	public final String configID;
	public boolean isWorldRunning;
	public boolean allRequireWorldRestart;
	public boolean allRequireMcRestart;
	public boolean needsRefresh = true;
	protected HoverChecker undoHoverChecker;
	protected HoverChecker resetHoverChecker;
	protected HoverChecker checkBoxHoverChecker;

	public ConfigScreen(Screen parent, ForgeConfigSpec forgeConfigSpec, @Nullable String configID) {
		this(parent, forgeConfigSpec, Collections.emptyList(), configID, false, false, new StringTextComponent("Config GUI"), null);
	}
	public ConfigScreen(Screen parent, ForgeConfigSpec forgeConfigSpec, List<String> categoryPath, @Nullable String configID, boolean allRequireWorldRestart, boolean allRequireMcRestart, ITextComponent title, @Nullable ITextComponent title2) {
		super(title);
		this.parent = parent;
		this.categoryPath = categoryPath;
		this.configID = configID;
		this.forgeConfigSpec = forgeConfigSpec;
		titleLine2 = title2;
		this.allRequireWorldRestart = allRequireWorldRestart;
		this.allRequireMcRestart = allRequireMcRestart;

	}

	@Override
	protected void init() {
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		isWorldRunning = minecraft.level != null;
		if (entryList == null || needsRefresh) {
			entryList = new ConfigEntries(this, minecraft);
			needsRefresh = false;
		}
		addWidget(entryList);

		//短絡評価させるためには複合代入演算子を使えないという罠
		allRequireWorldRestart = allRequireWorldRestart || entryList.children().stream().allMatch(AbstractGuiConfigEntry::requiresWorldRestart);
		allRequireMcRestart = allRequireMcRestart || entryList.children().stream().allMatch(AbstractGuiConfigEntry::requiresMcRestart);

		int undoGlyphWidth = font.width(UNDO_CHAR) * 2;
		int resetGlyphWidth = font.width(RESET_CHAR) * 2;
		int doneWidth = Math.max(font.width(I18n.get("gui.done")) + 20, 100);
		int undoWidth = font.width(I18n.get("gui.config.tooltip.undoChanges")) + undoGlyphWidth + 16;
		int resetWidth = font.width(I18n.get("gui.config.tooltip.resetToDefault")) + resetGlyphWidth + 16;
		int checkWidth = font.width(I18n.get("gui.config.applyGlobally")) + 20;
		int buttonWidthHalf = (doneWidth + 5 + undoWidth + 3 + resetWidth + 3 + checkWidth) / 2;
		addButton(new ExtendedButton(width / 2 - buttonWidthHalf, height - 29, doneWidth, 20, new TranslationTextComponent("gui.done"), btn -> {
			boolean flag = true;
			try {
				if ((configID != null || parent == null || !(parent instanceof ConfigScreen))
						&& (entryList.hasChangedEntry(true))) {
					boolean requiresMcRestart = entryList.saveConfigElements();

					ConfigHolder.FORGE_CONFIG_SPEC.save();

					if (requiresMcRestart) {
						flag = false;
						minecraft.setScreen(new AlertScreen(() -> {
							minecraft.setScreen(this);
						}, new TranslationTextComponent("gui.config.gameRestartTitle"),
								new TranslationTextComponent("gui.config.gameRestartRequired"), new TranslationTextComponent("gui.config.confirmRestartMessage")));
					}

					if (parent instanceof ConfigScreen)
						((ConfigScreen) parent).needsRefresh = true;
				}
			} catch (Throwable e) {
				Modmain.LOGGER.error("Error performing GuiConfig action:", e);
			}

			if (flag)
				minecraft.setScreen(parent);
		}));
		//UNDO_CHARがなんか小さすぎる
		btnUndoAll = addButton(new ModifiedUnicodeGlyphButton(width / 2 - buttonWidthHalf + doneWidth + 5,
				height - 29, undoWidth, 20, new StringTextComponent(" ").append(new TranslationTextComponent("gui.config.tooltip.undoChanges")), UNDO_CHAR, 2f, btn -> {
			entryList.undoAllChanges(chkApplyGlobally.selected());
		}));
		btnDefaultAll = addButton(new ModifiedUnicodeGlyphButton(width / 2 - buttonWidthHalf + doneWidth + 5 + undoWidth + 3,
				height - 29, resetWidth, 20, new StringTextComponent(" ").append(new TranslationTextComponent("gui.config.tooltip.resetToDefault")), RESET_CHAR, 1.25f, btn -> {
			entryList.setAllToDefault(chkApplyGlobally.selected());
		}));
		chkApplyGlobally = addButton(new CheckboxButton(width / 2 - buttonWidthHalf + doneWidth + 5 + undoWidth + 3 + resetWidth + 3,
				height - 24 - 4, 20, 20, new TranslationTextComponent("gui.config.applyGlobally"), false));

		undoHoverChecker = new HoverChecker(btnUndoAll, 800);
		resetHoverChecker = new HoverChecker(btnDefaultAll, 800);
		checkBoxHoverChecker = new HoverChecker(chkApplyGlobally, 800);
		entryList.init();
	}

	@Override
	public void onClose() {
		entryList.onClose();

		if (configID != null && parent instanceof ConfigScreen) {
			ConfigScreen configScreen = (ConfigScreen) parent;
			configScreen.needsRefresh = true;
			configScreen.init();
		}

		if (!(parent instanceof ConfigScreen))
			minecraft.keyboardHandler.setSendRepeatsToGui(false);
		super.onClose();
	}

	@Override
	public void tick() {
		entryList.tick();
		super.tick();
	}

	@Override
	public boolean charTyped(char p_231042_1_, int p_231042_2_) {
		//これ必要ないみたいだね
		//entryList.charTyped(p_231042_1_, p_231042_2_);
		return super.charTyped(p_231042_1_, p_231042_2_);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
		renderBackground(matrixStack);
		entryList.render(matrixStack, mouseX, mouseY, p_230430_4_);
		drawCenteredString(matrixStack, font, getTitle(), width / 2, 8, 0xFFFFFF);

		ITextComponent title2 = titleLine2;

		if (title2 != null) {
			int strWidth = font.width(title2);
			int ellipsisWidth = font.width("...");
			if (strWidth > width - 6 && strWidth > ellipsisWidth)
				title2 = new StringTextComponent(font.substrByWidth(title2, width - 6 - ellipsisWidth).getString() + "...");
			drawCenteredString(matrixStack, font, title2, width / 2, 18, 0xFFFFFF);
		}

		btnUndoAll.active = entryList.areAnyEntriesEnabled(chkApplyGlobally.selected()) && entryList.hasChangedEntry(chkApplyGlobally.selected());
		btnDefaultAll.active = entryList.areAnyEntriesEnabled(chkApplyGlobally.selected()) && !entryList.areAllEntriesDefault(chkApplyGlobally.selected());

		super.render(matrixStack, mouseX, mouseY, p_230430_4_);

		if (undoHoverChecker.checkHover(mouseX, mouseY))
			drawToolTip(matrixStack, Arrays.asList(new TranslationTextComponent("gui.config.tooltip.undoAll")), mouseX, mouseY);
		if (resetHoverChecker.checkHover(mouseX, mouseY))
			drawToolTip(matrixStack, Arrays.asList(new TranslationTextComponent("gui.config.tooltip.resetAll")), mouseX, mouseY);
		if (checkBoxHoverChecker.checkHover(mouseX, mouseY))
			drawToolTip(matrixStack, Arrays.asList(new TranslationTextComponent("gui.config.tooltip.applyGlobally")), mouseX, mouseY);

	}


	public void drawToolTip(MatrixStack matrixStack, List<ITextProperties> stringList, int x, int y) {
		GuiUtils.drawHoveringText(matrixStack, stringList, x, y, width, height, 300, font);
	}
}
