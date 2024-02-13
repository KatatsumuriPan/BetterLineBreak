package kpan.b_line_break.asm.tf;

import kpan.b_line_break.asm.core.AsmTypes;
import kpan.b_line_break.asm.core.AsmUtil;
import kpan.b_line_break.asm.core.MyAsmNameRemapper.MethodRemap;
import kpan.b_line_break.asm.core.adapters.ReplaceRefMethodAdapter;
import org.objectweb.asm.ClassVisitor;

public class TF_FontRenderer {

    private static final String TARGET = "net.minecraft.client.gui.FontRenderer";
    private static final String HOOK = AsmTypes.HOOK + "HK_" + "FontRenderer";
    private static final MethodRemap sizeStringToWidth = new MethodRemap(TARGET, "sizeStringToWidth", AsmUtil.toMethodDesc(AsmTypes.INT, AsmTypes.STRING, AsmTypes.INT), "func_78259_e");

    public static ClassVisitor appendVisitor(ClassVisitor cv, String className) {
        if (!TARGET.equals(className))
            return cv;
        ClassVisitor newcv = new ReplaceRefMethodAdapter(cv, HOOK, sizeStringToWidth);
        return newcv;
    }
}
