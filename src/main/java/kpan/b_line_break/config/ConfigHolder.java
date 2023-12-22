package kpan.b_line_break.config;

import kpan.b_line_break.config.core.ConfigAnnotations.Comment;
import kpan.b_line_break.config.core.ConfigAnnotations.ConfigOrder;
import kpan.b_line_break.config.core.ConfigAnnotations.Name;
import kpan.b_line_break.config.core.ConfigVersionUpdateContext;

public class ConfigHolder {

	@Comment("Common settings(Blocks, items, etc.)")
	@ConfigOrder(5)
	public static Common common = new Common();

	public static class Common {

		public EnumTest enumTest = EnumTest.test2;

		public boolean boolValue = true;

		public enum EnumTest {
			TEST1,
			test2,
			Test3
		}
	}

	@Comment("Client only settings(Rendering, resources, etc.)")
	@ConfigOrder(3)
	public static Client client = new Client();

	public static class Client {

		@Name("Gui IDs")
		@Comment("Gui ID settings")
		public GuiIDs Gui_IDs = new GuiIDs();

		public static class GuiIDs {

			@Comment("Gui ID1")
			public int GuiId1 = 31;
		}

	}

	//	@Comment("Server settings(Behaviors, physics, etc.)")
	//	public static Server server = new Server();

	public static class Server {

	}

	@ConfigOrder(2)
	public static long longVal = 89732434533L;

	@ConfigOrder(1)
	public static String string = "StrVal";

	@ConfigOrder(4)
	public static double ThisIsDouble = 343.123;

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
