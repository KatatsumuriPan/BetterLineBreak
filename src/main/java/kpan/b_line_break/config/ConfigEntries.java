package kpan.b_line_break.config;

import com.electronwill.nightconfig.core.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import kpan.b_line_break.ModReference;
import kpan.b_line_break.config.ConfigEntries.AbstractGuiConfigEntry;
import kpan.b_line_break.util.ListUtil;
import kpan.b_line_break.util.TextComponentUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigEntries extends AbstractList<AbstractGuiConfigEntry> {
	private final ConfigScreen configScreen;

	/**
	 * The max width of the label of all IConfigEntry objects.
	 */
	public int maxLabelTextWidth = 0;
	/**
	 * The max x boundary of all IConfigEntry objects.
	 */
	public int maxEntryRightBound = 0;
	/**
	 * The x position where the label should be drawn.
	 */
	public int labelX;
	/**
	 * The x position where the control should be drawn.
	 */
	public int controlX;
	/**
	 * The width of the control.
	 */
	public int controlWidth;
	/**
	 * The minimum x position where the Undo/Default buttons will start
	 */
	public int resetX;
	/**
	 * The x position of the scroll bar.
	 */
	public int scrollBarX;

	public ConfigEntries(ConfigScreen configScreen, Minecraft minecraft) {
		super(minecraft, configScreen.width, configScreen.height, configScreen.titleLine2 != null ? 33 : 23, configScreen.height - 32, 20);
		this.configScreen = configScreen;
		setRenderSelection(false);

		Map<String, Object> map;
		if (configScreen.categoryPath.isEmpty()) {
			map = configScreen.forgeConfigSpec.getValues().valueMap();
		} else {
			map = configScreen.forgeConfigSpec.getValues().valueMap();
			for (String category : configScreen.categoryPath) {
				map = ((Config) map.get(category)).valueMap();
			}
		}
		for (Object configElement : map.values()) {
			if (configElement instanceof ConfigValue<?>) {
				int length = minecraft.font.width(ConfigUtil.getTranslatedText(configScreen.forgeConfigSpec, (ConfigValue<?>) configElement));
				if (length > maxLabelTextWidth)
					maxLabelTextWidth = length;
			}
		}

		int viewWidth = maxLabelTextWidth + 8 + (width / 2);
		labelX = (width / 2) - (viewWidth / 2);
		controlX = labelX + maxLabelTextWidth + 8;
		resetX = (width / 2) + (viewWidth / 2) - 45;
		controlWidth = resetX - controlX - 5;
		scrollBarX = width;

		for (Entry<String, Object> entry : map.entrySet()) {
			children().add(AbstractGuiConfigEntry.toEntry(entry.getKey(), entry.getValue(), this));
		}

	}

	public void init() {
		width = configScreen.width;
		height = configScreen.height;

		maxLabelTextWidth = 0;
		for (AbstractGuiConfigEntry entry : children()) {
			if (entry.getLabelWidth() > maxLabelTextWidth)
				maxLabelTextWidth = entry.getLabelWidth();
		}

		y0 = configScreen.titleLine2 != null ? 33 : 23;
		y1 = configScreen.height - 32;
		x0 = 0;
		x1 = width;
		int viewWidth = maxLabelTextWidth + 8 + (width / 2);
		labelX = (width / 2) - (viewWidth / 2);
		controlX = labelX + maxLabelTextWidth + 8;
		resetX = (width / 2) + (viewWidth / 2) - 45;

		maxEntryRightBound = 0;
		for (AbstractGuiConfigEntry entry : children()) {
			if (entry.getEntryRightBound() > maxEntryRightBound)
				maxEntryRightBound = entry.getEntryRightBound();
		}

		scrollBarX = maxEntryRightBound + 5;
		controlWidth = maxEntryRightBound - controlX - 45;
	}

	public void tick() {
		for (AbstractGuiConfigEntry child : children()) {
			child.tick();
		}
	}

	@Override
	protected void renderDecorations(MatrixStack matrixStack, int mouseX, int mouseY) {
		super.renderDecorations(matrixStack, mouseX, mouseY);
		for (AbstractGuiConfigEntry entry : children()) {
			entry.drawToolTip(matrixStack, mouseX, mouseY);
		}
	}
	@Override
	protected int getScrollbarPosition() {
		return scrollBarX;
	}
	@Override
	public int getRowWidth() {
		return configScreen.width;
	}
	@Override
	public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
		for (AbstractGuiConfigEntry entry : children()) {
			//不要？
			//entry.keyPressed(key, p_231046_2_, p_231046_3_);
		}
		return super.keyPressed(key, p_231046_2_, p_231046_3_);
	}

	public void onClose() { }

	public boolean areAnyEntriesEnabled(boolean includeChildren) {
		for (AbstractGuiConfigEntry entry : children()) {
			if ((includeChildren || !(entry instanceof CategoryEntry)) && entry.enabled())
				return true;
		}
		return true;
	}
	public boolean hasChangedEntry(boolean includeChildren) {
		for (AbstractGuiConfigEntry entry : children()) {
			if ((includeChildren || !(entry instanceof CategoryEntry)) && entry.isChanged())
				return true;
		}
		return false;
	}
	public boolean areAllEntriesDefault(boolean includeChildren) {
		for (AbstractGuiConfigEntry entry : children()) {
			if ((includeChildren || !(entry instanceof CategoryEntry)) && !entry.isDefault())
				return false;
		}

		return true;
	}
	public boolean saveConfigElements() {
		boolean requiresRestart = false;
		for (AbstractGuiConfigEntry entry : children()) {
			if (entry.saveConfigElement())
				requiresRestart = true;
		}

		return requiresRestart;
	}
	public void setAllToDefault(boolean includeChildren) {
		for (AbstractGuiConfigEntry entry : children()) {
			if ((includeChildren || !(entry instanceof CategoryEntry)))
				entry.setToDefault();
		}
	}
	public void undoAllChanges(boolean includeChildren) {
		for (AbstractGuiConfigEntry entry : children()) {
			if ((includeChildren || !(entry instanceof CategoryEntry)))
				entry.undoChanges();
		}
	}


	public static class BooleanEntry extends CycleValueEntry {

		public BooleanEntry(ConfigScreen owningScreen, ConfigEntries owningEntryList, BooleanValue property) {
			super(owningScreen, owningEntryList, property, new String[]{"true", "false"}, property.get() ? 0 : 1, (boolean) ConfigUtil.getDefault(owningScreen.forgeConfigSpec, property) ? 0 : 1);
		}
		@Override
		public boolean saveConfigElement() {
			if (enabled() && isChanged()) {
				setValue(currentIndex == 0);
				return ConfigUtil.requiresMcRestart(configValue);
			}
			return false;
		}

	}

	public static class EnumEntry extends CycleValueEntry {
		private final Class<? extends Enum<?>> enumClass;

		public EnumEntry(ConfigScreen owningScreen, ConfigEntries owningEntryList, EnumValue<?> property) {
			super(owningScreen, owningEntryList, property, getValueTexts(property.get()), getCurrentIndex(property.get()), getDefaultIndex(property.get(), (Enum<?>) ConfigUtil.getDefault(owningScreen.forgeConfigSpec, property)));
			enumClass = (Class<? extends Enum<?>>) property.get().getClass();
		}
		@Override
		public boolean saveConfigElement() {
			if (enabled() && isChanged()) {
				setValue(enumClass.getEnumConstants()[currentIndex]);
				return ConfigUtil.requiresMcRestart(configValue);
			}
			return false;
		}

		private static String[] getValueTexts(Enum<?> current) {
			return Arrays.stream(current.getClass().getEnumConstants()).map(Enum::toString).toArray(String[]::new);
		}
		private static int getCurrentIndex(Enum<?> current) {
			return ArrayUtils.indexOf(current.getClass().getEnumConstants(), current);
		}
		private static int getDefaultIndex(Enum<?> current, Enum<?> defaultValue) {
			return ArrayUtils.indexOf(current.getClass().getEnumConstants(), defaultValue);
		}

	}

	public static abstract class CycleValueEntry extends ButtonEntry {
		protected final String[] valueTexts;
		protected final int beforeIndex;
		protected final int defaultIndex;
		protected int currentIndex;
		public CycleValueEntry(ConfigScreen owningScreen, ConfigEntries owningEntryList, ConfigValue<?> property, String[] valueTexts, int currentIndex, int defaultIndex) {
			super(owningScreen, owningEntryList, property);
			this.valueTexts = valueTexts;
			beforeIndex = currentIndex;
			this.currentIndex = currentIndex;
			this.defaultIndex = defaultIndex;
			btnValue.active = enabled();
			updateValueButtonText();
		}

		protected void updateValueButtonText() {
			btnValue.setMessage(new StringTextComponent(valueTexts[currentIndex]));
		}

		@Override
		public void valueButtonPressed() {
			if (enabled()) {
				if (++currentIndex >= valueTexts.length)
					currentIndex = 0;
				updateValueButtonText();
			}
		}

		@Override
		public boolean isDefault() {
			return currentIndex == defaultIndex;
		}

		@Override
		public void setToDefault() {
			if (enabled()) {
				currentIndex = defaultIndex;
				updateValueButtonText();
			}
		}

		@Override
		public boolean isChanged() {
			return currentIndex != beforeIndex;
		}

		@Override
		public void undoChanges() {
			if (enabled()) {
				currentIndex = beforeIndex;
				updateValueButtonText();
			}
		}

	}

	public static abstract class ButtonEntry extends PropertyEntry {
		protected final ExtendedButton btnValue;

		public ButtonEntry(ConfigScreen owningScreen, ConfigEntries owningEntryList, ConfigValue<?> property) {
			super(owningScreen, owningEntryList, property);
			btnValue = new ExtendedButton(owningEntryList.controlX, 0, owningEntryList.controlWidth, 18, new StringTextComponent(ListUtil.getLast(property.getPath())), btn -> valueButtonPressed());
		}

		protected abstract void valueButtonPressed();

		@Override
		public void render(MatrixStack matrixStack, int slotIndex, int top, int left, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial) {
			super.render(matrixStack, slotIndex, top, left, listWidth, slotHeight, mouseX, mouseY, isSelected, partial);
			btnValue.setWidth(owningEntryList.controlWidth);
			btnValue.x = owningEntryList.controlX;
			btnValue.y = top;
			btnValue.active = enabled();
			btnValue.render(matrixStack, mouseX, mouseY, partial);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			if (btnValue.mouseClicked(mouseX, mouseY, mouseButton)) {
				return true;
			} else
				return super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
			return btnValue.mouseReleased(mouseX, mouseY, mouseButton);
		}

	}

	public static class IntEntry extends InputEntry {
		protected final int beforeValueInt;

		public IntEntry(ConfigScreen configScreen, ConfigEntries owningEntryList, IntValue configValue) {
			super(configScreen, owningEntryList, configValue);
			beforeValueInt = configValue.get();
		}

		@Override
		public boolean isChanged() {
			try {
				return beforeValueInt != Integer.parseInt(textFieldValue.getValue().trim());
			} catch (Throwable e) {
				return true;
			}
		}
	}

	public static class LongEntry extends InputEntry {
		protected final long beforeValueLong;

		public LongEntry(ConfigScreen configScreen, ConfigEntries owningEntryList, LongValue configValue) {
			super(configScreen, owningEntryList, configValue);
			beforeValueLong = configValue.get();
		}

		@Override
		public boolean isChanged() {
			try {
				return beforeValueLong != Long.parseLong(textFieldValue.getValue().trim());
			} catch (Throwable e) {
				return true;
			}
		}
	}

	public static class DoubleEntry extends InputEntry {
		protected final double beforeValueDouble;

		public DoubleEntry(ConfigScreen configScreen, ConfigEntries owningEntryList, DoubleValue configValue) {
			super(configScreen, owningEntryList, configValue);
			beforeValueDouble = configValue.get();
		}

		@Override
		public boolean isChanged() {
			try {
				return beforeValueDouble != Double.parseDouble(textFieldValue.getValue().trim());
			} catch (Throwable e) {
				return true;
			}
		}
	}

	public static abstract class InputEntry extends PropertyEntry {
		protected final TextFieldWidget textFieldValue;
		protected final String beforeValueString;
		protected final boolean isBeforeValueDefault;
		public InputEntry(ConfigScreen configScreen, ConfigEntries owningEntryList, ConfigValue<?> configValue) {
			super(configScreen, owningEntryList, configValue);
			beforeValueString = configValue.get().toString();
			isBeforeValueDefault = isDefault();
			textFieldValue = new TextFieldWidget(mc.font, owningEntryList.controlX + 1, 0, owningEntryList.controlWidth - 3, 16, new StringTextComponent(""));
			textFieldValue.setMaxLength(10000);
			textFieldValue.setValue(beforeValueString);
		}

		@Override
		public void render(MatrixStack matrixStack, int slotIndex, int top, int left, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial) {
			super.render(matrixStack, slotIndex, top, left, listWidth, slotHeight, mouseX, mouseY, isSelected, partial);
			textFieldValue.setWidth(owningEntryList.controlWidth - 4);
			textFieldValue.x = owningEntryList.controlX + 2;
			textFieldValue.y = top + 1;
			textFieldValue.active = enabled();
			textFieldValue.render(matrixStack, mouseX, mouseY, partial);
		}

		@Override
		public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
			boolean b = textFieldValue.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
			isValidValue = ConfigUtil.isValid(configValue, textFieldValue.getValue().trim());
			return b;
		}

		@Override
		public boolean charTyped(char p_231042_1_, int p_231042_2_) {
			boolean b = textFieldValue.charTyped(p_231042_1_, p_231042_2_);
			isValidValue = ConfigUtil.isValid(configValue, textFieldValue.getValue().trim());
			return b;
		}

		@Override
		public void tick() {
			textFieldValue.tick();
		}
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			if (textFieldValue.mouseClicked(mouseX, mouseY, mouseButton))
				return true;
			return super.mouseClicked(mouseX, mouseY, mouseButton);
		}
		@Override
		public boolean isDefault() {
			return ConfigUtil.getDefault(configScreen.forgeConfigSpec, configValue) == configValue.get();
		}

		@Override
		public void setToDefault() {
			if (enabled()) {
				textFieldValue.setValue(ConfigUtil.getDefault(configScreen.forgeConfigSpec, configValue).toString());
				isValidValue = ConfigUtil.isValid(configValue, textFieldValue.getValue().trim());
				textFieldValue.moveCursorToStart();
			}
		}

		@Override
		public void undoChanges() {
			if (enabled()) {
				textFieldValue.setValue(beforeValueString);
				isValidValue = ConfigUtil.isValid(configValue, textFieldValue.getValue().trim());
			}
		}

		@Override
		public boolean saveConfigElement() {
			if (enabled() && isChanged()) {
				if (ConfigUtil.isValid(configValue, textFieldValue.getValue())) {
					setValue(ConfigUtil.parse(configValue, textFieldValue.getValue()));
					return ConfigUtil.requiresMcRestart(configValue);
				} else {
					setValue(ConfigUtil.getDefault(configScreen.forgeConfigSpec, configValue));
					return ConfigUtil.requiresMcRestart(configValue) && isBeforeValueDefault;
				}
			}
			return false;
		}

	}

	public static abstract class PropertyEntry extends ListEntryBase {
		protected final ConfigValue<?> configValue;
		protected final @Nullable String comment;

		public PropertyEntry(ConfigScreen configScreen, ConfigEntries owningEntryList, ConfigValue<?> configValue) {
			super(configScreen, owningEntryList, ConfigUtil.getTranslatedText(configScreen.forgeConfigSpec, configValue));
			this.configValue = configValue;
			comment = ConfigUtil.getComment(configScreen.forgeConfigSpec, configValue);

			toolTip.add(name.copy().withStyle(TextFormatting.GREEN));
			String tooltip = I18n.get("config." + ModReference.MOD_ID + "." + StringUtils.join(configValue.getPath(), '.') + ".tooltip").replace("\\n", "\n");
			if (I18n.exists(tooltip))
				toolTip.addAll(TextComponentUtil.splitLines(new StringTextComponent(tooltip).withStyle(TextFormatting.YELLOW)));
			else if (comment != null)
				toolTip.addAll(TextComponentUtil.splitLines(new StringTextComponent(comment.trim()).withStyle(TextFormatting.YELLOW)));
			else
				toolTip.add(new StringTextComponent("No tooltip defined.").withStyle(TextFormatting.RED));

			if (ConfigUtil.requiresWorldRestart(configScreen.forgeConfigSpec, configValue) || configScreen.allRequireMcRestart)
				toolTip.add(new StringTextComponent("[" + I18n.get("gui.config.gameRestartTitle") + "]").withStyle(TextFormatting.RED));
		}

		@Override
		public boolean enabled() {
			if (configScreen.isWorldRunning)
				return !configScreen.allRequireWorldRestart && !ConfigUtil.requiresWorldRestart(configScreen.forgeConfigSpec, configValue);
			else
				return true;
		}

		@Override
		public boolean requiresMcRestart() {
			return ConfigUtil.requiresMcRestart(configValue);
		}
		@Override
		public boolean requiresWorldRestart() {
			return ConfigUtil.requiresWorldRestart(configScreen.forgeConfigSpec, configValue);
		}

		protected <E> void setValue(Object value) {
			setValue(configValue, value);
		}

		private static <E> void setValue(ConfigValue<E> configValue, Object value) {
			configValue.set((E) value);
		}
	}

	static class CategoryEntry extends ListEntryBase {
		protected Screen childScreen;
		protected final ForgeConfigSpec configSpec;
		protected final List<String> categoryPath;
		protected final ExtendedButton btnSelectCategory;
		protected final @Nullable String comment;

		public CategoryEntry(ConfigScreen owningScreen, ConfigEntries owningEntryList, ForgeConfigSpec configSpec, List<String> categoryPath) {
			super(owningScreen, owningEntryList, new TranslationTextComponent(ConfigUtil.getTranslationKey(configSpec, categoryPath)));
			this.configSpec = configSpec;
			this.categoryPath = categoryPath;
			comment = ConfigUtil.getComment(configSpec, categoryPath);

			childScreen = buildChildScreen();

			btnSelectCategory = new ExtendedButton(0, 0, 300, 18, name, btn -> {
				mc.setScreen(childScreen);
			});
			tooltipHoverChecker = new HoverChecker(btnSelectCategory, 800);

			drawLabel = false;

			toolTip.add(name.copy().withStyle(TextFormatting.GREEN));
			String tooltip = I18n.get("config." + ModReference.MOD_ID + "." + StringUtils.join(categoryPath, '.') + ".tooltip").replace("\\n", "\n");
			if (I18n.exists(tooltip))
				toolTip.add(new StringTextComponent(tooltip).withStyle(TextFormatting.YELLOW));
			else if (comment != null)
				toolTip.add(new StringTextComponent(comment.trim()).withStyle(TextFormatting.YELLOW));
			else
				toolTip.add(new StringTextComponent("No tooltip defined.").withStyle(TextFormatting.RED));

			if (ConfigUtil.requiresWorldRestart(configScreen.forgeConfigSpec, categoryPath) || owningScreen.allRequireMcRestart)
				toolTip.add(new StringTextComponent("[" + I18n.get("gui.config.gameRestartTitle") + "]").withStyle(TextFormatting.RED));

		}

		protected Screen buildChildScreen() {
			ITextComponent iTextComponent;
			if (configScreen.titleLine2 == null)
				iTextComponent = name;
			else
				iTextComponent = configScreen.titleLine2.copy().append(" > ").append(name);
			return new ConfigScreen(configScreen, configScreen.forgeConfigSpec, categoryPath, configScreen.configID,
					configScreen.allRequireWorldRestart || ConfigUtil.requiresWorldRestart(configSpec, categoryPath),
					configScreen.allRequireMcRestart || ConfigUtil.requiresMcRestart(configSpec, categoryPath), configScreen.getTitle(),
					iTextComponent);
		}

		@Override
		public void render(MatrixStack matrixStack, int slotIndex, int top, int left, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial) {
			btnSelectCategory.x = listWidth / 2 - 150;
			btnSelectCategory.y = top;
			btnSelectCategory.active = enabled();
			btnSelectCategory.render(matrixStack, mouseX, mouseY, partial);
			super.render(matrixStack, slotIndex, top, left, listWidth, slotHeight, mouseX, mouseY, isSelected, partial);
		}

		@Override
		public void drawToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
			boolean canHover = mouseY < owningEntryList.y1 && mouseY > owningEntryList.y0;

			if (tooltipHoverChecker.checkHover(mouseX, mouseY, canHover))
				configScreen.drawToolTip(matrixStack, toolTip, mouseX, mouseY);

			super.drawToolTip(matrixStack, mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			if (btnSelectCategory.mouseClicked(mouseX, mouseY, mouseButton)) {
				return true;
			} else
				return super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
			return btnSelectCategory.mouseReleased(mouseX, mouseY, mouseButton);
		}

		@Override
		public boolean isDefault() {
			if (childScreen instanceof ConfigScreen && ((ConfigScreen) childScreen).entryList != null)
				return ((ConfigScreen) childScreen).entryList.areAllEntriesDefault(true);

			return true;
		}

		@Override
		public void setToDefault() {
			if (childScreen instanceof ConfigScreen && ((ConfigScreen) childScreen).entryList != null)
				((ConfigScreen) childScreen).entryList.setAllToDefault(true);
		}

		@Override
		public boolean saveConfigElement() {
			boolean requiresRestart = false;

			if (childScreen instanceof ConfigScreen && ((ConfigScreen) childScreen).entryList != null) {
				requiresRestart = ConfigUtil.requiresMcRestart(configSpec, categoryPath) && ((ConfigScreen) childScreen).entryList.hasChangedEntry(true);

				if (((ConfigScreen) childScreen).entryList.saveConfigElements())
					requiresRestart = true;
			}

			return requiresRestart;
		}

		@Override
		public boolean isChanged() {
			if (childScreen instanceof ConfigScreen && ((ConfigScreen) childScreen).entryList != null)
				return ((ConfigScreen) childScreen).entryList.hasChangedEntry(true);
			else
				return false;
		}

		@Override
		public void undoChanges() {
			if (childScreen instanceof ConfigScreen && ((ConfigScreen) childScreen).entryList != null)
				((ConfigScreen) childScreen).entryList.undoAllChanges(true);
		}

		@Override
		public boolean enabled() {
			return true;
		}

		@Override
		public int getLabelWidth() {
			return 0;
		}

		@Override
		public int getEntryRightBound() {
			return owningEntryList.width / 2 + 155 + 22 + 18;
		}

		@Override
		public boolean requiresWorldRestart() {
			return ConfigUtil.requiresWorldRestart(configSpec, categoryPath);
		}
		@Override
		public boolean requiresMcRestart() {
			return ConfigUtil.requiresMcRestart(configSpec, categoryPath);
		}

	}

	static abstract class ListEntryBase extends AbstractGuiConfigEntry {
		protected final ConfigScreen configScreen;
		protected final ConfigEntries owningEntryList;
		protected final Minecraft mc;
		protected final ITextComponent name;
		protected final ExtendedButton btnUndoChanges;
		protected final ExtendedButton btnDefault;
		protected List<ITextProperties> toolTip;
		protected List<ITextProperties> undoToolTip;
		protected List<ITextProperties> defaultToolTip;
		protected boolean isValidValue = true;
		protected HoverChecker tooltipHoverChecker;
		protected HoverChecker undoHoverChecker;
		protected HoverChecker defaultHoverChecker;
		protected boolean drawLabel;

		public ListEntryBase(ConfigScreen configScreen, ConfigEntries owningEntryList, ITextComponent name) {
			this.configScreen = configScreen;
			this.owningEntryList = owningEntryList;
			mc = Minecraft.getInstance();
			this.name = name;
			btnUndoChanges = new ExtendedButton(0, 0, 18, 18, new StringTextComponent(ConfigScreen.UNDO_CHAR), btn -> {
				undoChanges();
			});
			btnDefault = new ExtendedButton(0, 0, 18, 18, new StringTextComponent(ConfigScreen.RESET_CHAR), btn -> {
				setToDefault();
			});

			undoHoverChecker = new HoverChecker(btnUndoChanges, 800);
			defaultHoverChecker = new HoverChecker(btnDefault, 800);
			undoToolTip = Collections.singletonList(new TranslationTextComponent("gui.config.tooltip.undoChanges"));
			defaultToolTip = Collections.singletonList(new TranslationTextComponent("gui.config.tooltip.resetToDefault"));
			toolTip = new ArrayList<>();

			drawLabel = true;
		}

		@Override
		public void render(MatrixStack matrixStack, int slotIndex, int top, int left, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial) {
			boolean isChanged = isChanged();

			if (drawLabel) {
				IFormattableTextComponent label = name.copy();
				label.withStyle((!isValidValue ? TextFormatting.RED : (isChanged ? TextFormatting.WHITE : TextFormatting.GRAY)));
				if (isChanged)
					label.withStyle(TextFormatting.ITALIC);
				mc.font.draw(matrixStack,
						label,
						owningEntryList.labelX,
						top + (float) slotHeight / 2 - (float) mc.font.lineHeight / 2,
						0xFFFFFF);
			}

			btnUndoChanges.x = owningEntryList.scrollBarX - 44;
			btnUndoChanges.y = top;
			btnUndoChanges.active = enabled() && isChanged;
			btnUndoChanges.render(matrixStack, mouseX, mouseY, partial);

			btnDefault.x = owningEntryList.scrollBarX - 22;
			btnDefault.y = top;
			btnDefault.active = enabled() && !isDefault();
			btnDefault.render(matrixStack, mouseX, mouseY, partial);

			if (tooltipHoverChecker == null)
				tooltipHoverChecker = new HoverChecker(top, top + slotHeight, left, owningEntryList.resetX - 5, 800);
			else
				tooltipHoverChecker.updateBounds(top, top + slotHeight, left, owningEntryList.resetX - 5);
		}

		@Override
		public void drawToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {

			boolean canHover = mouseY < owningEntryList.y1 && mouseY > owningEntryList.y0;
			if (toolTip != null && tooltipHoverChecker != null) {
				if (tooltipHoverChecker.checkHover(mouseX, mouseY, canHover))
					configScreen.drawToolTip(matrixStack, toolTip, mouseX, mouseY);
			}

			if (undoHoverChecker.checkHover(mouseX, mouseY, canHover))
				configScreen.drawToolTip(matrixStack, undoToolTip, mouseX, mouseY);

			if (defaultHoverChecker.checkHover(mouseX, mouseY, canHover))
				configScreen.drawToolTip(matrixStack, defaultToolTip, mouseX, mouseY);
		}

		@Override
		public void tick() {

		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			if (btnDefault.mouseClicked(mouseX, mouseY, mouseButton)) {
				return true;
			} else if (btnUndoChanges.mouseClicked(mouseX, mouseY, mouseButton)) {
				return true;
			}
			return false;
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
			boolean res = btnDefault.mouseReleased(mouseX, mouseY, mouseButton);
			res |= btnUndoChanges.mouseReleased(mouseX, mouseY, mouseButton);
			return res;
		}

		@Override
		public int getLabelWidth() {
			return mc.font.width(name);
		}

		@Override
		public int getEntryRightBound() {
			return owningEntryList.resetX + 40;
		}


	}

	static abstract class AbstractGuiConfigEntry extends AbstractList.AbstractListEntry<AbstractGuiConfigEntry> {

		public static AbstractGuiConfigEntry toEntry(String name, Object element, ConfigEntries configEntries) {
			List<String> configPath = new ArrayList<>(configEntries.configScreen.categoryPath);
			if (element instanceof Config) {
				configPath.add(name);
				return new CategoryEntry(configEntries.configScreen, configEntries, configEntries.configScreen.forgeConfigSpec, configPath);
			} else if (element instanceof ConfigValue<?>) {
				if (element instanceof EnumValue<?>) {
					return new EnumEntry(configEntries.configScreen, configEntries, (EnumValue<?>) element);
				} else if (element instanceof IntValue) {
					return new IntEntry(configEntries.configScreen, configEntries, (IntValue) element);
				}
				throw new NotImplementedException("");
			} else {
				throw new IllegalArgumentException("Invalid element:" + element.getClass());
			}
		}

		public abstract boolean saveConfigElement();
		public abstract boolean enabled();
		public abstract boolean isChanged();
		public abstract boolean isDefault();
		public abstract void setToDefault();
		public abstract void undoChanges();
		public abstract int getLabelWidth();
		public abstract int getEntryRightBound();
		public abstract void drawToolTip(MatrixStack matrixStack, int mouseX, int mouseY);
		public abstract void tick();
		public abstract boolean requiresWorldRestart();
		public abstract boolean requiresMcRestart();
	}

}
