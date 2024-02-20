package kpan.b_line_break.config.core.properties;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kpan.b_line_break.config.core.gui.ModGuiConfig;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries.IGuiConfigEntry;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries.LongEntry;

public class ConfigPropertyLong extends AbstractConfigProperty {

    public static final String TYPE = "L";

    private final long defaultValue;
    private final long minValue;
    private final long maxValue;
    private long value;
    private boolean hasSlidingControl = false;

    public ConfigPropertyLong(String id, long defaultValue, long minValue, long maxValue, String commentForFile, int order) {
        super(id, commentForFile, order);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        value = defaultValue;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
        dirty = true;
    }

    public long getMinValue() {
        return minValue;
    }

    public long getMaxValue() {
        return maxValue;
    }

    @Override
    public boolean readValue(String value) {
        try {
            long i = Long.parseLong(value);
            if (i < minValue || i > maxValue)
                return false;
            this.value = Long.parseLong(value);
            dirty = true;
            return true;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    @Override
    public String getAdditionalComment() {
        if (minValue == Long.MIN_VALUE) {
            if (maxValue == Long.MAX_VALUE)
                return "Default: " + defaultValue;
            else
                return "Range: ~ " + maxValue + "\nDefault: " + defaultValue;
        } else {
            if (maxValue == Long.MAX_VALUE)
                return "Range: " + minValue + " ~" + "\nDefault: " + defaultValue;
            else
                return "Range: " + minValue + " ~ " + maxValue + "\nDefault: " + defaultValue;
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
            long value = Long.parseLong(str);
            return value >= minValue && value <= maxValue;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IGuiConfigEntry toEntry(ModGuiConfig screen, ModGuiConfigEntries entryList) {
        return new LongEntry(screen, entryList, this);
    }

    public boolean hasSlidingControl() {
        return hasSlidingControl;
    }

    public void setHasSlidingControl(boolean hasSlidingControl) {
        this.hasSlidingControl = hasSlidingControl;
    }
}
