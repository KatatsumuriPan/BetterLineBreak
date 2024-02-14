package kpan.b_line_break.asm.tf.integration.smoothfont;

import kpan.b_line_break.asm.core.AsmTypes;
import kpan.b_line_break.asm.core.AsmUtil;
import kpan.b_line_break.asm.core.adapters.ReplaceRefMethodAdapter;
import org.objectweb.asm.ClassVisitor;

public class TF_FontRendererHook {

	private static final String TARGET = "bre.smoothfont.FontRendererHook";
	private static final String HOOK = AsmTypes.HOOK + "integration/smoothfont/" + "HK_" + "FontRendererHook";

	public static ClassVisitor appendVisitor(ClassVisitor cv, String className) {
		if (!TARGET.equals(className))
			return cv;
		ClassVisitor newcv = new ReplaceRefMethodAdapter(cv, HOOK, className, "sizeStringToWidthFloatHook", AsmUtil.toMethodDesc(AsmTypes.INT, AsmTypes.STRING, AsmTypes.INT));
		return newcv;
	}
}
