package kpan.b_line_break.config.core;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kpan.b_line_break.ModReference;
import kpan.b_line_break.config.core.gui.ModGuiConfig;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries.CategoryEntry;
import kpan.b_line_break.config.core.gui.ModGuiConfigEntries.IGuiConfigEntry;
import kpan.b_line_break.config.core.properties.AbstractConfigProperty;
import kpan.b_line_break.config.core.properties.ConfigPropertyBool;
import kpan.b_line_break.config.core.properties.ConfigPropertyDouble;
import kpan.b_line_break.config.core.properties.ConfigPropertyEnum;
import kpan.b_line_break.config.core.properties.ConfigPropertyFloat;
import kpan.b_line_break.config.core.properties.ConfigPropertyInt;
import kpan.b_line_break.config.core.properties.ConfigPropertyLong;
import kpan.b_line_break.config.core.properties.ConfigPropertyString;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ModConfigCategory implements IConfigElement {
    private static final String INDENT = "    ";
    public static final CharMatcher allowedProperties = CharMatcher.forPredicate(ModConfigCategory::isValidChar);
    private final String id;
    public final boolean isRoot;
    private final ModConfigurationFile configuration;
    private String commentForFile = "";
    private int order = 0;
    private boolean showInGUI = true;
    private final Map<String, ModConfigCategory> children = new TreeMap<>();
    private final Map<String, AbstractConfigProperty> id2PropertyMap = new TreeMap<>();

    public ModConfigCategory(String id, boolean isRoot, ModConfigurationFile configuration) {
        this.id = id;
        this.isRoot = isRoot;
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setCommentForFile(String commentForFile) {
        this.commentForFile = commentForFile;
    }
    public String getCommentForFile() {
        return commentForFile;
    }

    public String getNameTranslationKey(String path) {
        if (path.isEmpty())
            return ModReference.MODID + ".config." + getId();
        else
            return ModReference.MODID + ".config." + path + "." + getId();
    }

    public String getCommentTranslationKey(String path) {
        if (path.isEmpty())
            return ModReference.MODID + ".config." + getId() + ".tooltip";
        else
            return ModReference.MODID + ".config." + path + "." + getId() + ".tooltip";
    }

    public void clear() {
        children.clear();
        id2PropertyMap.clear();
    }

    public void put(String id, AbstractConfigProperty property) {
        id2PropertyMap.put(id, property);
    }

    @Nullable
    public AbstractConfigProperty get(String id) {
        return id2PropertyMap.get(id);
    }

    public List<IConfigElement> getOrderedElements() {
        List<IConfigElement> list = new ArrayList<>(children.size() + id2PropertyMap.size());
        list.addAll(children.values());
        list.addAll(id2PropertyMap.values());
        list.sort(Comparator.comparingInt(IConfigElement::getOrder));
        return list;
    }

    @Override
    public void write(BufferedWriter out, int indent, String path) throws IOException {
        String pad = getIndent(indent);

        String comment = CommentLocalizer.tryLocalize(getCommentTranslationKey(path), getCommentForFile());
        if (!comment.isEmpty()) {
            writeLine(out, pad, Configuration.COMMENT_SEPARATOR);
            writeLine(out, pad, "# ", id);
            writeLine(out, pad, "#--------------------------------------------------------------------------------------------------------#");
            Splitter splitter = Splitter.onPattern("\r?\n");

            for (String line : splitter.split(comment)) {
                writeLine(out, pad, "# ", line);
            }

            writeLine(out, pad, Configuration.COMMENT_SEPARATOR);
        }

        if (!isRoot) {
            String id = this.id;
            if (!allowedProperties.matchesAllOf(id)) {
                id = '"' + id + '"';
            }
            writeLine(out, pad, id, " {");
        }

        out.newLine();
        for (IConfigElement element : getOrderedElements()) {
            String p;
            if (isRoot)
                p = "";
            else if (path.isEmpty())
                p = getId();
            else
                p = path + "." + getId();
            element.write(out, indent + 1, p);
            out.write(Configuration.NEW_LINE);
        }

        if (!isRoot)
            writeLine(out, pad, "}");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IGuiConfigEntry toEntry(ModGuiConfig screen, ModGuiConfigEntries entryList) {
        return new CategoryEntry(screen, entryList, this);
    }

    public ModConfigCategory getOrCreateCategory(String name) {
        ModConfigCategory category = children.get(name);
        if (category == null) {
            category = new ModConfigCategory(name, false, configuration);
            children.put(category.id, category);
        }
        return category;
    }

    @Nullable
    public ModConfigCategory tryGetCategory(String name) {
        return children.get(name);
    }

    public void create(String id, String commentForFile, boolean defaultValue, int order) {
        if (get(id) != null)
            throw new IllegalStateException("property named to \"" + id + "\" already exists!");
        ConfigPropertyBool property = new ConfigPropertyBool(id, defaultValue, commentForFile, order);
        put(id, property);
    }

    public void create(String id, String commentForFile, int defaultValue, int minValue, int maxValue, int order) {
        if (get(id) != null)
            throw new IllegalStateException("property named to \"" + id + "\" already exists!");
        ConfigPropertyInt property = new ConfigPropertyInt(id, defaultValue, minValue, maxValue, commentForFile, order);
        put(id, property);
    }

    public void create(String id, String commentForFile, long defaultValue, long minValue, long maxValue, int order) {
        if (get(id) != null)
            throw new IllegalStateException("property named to \"" + id + "\" already exists!");
        ConfigPropertyLong property = new ConfigPropertyLong(id, defaultValue, minValue, maxValue, commentForFile, order);
        put(id, property);
    }

    public void create(String id, String commentForFile, float defaultValue, float minValue, float maxValue, int order) {
        if (get(id) != null)
            throw new IllegalStateException("property named to \"" + id + "\" already exists!");
        ConfigPropertyFloat property = new ConfigPropertyFloat(id, defaultValue, minValue, maxValue, commentForFile, order);
        put(id, property);
    }

    public void create(String id, String commentForFile, double defaultValue, double minValue, double maxValue, int order) {
        if (get(id) != null)
            throw new IllegalStateException("property named to \"" + id + "\" already exists!");
        ConfigPropertyDouble property = new ConfigPropertyDouble(id, defaultValue, minValue, maxValue, commentForFile, order);
        put(id, property);
    }

    public void create(String id, String commentForFile, String defaultValue, int order) {
        if (get(id) != null)
            throw new IllegalStateException("property named to \"" + id + "\" already exists!");
        ConfigPropertyString property = new ConfigPropertyString(id, defaultValue, commentForFile, order);
        put(id, property);
    }

    public void create(String id, String commentForFile, Enum<?> defaultValue, int order) {
        if (get(id) != null)
            throw new IllegalStateException("property named to \"" + id + "\" already exists!");
        ConfigPropertyEnum property = new ConfigPropertyEnum(id, defaultValue, commentForFile, order);
        put(id, property);
    }

    public boolean getBool(String id) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyBool p)
            return p.getValue();
        else
            throw new IllegalStateException("Bool property \"" + id + "\" is not found!");
    }

    public int getInt(String id) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyInt p)
            return p.getValue();
        else
            throw new IllegalStateException("Int property \"" + id + "\" is not found!");
    }

    public long getLong(String id) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyLong p)
            return p.getValue();
        else
            throw new IllegalStateException("Long property \"" + id + "\" is not found!");
    }

    public float getFloat(String id) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyFloat p)
            return p.getValue();
        else
            throw new IllegalStateException("Float property \"" + id + "\" is not found!");
    }

    public double getDouble(String id) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyDouble p)
            return p.getValue();
        else
            throw new IllegalStateException("Double property \"" + id + "\" is not found!");
    }

    public String getString(String id) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyString p)
            return p.getValue();
        else
            throw new IllegalStateException("String property \"" + id + "\" is not found!");
    }

    public <E extends Enum<E>> E getEnum(String id) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyEnum p)
            return (E) p.getValue();
        else
            throw new IllegalStateException("String property \"" + id + "\" is not found!");
    }

    public void setBool(String id, boolean value) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyBool p)
            p.setValue(value);
        else
            throw new IllegalStateException("Bool property \"" + id + "\" is not found!");
    }

    public void setInt(String id, int value) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyInt p)
            p.setValue(value);
        else
            throw new IllegalStateException("Int property \"" + id + "\" is not found!");
    }

    public void setLong(String id, long value) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyLong p)
            p.setValue(value);
        else
            throw new IllegalStateException("Long property \"" + id + "\" is not found!");
    }

    public void setFloat(String id, float value) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyFloat p)
            p.setValue(value);
        else
            throw new IllegalStateException("Float property \"" + id + "\" is not found!");
    }

    public void setDouble(String id, double value) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyDouble p)
            p.setValue(value);
        else
            throw new IllegalStateException("Double property \"" + id + "\" is not found!");
    }

    public void setString(String id, String value) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyString p)
            p.setValue(value);
        else
            throw new IllegalStateException("String property \"" + id + "\" is not found!");
    }

    public void setEnum(String id, Enum<?> value) {
        AbstractConfigProperty property = get(id);
        if (property instanceof ConfigPropertyEnum p)
            p.setValue(value);
        else
            throw new IllegalStateException("String property \"" + id + "\" is not found!");
    }


    //TODO:
    public boolean requiresWorldRestart() {
        return false;
    }

    public boolean requiresMcRestart() {
        return false;
    }

    @Override
    public boolean showInGui() {
        return showInGUI;
    }


    public static String getIndent(int indent) {
        return StringUtils.repeat(INDENT, Math.max(0, indent));
    }

    public static void writeLine(BufferedWriter out, String... data) throws IOException {
        for (String datum : data) {
            out.write(datum);
        }
        out.write(Configuration.NEW_LINE);
    }

    private static boolean isValidChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
