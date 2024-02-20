package kpan.b_line_break.config.core.properties;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kpan.b_line_break.config.core.gui.ModGuiConfig;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries.IGuiConfigEntry;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries.IntegerEntry;

public class ConfigPropertyInt extends AbstractConfigProperty {

    public static final String TYPE = "I";

    private final int defaultValue;
    private final int minValue;
    private final int maxValue;
    private int value;
    private boolean hasSlidingControl = false;

    public ConfigPropertyInt(String id, int defaultValue, int minValue, int maxValue, String commentForFile, int order) {
        super(id, commentForFile, order);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        value = defaultValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        dirty = true;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public boolean readValue(String value) {
        try {
            int i = Integer.parseInt(value);
            if (i < minValue || i > maxValue)
                return false;
            this.value = Integer.parseInt(value);
            dirty = true;
            return true;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    @Override
    public String getAdditionalComment() {
        if (minValue == Integer.MIN_VALUE) {
            if (maxValue == Integer.MAX_VALUE)
                return "Default: " + defaultValue;
            else
                return "Range: ~ " + maxValue + "\nDefault: " + defaultValue;
        } else {
            if (maxValue == Integer.MAX_VALUE)
                return "Range: " + minValue + " ~" + "\nDefault: " + defaultValue;
            else
                return "Range: " + minValue + " ~ " + maxValue + "\ndefault: " + defaultValue;
        }
    }

    @Override
    public String getTypeString() {
        return TYPE;
    }

    @Override
    public String getValueString() {
        return value + "";
    }

    @Override
    public String getDefaultValueString() {
        return defaultValue + "";
    }

    @Override
    public boolean isDefault() {
        return value == defaultValue;
    }

    @Override
    public void setToDefault() {
        value = defaultValue;
    }

    @Override
    public boolean isValidValue(String str) {
        try {
            int value = Integer.parseInt(str);
            return value >= minValue && value <= maxValue;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IGuiConfigEntry toEntry(ModGuiConfig screen, ModGuiConfigEntries entryList) {
        return new IntegerEntry(screen, entryList, this);
    }

    public boolean hasSlidingControl() {
        return hasSlidingControl;
    }

    public void setHasSlidingControl(boolean hasSlidingControl) {
        this.hasSlidingControl = hasSlidingControl;
    }
}
