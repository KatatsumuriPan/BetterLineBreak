package kpan.b_line_break.asm.tf.integration.betterquesting;

import kpan.b_line_break.asm.core.AsmTypes;
import kpan.b_line_break.asm.core.AsmUtil;
import kpan.b_line_break.asm.core.adapters.ReplaceRefMethodAdapter;
import org.objectweb.asm.ClassVisitor;

public class TF_RenderUtils {

    private static final String TARGET = "betterquesting.api.utils.RenderUtils";
    private static final String HOOK = AsmTypes.HOOK + "HK_" + "FontRenderer";

    public static ClassVisitor appendVisitor(ClassVisitor cv, String className) {
        if (!TARGET.equals(className))
            return cv;
        return new ReplaceRefMethodAdapter(cv, HOOK, className, "sizeStringToWidth", AsmUtil.toMethodDesc(AsmTypes.INT, AsmTypes.STRING, AsmTypes.INT, AsmTypes.FONT_RENDERER));
    }
}
