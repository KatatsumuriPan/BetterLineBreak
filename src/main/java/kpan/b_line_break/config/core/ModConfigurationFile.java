package kpan.b_line_break.config.core;

import kpan.b_line_break.ModMain;
import kpan.b_line_break.ModReference;
import kpan.b_line_break.config.core.properties.AbstractConfigProperty;
import kpan.b_line_break.util.StringReader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Configuration.UnicodeInputStreamReader;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModConfigurationFile {
    private static final String CONFIG_VERSION = "CONFIG_VERSION";

    private final ModConfigCategory rootCategory = new ModConfigCategory("root", true, this);
    private final File file;
    private final String configVersion;
    private String loadedConfigVersion = "";

    public ModConfigurationFile(File file, String configVersion) {
        this.file = file;
        this.configVersion = configVersion;
    }

    public String getLoadedConfigVersion() {
        return loadedConfigVersion;
    }

    public ModConfigCategory getRootCategory() {
        return rootCategory;
    }

    public void load(Consumer<ConfigVersionUpdateContext> updater) {
        loadedConfigVersion = "";
        List<String> lines = new ArrayList<>();
        try {
            UnicodeInputStreamReader input = new UnicodeInputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader buffer = new BufferedReader(input);
            while (true) {
                String line = buffer.readLine();
                if (line == null)
                    break;
                lines.add(line);
            }
        } catch (IOException e) {
            ModMain.LOGGER.error("Cannot open a config file:" + file.getPath());
            return;
        }

        List<String> category_path = new ArrayList<>();
        for (String line : lines) {
            StringReader reader = new StringReader(line);
            reader.skipWhitespace();
            if (!reader.canRead())
                continue;
            switch (reader.peek()) {
                case '#' -> //comment line
                {
                }
                case '}' -> //category end
                {
                    if (category_path.isEmpty()) {
                        ModMain.LOGGER.error("Invalid category end!");
                    } else {
                        category_path.remove(category_path.size() - 1);
                    }
                }
                case '>' ->//list end
                {
                    reader.skip();
                    //TODO
                    if (true)
                        throw new NotImplementedException("");
                }
                case '~' ->//config option
                {
                    reader.skip();
                    if (!category_path.isEmpty()) {
                        ModMain.LOGGER.error("Cannot use option not on root!:" + line);
                        break;
                    }
                    if (!reader.canRead()) {
                        ModMain.LOGGER.error("Invalid line:" + line);
                        break;
                    }
                    if (!isValidChar(reader.peek())) {
                        ModMain.LOGGER.error("Invalid char:" + reader.peek());
                        break;
                    }
                    String name = reader.readStr(ModConfigurationFile::isValidChar);
                    if (reader.canRead() && reader.peek() == ':') {
                        reader.skip();
                        readOption(name, reader.readToChar('#').trim());
                    } else {
                        readOption(name, "");
                    }
                }
                case '"' -> {
                    //category
                    String name = reader.readQuotedString();
                    reader.skipWhitespace();
                    if (reader.canRead()) {
                        ModMain.LOGGER.error("Invalid line:" + line);
                        break;
                    }
                    if (reader.peek() != '{') {
                        ModMain.LOGGER.error("Invalid char:" + reader.peek());
                        break;
                    }
                    reader.skip();
                    category_path.add(name);
                }
                default -> {
                    String name_or_type;
                    if (isValidChar(reader.peek())) {
                        name_or_type = reader.readStr(ModConfigurationFile::isValidChar);
                    } else {
                        ModMain.LOGGER.error("Invalid char:" + reader.peek());
                        break;
                    }
                    reader.skipWhitespace();
                    if (!reader.canRead()) {
                        ModMain.LOGGER.error("Invalid line:" + line);
                    } else if (reader.peek() == '{') {
                        //category
                        reader.skip();
                        category_path.add(name_or_type);
                    } else if (reader.peek() == ':') {
                        //property
                        reader.skip();
                        if (!reader.canRead()) {
                            ModMain.LOGGER.error("Invalid line:" + line);
                            break;
                        }
                        String name;
                        if (reader.peek() == '"') {
                            name = reader.readQuotedString();
                        } else {
                            name = reader.readStr(ModConfigurationFile::isValidChar);
                        }
                        reader.skipWhitespace();
                        if (!reader.canRead()) {
                            ModMain.LOGGER.error("Invalid line:" + line);
                            break;
                        }
                        if (reader.peek() != '=') {
                            ModMain.LOGGER.error("Invalid char:" + reader.peek());
                            break;
                        }
                        reader.skip();
                        String value = reader.readToChar('#').trim();

                        ConfigVersionUpdateContext updateContext = new ConfigVersionUpdateContext(loadedConfigVersion, StringUtils.join(category_path, '.'), name_or_type, name, value);
                        updater.accept(updateContext);

                        ModConfigCategory category = tryGetCategory(updateContext.categoryPath);
                        if (category == null) {
                            ModMain.LOGGER.error("category path \"{}\" is not found!", updateContext.categoryPath);
                            break;
                        }
                        AbstractConfigProperty property = category.get(updateContext.name);
                        if (property == null) {
                            ModMain.LOGGER.error("Unknown property:" + updateContext.name);
                        } else if (!property.getTypeString().equals(updateContext.type)) {
                            ModMain.LOGGER.error("Unmatched type {} (expected {})", updateContext.type, property.getTypeString());
                        } else {
                            if (!property.readValue(updateContext.value))
                                ModMain.LOGGER.error("Invalid value \"{}\" for {}.{}", updateContext.value, updateContext.categoryPath, updateContext.name);
                        }
                    } else {
                        ModMain.LOGGER.error("Invalid char:" + reader.peek());
                    }
                }
            }
        }

        if (loadedConfigVersion.isEmpty()) {
            ModMain.LOGGER.error("Config version not detected!");
        }
    }

    private void readOption(String name, String value) {
        switch (name) {
            case CONFIG_VERSION -> loadedConfigVersion = value;
            default -> ModMain.LOGGER.error("Unknown config option:" + name);
        }
    }

    public void save() {
        try {
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();

            if (!file.exists() && !file.createNewFile())
                return;

            if (file.canWrite()) {
                FileOutputStream fos = new FileOutputStream(file);
                BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));

                buffer.write("# This is a configuration file of " + ModReference.MODNAME + Configuration.NEW_LINE);
                buffer.write("~" + CONFIG_VERSION + ": " + configVersion + Configuration.NEW_LINE + Configuration.NEW_LINE);

                rootCategory.write(buffer, -1, "");

                buffer.close();
                fos.close();
            }
        } catch (IOException e) {
            ModMain.LOGGER.error("Error while saving config {}.", file.getName(), e);
        }
    }


    public ModConfigCategory getOrCreateCategory(String categoryPath) {
        if (categoryPath.isEmpty())
            return rootCategory;
        ModConfigCategory category = rootCategory;
        for (String c : categoryPath.split("\\.")) {
            category = category.getOrCreateCategory(c);
        }
        return category;
    }

    public ModConfigCategory getCategory(String categoryPath) {
        if (categoryPath.isEmpty())
            return rootCategory;
        ModConfigCategory category = rootCategory;
        for (String c : categoryPath.split("\\.")) {
            category = category.tryGetCategory(c);
            if (category == null)
                throw new IllegalStateException("category path \"" + categoryPath + "\" is not found!");
        }
        return category;
    }

    @Nullable
    public ModConfigCategory tryGetCategory(String categoryPath) {
        if (categoryPath.isEmpty())
            return rootCategory;
        ModConfigCategory category = rootCategory;
        for (String c : categoryPath.split("\\.")) {
            category = category.tryGetCategory(c);
            if (category == null)
                return null;
        }
        return category;
    }

    public void createBool(String id, String categoryPath, boolean defaultValue, String commentForFile, int order) {
        getOrCreateCategory(categoryPath).create(id, commentForFile, defaultValue, order);
    }

    public void createInt(String id, String categoryPath, int defaultValue, int minValue, int maxValue, String commentForFile, int order) {
        getOrCreateCategory(categoryPath).create(id, commentForFile, defaultValue, minValue, maxValue, order);
    }

    public void createLong(String id, String categoryPath, long defaultValue, long minValue, long maxValue, String commentForFile, int order) {
        getOrCreateCategory(categoryPath).create(id, commentForFile, defaultValue, minValue, maxValue, order);
    }

    public void createFloat(String id, String categoryPath, float defaultValue, float minValue, float maxValue, String commentForFile, int order) {
        getOrCreateCategory(categoryPath).create(id, commentForFile, defaultValue, minValue, maxValue, order);
    }

    public void createDouble(String id, String categoryPath, double defaultValue, double minValue, double maxValue, String commentForFile, int order) {
        getOrCreateCategory(categoryPath).create(id, commentForFile, defaultValue, minValue, maxValue, order);
    }

    public void createString(String id, String categoryPath, String defaultValue, String commentForFile, int order) {
        getOrCreateCategory(categoryPath).create(id, commentForFile, defaultValue, order);
    }

    public void createEnum(String id, String categoryPath, Enum<?> defaultValue, String commentForFile, int order) {
        getOrCreateCategory(categoryPath).create(id, commentForFile, defaultValue, order);
    }

    public boolean getBool(String id, String categoryPath) {
        return getCategory(categoryPath).getBool(id);
    }

    public int getInt(String id, String categoryPath) {
        return getCategory(categoryPath).getInt(id);
    }

    public long getLong(String id, String categoryPath) {
        return getCategory(categoryPath).getLong(id);
    }

    public float getFloat(String id, String categoryPath) {
        return getCategory(categoryPath).getFloat(id);
    }

    public double getDouble(String id, String categoryPath) {
        return getCategory(categoryPath).getDouble(id);
    }

    public String getString(String id, String categoryPath) {
        return getCategory(categoryPath).getString(id);
    }

    public <E extends Enum<E>> E getEnum(String id, String categoryPath) {
        return getCategory(categoryPath).getEnum(id);
    }

    public void setBool(String id, String categoryPath, boolean value) {
        getCategory(categoryPath).setBool(id, value);
    }

    public void setInt(String id, String categoryPath, int value) {
        getCategory(categoryPath).setInt(id, value);
    }

    public void setLong(String id, String categoryPath, long value) {
        getCategory(categoryPath).setLong(id, value);
    }

    public void setFloat(String id, String categoryPath, float value) {
        getCategory(categoryPath).setFloat(id, value);
    }

    public void setDouble(String id, String categoryPath, double value) {
        getCategory(categoryPath).setDouble(id, value);
    }

    public void setString(String id, String categoryPath, String value) {
        getCategory(categoryPath).setString(id, value);
    }

    public void setEnum(String id, String categoryPath, Enum<?> value) {
        getCategory(categoryPath).setEnum(id, value);
    }

    public static boolean isValidChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }
}
