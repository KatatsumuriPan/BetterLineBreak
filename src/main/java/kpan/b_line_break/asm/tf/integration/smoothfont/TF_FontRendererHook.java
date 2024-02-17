package kpan.b_line_break.asm.tf.integration.smoothfont;

import kpan.b_line_break.asm.core.AsmTypes;
import kpan.b_line_break.asm.core.AsmUtil;
import kpan.b_line_break.asm.core.MyAsmNameRemapper.MethodRemap;
import kpan.b_line_break.asm.core.adapters.Instructions;
import kpan.b_line_break.asm.core.adapters.Instructions.Instr;
import kpan.b_line_break.asm.core.adapters.Instructions.Instr.LookupSwitch;
import kpan.b_line_break.asm.core.adapters.Instructions.OpcodeJump;
import kpan.b_line_break.asm.core.adapters.MyClassVisitor;
import kpan.b_line_break.asm.core.adapters.ReplaceInstructionsAdapter;
import kpan.b_line_break.asm.core.adapters.ReplaceRefMethodAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class TF_FontRendererHook {

	private static final String TARGET = "bre.smoothfont.FontRendererHook";
	private static final String HOOK = AsmTypes.HOOK + "integration/smoothfont/" + "HK_" + "FontRendererHook";
	private static final MethodRemap isFormatColor = new MethodRemap("net.minecraft.client.gui.FontRenderer", "isFormatColor", AsmUtil.toMethodDesc(AsmTypes.BOOL, AsmTypes.CHAR), "func_78272_b");

	public static ClassVisitor appendVisitor(ClassVisitor cv, String className) {
		if (!TARGET.equals(className))
			return cv;
		ClassVisitor newcv = new MyClassVisitor(cv, className) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
				if (name.equals("getStringWidthFloatHook") || name.equals("trimStringToWidthFloatHook")) {
					mv = new ReplaceInstructionsAdapter(mv, name
							, Instructions.create()
							.rep()
							.addInstr(Instr.lookupSwitchRep())
							, old -> {
						Instructions instructions = Instructions.create();
						int varIndex = ((Integer) old.get(0).getParamsCopy()[0]);
						LookupSwitch lookupSwitch = (LookupSwitch) old.get(1);
						Label labelBold = lookupSwitch.getLabelsCopy()[0];
						Label labelReset = lookupSwitch.getLabelsCopy()[1];
						instructions
								.iload(varIndex)
								.bipush('l')
								.jumpInsn(OpcodeJump.IF_ICMPEQ, labelBold)
								.iload(varIndex)
								.bipush('L')
								.jumpInsn(OpcodeJump.IF_ICMPEQ, labelBold)
								.iload(varIndex)
								.bipush('r')
								.jumpInsn(OpcodeJump.IF_ICMPEQ, labelReset)
								.iload(varIndex)
								.bipush('R')
								.jumpInsn(OpcodeJump.IF_ICMPEQ, labelReset)
								.iload(varIndex)
								.invokeStatic(isFormatColor)
								.jumpInsn(OpcodeJump.IFNE, labelReset)
								.jumpInsn(OpcodeJump.GOTO, lookupSwitch.getDefaulLabel())
						;
						return instructions;
					}
					);
					success();
				}
				return mv;
			}
		}.setSuccessExpected(2);
		newcv = new ReplaceRefMethodAdapter(newcv, HOOK, className, "sizeStringToWidthFloatHook", AsmUtil.toMethodDesc(AsmTypes.INT, AsmTypes.STRING, AsmTypes.INT));
		return newcv;
	}
}
