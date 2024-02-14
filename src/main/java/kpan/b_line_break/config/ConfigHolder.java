package kpan.b_line_break.config;

import kpan.b_line_break.config.core.ConfigAnnotations.ConfigOrder;
import kpan.b_line_break.config.core.ConfigAnnotations.Id;
import kpan.b_line_break.config.core.ConfigVersionUpdateContext;

public class ConfigHolder {

    @Id("Client")
    @ConfigOrder(1)
    public static Client client = new Client();

    public static class Client {

        @Id("LineBreakAlgorithm")
        @ConfigOrder(1)
        public Algorithm lineBreakAlgorithm = Algorithm.NON_ASCII;

        public enum Algorithm {
            VANILLA,
            NON_ASCII,
            PHRASE,
        }
    }

    public static void updateVersion(ConfigVersionUpdateContext context) {
        switch (context.loadedConfigVersion) {
            case "1":
                break;
            default:
                throw new RuntimeException("Unknown config version:" + context.loadedConfigVersion);
        }
    }

    public static String getVersion() {
        return "1";
    }
}
