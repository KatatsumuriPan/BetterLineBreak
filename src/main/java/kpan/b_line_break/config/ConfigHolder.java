package kpan.b_line_break.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

import static kpan.b_line_break.ModReference.MODID;

public class ConfigHolder {

    public static Configuration config;

    public static Algorithm lineBreakAlgorithm = Algorithm.NON_ASCII;

    private static final String LANG_PREFIX = MODID + ".config.";

    public static void init(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    public static void syncConfig() {
        config.setCategoryComment(Configuration.CATEGORY_GENERAL, "General");

        lineBreakAlgorithm = Algorithm.fromString(
            config.get(
                    Configuration.CATEGORY_GENERAL,
                    "lineBreakAlgorithm",
                    "NON_ASCII",
                    """
                    The algorithm used for line breaks
                    Possible values: [VANILLA, NON_ASCII, PHRASE]
                     [default: NON_ASCII]""",
                    new String[] {
                        "VANILLA",
                        "NON_ASCII",
                        "PHRASE",
                    }
                )
                .setLanguageKey(LANG_PREFIX + "lineBreakAlgorithm")
                .getString());

        if (config.hasChanged()) {
            config.save();
        }
    }

    public enum Algorithm {
        VANILLA,
        NON_ASCII,
        PHRASE;

        public static Algorithm fromString(String name) {
            try {
                return Algorithm.valueOf(name);
            } catch (IllegalArgumentException ignored) {
                return NON_ASCII;
            }
        }
    }
}
