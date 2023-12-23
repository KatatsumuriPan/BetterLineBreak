package kpan.b_line_break.asm.compat;

public class CompatSmoothFont {
	private static final boolean loaded;

	static {
		boolean tmp;
		try {
			Class.forName("bre.smoothfont.asm.AsmHelper");
			tmp = true;
		} catch (ClassNotFoundException e) {
			tmp = false;
		}
		loaded = tmp;
	}

	public static boolean isLoaded() {
		return loaded;
	}
}
