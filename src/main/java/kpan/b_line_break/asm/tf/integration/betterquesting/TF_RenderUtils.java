package kpan.b_line_break.asm.tf.integration.betterquesting;

import kpan.b_line_break.asm.core.AsmTypes;
import kpan.b_line_break.asm.core.AsmUtil;
import kpan.b_line_break.asm.core.adapters.ReplaceRefMethodAdapter;
import org.objectweb.asm.ClassVisitor;

public class TF_RenderUtils {

	private static final String TARGET = "betterquesting.api.utils.RenderUtils";
	private static final String HOOK = AsmTypes.HOOK + "integration/betterquesting/" + "HK_" + "RenderUtils";
	private static final String FONT_RENDERER = "net/minecraft/client/gui/FontRenderer";

	public static ClassVisitor appendVisitor(ClassVisitor cv, String className) {
		if (!TARGET.equals(className))
			return cv;
		ClassVisitor newcv = new ReplaceRefMethodAdapter(cv, HOOK, className, "sizeStringToWidth", AsmUtil.toMethodDesc(AsmTypes.INT, AsmTypes.STRING, AsmTypes.INT, FONT_RENDERER));
		newcv = new ReplaceRefMethodAdapter(newcv, HOOK, className, "getStringWidth", AsmUtil.toMethodDesc(AsmTypes.INT, AsmTypes.STRING, FONT_RENDERER));
		return newcv;
	}
}
