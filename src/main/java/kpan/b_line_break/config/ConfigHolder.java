package kpan.b_line_break.config;

import kpan.b_line_break.config.core.ConfigAnnotations.Comment;
import kpan.b_line_break.config.core.ConfigAnnotations.ConfigOrder;
import kpan.b_line_break.config.core.ConfigAnnotations.Name;
import kpan.b_line_break.config.core.ConfigVersionUpdateContext;

public class ConfigHolder {

//	@Comment("Common settings(Blocks, items, etc.)")
//	@ConfigOrder(5)
//	public static Common common = new Common();

	public static class Common {

	}

	@Comment("Client only settings(Rendering, resources, etc.)")
	@ConfigOrder(1)
	public static Client client = new Client();

	public static class Client {

		@Name("Line Break Algorithm")
		@Comment("The algorithm used for line breaks")
		@ConfigOrder(1)
		public Algorithm lineBreakAlgorithm = Algorithm.NON_ASCII;

		public enum Algorithm {
			VANILLA,
			NON_ASCII,
			PHRASE,
		}
	}

	//	@Comment("Server settings(Behaviors, physics, etc.)")
	//	public static Server server = new Server();

	public static class Server {

	}

	public static void updateVersion(ConfigVersionUpdateContext context) {
		switch (context.loadedConfigVersion) {
			case "1":
				break;
			default:
				throw new RuntimeException("Unknown config version:" + context.loadedConfigVersion);
		}
	}

	public static String getVersion() { return "1"; }
}
