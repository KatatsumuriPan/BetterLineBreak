package kpan.b_line_break.asm.tf;

import kpan.b_line_break.asm.core.AsmTypes;
import kpan.b_line_break.asm.core.AsmUtil;
import kpan.b_line_break.asm.core.MyAsmNameRemapper.MethodRemap;
import kpan.b_line_break.asm.core.adapters.Instructions;
import kpan.b_line_break.asm.core.adapters.Instructions.OpcodeJump;
import kpan.b_line_break.asm.core.adapters.MyClassVisitor;
import kpan.b_line_break.asm.core.adapters.ReplaceInstructionsAdapter;
import kpan.b_line_break.asm.core.adapters.ReplaceRefMethodAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class TF_FontRenderer {

	private static final String TARGET = "net.minecraft.client.gui.FontRenderer";
	private static final String HOOK = AsmTypes.HOOK + "HK_" + "FontRenderer";
	private static final MethodRemap sizeStringToWidth = new MethodRemap(TARGET, "sizeStringToWidth", AsmUtil.toMethodDesc(AsmTypes.INT, AsmTypes.STRING, AsmTypes.INT), "func_78259_e");
	private static final MethodRemap getStringWidth = new MethodRemap(TARGET, "getStringWidth", AsmUtil.toMethodDesc(AsmTypes.INT, AsmTypes.STRING), "func_78256_a");
	private static final MethodRemap trimStringToWidth = new MethodRemap(TARGET, "trimStringToWidth", AsmUtil.toMethodDesc(AsmTypes.STRING, AsmTypes.STRING, AsmTypes.INT, AsmTypes.BOOL), "func_78262_a");
	private static final MethodRemap isFormatColor = new MethodRemap(TARGET, "isFormatColor", AsmUtil.toMethodDesc(AsmTypes.BOOL, AsmTypes.CHAR), "func_78272_b");

	public static ClassVisitor appendVisitor(ClassVisitor cv, String className) {
		if (!TARGET.equals(className))
			return cv;
		ClassVisitor newcv = new MyClassVisitor(cv, className) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
				if (getStringWidth.isTarget(name, desc) || trimStringToWidth.isTarget(name, desc)) {
					mv = new ReplaceInstructionsAdapter(mv, name
							, Instructions.create()
							.jumpRep(OpcodeJump.IF_ICMPEQ)
							.rep()
							.bipush(82)
							.jumpRep(OpcodeJump.IF_ICMPNE)
							, old -> {
						Instructions instructions = Instructions.create();
						int varIndex = (Integer) old.get(1).getParamsCopy()[0];
						Label labelTrue = (Label) old.get(0).getParamsCopy()[0];
						Label labelFalse = (Label) old.get(3).getParamsCopy()[0];
						instructions
								.jumpInsn(OpcodeJump.IF_ICMPEQ, labelTrue)
								.iload(varIndex)
								.bipush(82)
								.jumpInsn(OpcodeJump.IF_ICMPEQ, labelTrue)
								.iload(varIndex)
								.invokeStatic(isFormatColor)
								.jumpInsn(OpcodeJump.IFNE, labelTrue)
								.jumpInsn(OpcodeJump.GOTO, labelFalse)
						;
						return instructions;
					}
					);
					success();
				}
				return mv;
			}
		}.setSuccessExpected(2);
		newcv = new ReplaceRefMethodAdapter(newcv, HOOK, sizeStringToWidth);
		return newcv;
	}
}
