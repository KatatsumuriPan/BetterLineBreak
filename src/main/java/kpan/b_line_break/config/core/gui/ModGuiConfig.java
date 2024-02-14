package kpan.b_line_break.config.core.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiMessageDialog;
import cpw.mods.fml.client.config.GuiUnicodeGlyphButton;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.Event.Result;
import kpan.b_line_break.ModMain;
import kpan.b_line_break.ModReference;
import kpan.b_line_break.config.core.IConfigElement;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries.IGuiConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static cpw.mods.fml.client.config.GuiUtils.RESET_CHAR;
import static cpw.mods.fml.client.config.GuiUtils.UNDO_CHAR;


public class ModGuiConfig extends GuiScreen {

    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    public final GuiScreen parentScreen;
    public String title = "Config GUI";
    public final String path;
    public final List<IConfigElement> configElements;
    public ModGuiConfigEntries entryList;
    protected GuiButtonExt btnDefaultAll;
    protected GuiButtonExt btnUndoAll;
    protected GuiCheckBox chkApplyGlobally;
    /**
     * When set to a non-null value the OnConfigChanged and PostConfigChanged events will be posted when the Done button is pressed
     * if any configElements were changed (includes child screens). If not defined, the events will be posted if the parent gui is null
     * or if the parent gui is not an instance of GuiConfig.
     */
    @Nullable
    public final String configID;
    public final boolean isWorldRunning;
    public final boolean allRequireWorldRestart;
    public final boolean allRequireMcRestart;
    public boolean needsRefresh = true;
    protected HoverChecker undoHoverChecker;
    protected HoverChecker resetHoverChecker;
    protected HoverChecker checkBoxHoverChecker;

    public ModGuiConfig(GuiScreen owningScreen) {
        this(owningScreen, ModMain.defaultConfig.getRootCategory().getOrderedElements(), null, false, false, ModReference.MODNAME, "");
    }

    public ModGuiConfig(GuiScreen owningScreen, List<IConfigElement> configElements, @Nullable String configID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title, String path) {
        mc = Minecraft.getMinecraft();
        parentScreen = owningScreen;
        this.configElements = configElements;
        this.path = path;
        entryList = new ModGuiConfigEntries(this, mc);
        this.allRequireWorldRestart = allRequireWorldRestart || entryList.listEntries.stream().allMatch(IGuiConfigEntry::requiresWorldRestart);
        this.allRequireMcRestart = allRequireMcRestart || entryList.listEntries.stream().allMatch(IGuiConfigEntry::requiresMcRestart);
        this.configID = configID;
        isWorldRunning = mc.theWorld != null;
        if (title != null)
            this.title = title;
    }


    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        if (entryList == null || needsRefresh) {
            entryList = new ModGuiConfigEntries(this, mc);
            needsRefresh = false;
        }

        int undoGlyphWidth = mc.fontRenderer.getStringWidth(UNDO_CHAR) * 2;
        int resetGlyphWidth = mc.fontRenderer.getStringWidth(RESET_CHAR) * 2;
        int doneWidth = Math.max(mc.fontRenderer.getStringWidth(I18n.format("gui.done")) + 20, 100);
        int undoWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.undoChanges")) + undoGlyphWidth + 20;
        int resetWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.resetToDefault")) + resetGlyphWidth + 20;
        int checkWidth = mc.fontRenderer.getStringWidth(I18n.format("fml.configgui.applyGlobally")) + 13;
        int buttonWidthHalf = (doneWidth + 5 + undoWidth + 5 + resetWidth + 5 + checkWidth) / 2;
        buttonList.add(new GuiButtonExt(2000, width / 2 - buttonWidthHalf, height - 29, doneWidth, 20, I18n.format("gui.done")));
        buttonList.add(btnDefaultAll = new GuiUnicodeGlyphButton(2001, width / 2 - buttonWidthHalf + doneWidth + 5 + undoWidth + 5,
                height - 29, resetWidth, 20, " " + I18n.format("fml.configgui.tooltip.resetToDefault"), RESET_CHAR, 2.0F));
        buttonList.add(btnUndoAll = new GuiUnicodeGlyphButton(2002, width / 2 - buttonWidthHalf + doneWidth + 5,
                height - 29, undoWidth, 20, " " + I18n.format("fml.configgui.tooltip.undoChanges"), UNDO_CHAR, 2.0F));
        buttonList.add(chkApplyGlobally = new GuiCheckBox(2003, width / 2 - buttonWidthHalf + doneWidth + 5 + undoWidth + 5 + resetWidth + 5,
                height - 24, I18n.format("fml.configgui.applyGlobally"), false));

