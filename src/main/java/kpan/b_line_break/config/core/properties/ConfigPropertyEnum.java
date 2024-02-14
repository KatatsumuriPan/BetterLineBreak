package kpan.b_line_break.config.core.properties;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kpan.b_line_break.config.core.gui.ModGuiConfig;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries.EnumEntry;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries.IGuiConfigEntry;
import org.apache.commons.lang3.StringUtils;

public class ConfigPropertyEnum extends AbstractConfigProperty {

    public static final String TYPE = "E";

    private final Class<? extends Enum<?>> enumClass;
    private final Enum<?> defaultValue;
    private Enum<?> value;

    public ConfigPropertyEnum(String id, Enum<?> defaultValue, int order) {
        super(id, order);
        enumClass = (Class<? extends Enum<?>>) defaultValue.getClass();
        this.defaultValue = defaultValue;
        value = defaultValue;
    }

    public Enum<?> getValue() {
        return value;
    }

    public void setValue(Enum<?> value) {
        if (value.getClass() != enumClass)
            throw new IllegalArgumentException("value is not member of " + enumClass.toString());
        this.value = value;
        dirty = true;
    }

    public Enum<?> getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean readValue(String value) {
        for (Enum<?> e : enumClass.getEnumConstants()) {
            if (e.toString().equalsIgnoreCase(value)) {
                this.value = e;
                return true;
            }
        }
        return false;
    }

    @Override
    public String getAdditionalComment() {
        return "Possible values: [" + StringUtils.join(enumClass.getEnumConstants(), ", ") + "]\nDefault: " + defaultValue;
    }

    @Override
    public String getTypeString() {
        return TYPE;
    }

    @Override
    public String getValueString() {
        return value.toString();
    }

    @Override
    public String getDefaultValueString() {
        return defaultValue.toString();
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
        for (Enum<?> e : enumClass.getEnumConstants()) {
            if (e.toString().equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IGuiConfigEntry toEntry(ModGuiConfig screen, ModGuiConfigEntries entryList) {
        return new EnumEntry(screen, entryList, this, value, defaultValue);
    }
}