        undoHoverChecker = new HoverChecker(btnUndoAll, 800);
        resetHoverChecker = new HoverChecker(btnDefaultAll, 800);
        checkBoxHoverChecker = new HoverChecker(chkApplyGlobally, 800);
        entryList.initGui();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed() {
        entryList.onGuiClosed();

        if (configID != null && parentScreen instanceof ModGuiConfig parentGuiConfig) {
            parentGuiConfig.needsRefresh = true;
            parentGuiConfig.initGui();
        }

        if (!(parentScreen instanceof ModGuiConfig))
            Keyboard.enableRepeatEvents(false);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 2000) {
            boolean flag = true;
            try {
                if ((configID != null || parentScreen == null || !(parentScreen instanceof ModGuiConfig))
                        && (entryList.hasChangedEntry(true))) {
                    boolean requiresMcRestart = entryList.saveConfigElements();

                    ConfigChangedEvent event = new OnConfigChangedEvent(ModReference.MODID, configID, isWorldRunning, requiresMcRestart);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (!event.getResult().equals(Result.DENY)) {
                        ModMain.defaultConfig.syncToFieldAndSave();
                        MinecraftForge.EVENT_BUS.post(new PostConfigChangedEvent(ModReference.MODID, configID, isWorldRunning, requiresMcRestart));
                    }

                    if (requiresMcRestart) {
                        flag = false;
                        mc.displayGuiScreen(new GuiMessageDialog(parentScreen, "fml.configgui.gameRestartTitle",
                                new ChatComponentText(I18n.format("fml.configgui.gameRestartRequired")), "fml.configgui.confirmRestartMessage"));
                    }

                    if (parentScreen instanceof ModGuiConfig)
                        ((ModGuiConfig) parentScreen).needsRefresh = true;
                }
            } catch (Throwable e) {
                FMLLog.getLogger().error("Error performing GuiConfig action:", e);
            }

            if (flag)
                mc.displayGuiScreen(parentScreen);
        } else if (button.id == 2001) {
            entryList.setAllToDefault(chkApplyGlobally.isChecked());
        } else if (button.id == 2002) {
            entryList.undoAllChanges(chkApplyGlobally.isChecked());
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    protected void mouseClicked(int x, int y, int mouseEvent) {
        if (mouseEvent != 0 || !entryList.func_148179_a(x, y, mouseEvent)) {
            entryList.mouseClickedPassThru(x, y, mouseEvent);
            super.mouseClicked(x, y, mouseEvent);
        }
    }

    /**
     * Called when a mouse button is released.
     */
    @Override
    protected void mouseMovedOrUp(int x, int y, int mouseEvent) {
        if (mouseEvent != 0 || !entryList.func_148181_b(x, y, mouseEvent)) {
            super.mouseMovedOrUp(x, y, mouseEvent);
        }
    }


    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    @Override
    protected void keyTyped(char eventChar, int eventKey) {
        if (eventKey == Keyboard.KEY_ESCAPE)
            mc.displayGuiScreen(parentScreen);
        else
            entryList.keyTyped(eventChar, eventKey);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen() {
        super.updateScreen();
        entryList.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        entryList.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRendererObj, title, width / 2, 8, 16777215);
        String title2 = path.replace(".", " > ");

        if (title2 != null) {
            int strWidth = mc.fontRenderer.getStringWidth(title2);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
            if (strWidth > width - 6 && strWidth > ellipsisWidth)
                title2 = mc.fontRenderer.trimStringToWidth(title2, width - 6 - ellipsisWidth).trim() + "...";
            drawCenteredString(fontRendererObj, title2, width / 2, 18, 16777215);
        }

        btnUndoAll.enabled = entryList.areAnyEntriesEnabled(chkApplyGlobally.isChecked()) && entryList.hasChangedEntry(chkApplyGlobally.isChecked());
        btnDefaultAll.enabled = entryList.areAnyEntriesEnabled(chkApplyGlobally.isChecked()) && !entryList.areAllEntriesDefault(chkApplyGlobally.isChecked());
        super.drawScreen(mouseX, mouseY, partialTicks);
        entryList.drawScreenPost(mouseX, mouseY, partialTicks);
        if (undoHoverChecker.checkHover(mouseX, mouseY))
            drawToolTip(Arrays.asList(I18n.format("fml.configgui.tooltip.undoAll").split("\n")), mouseX, mouseY);
        if (resetHoverChecker.checkHover(mouseX, mouseY))
            drawToolTip(Arrays.asList(I18n.format("fml.configgui.tooltip.resetAll").split("\n")), mouseX, mouseY);
        if (checkBoxHoverChecker.checkHover(mouseX, mouseY))
            drawToolTip(Arrays.asList(I18n.format("fml.configgui.tooltip.applyGlobally").split("\n")), mouseX, mouseY);
    }

    public void drawToolTip(List<String> stringList, int x, int y) {
        drawHoveringText(stringList, x, y, fontRendererObj);
    }
}
