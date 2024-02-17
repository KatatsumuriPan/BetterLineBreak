package kpan.b_line_break.asm.core.adapters;

import com.google.common.collect.Lists;
import kpan.b_line_break.asm.core.AsmUtil;
import kpan.b_line_break.asm.core.MyAsmNameRemapper;
import kpan.b_line_break.asm.core.MyAsmNameRemapper.FieldRemap;
import kpan.b_line_break.asm.core.MyAsmNameRemapper.MethodRemap;
import kpan.b_line_break.asm.core.adapters.Instructions.Instr;
import kpan.b_line_break.asm.core.adapters.Instructions.Instr.JumpRep;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

@SuppressWarnings("unused")
public class Instructions implements List<Instr> {
	private final List<Instr> instructions;

	public Instructions() { this(Lists.newArrayList()); }
	public Instructions(List<Instr> instructions) { this.instructions = instructions; }

	public Instructions addInstr(Instr instr) {
		add(instr);
		return this;
	}
	public Instructions fieldInsn(OpcodeField opcode, String runtimeOwner, String runtimeName, String runtimeDesc) {
		return addInstr(Instr.fieldInsn(opcode, runtimeOwner, runtimeName, runtimeDesc));
	}
	public Instructions fieldInsn(OpcodeField opcode, FieldRemap field) {
		return addInstr(Instr.fieldInsn(opcode, field));
	}
	public Instructions getField(FieldRemap field) {
		return fieldInsn(OpcodeField.GET, field);
	}
	public Instructions getField(String runtimeOwner, String runtimeName, String runtimeDesc) {
		return fieldInsn(OpcodeField.GET, runtimeOwner, runtimeName, runtimeDesc);
	}
	public Instructions putField(FieldRemap field) {
		return fieldInsn(OpcodeField.PUT, field);
	}
	public Instructions putField(String runtimeOwner, String runtimeName, String runtimeDesc) {
		return fieldInsn(OpcodeField.PUT, runtimeOwner, runtimeName, runtimeDesc);
	}
	public Instructions getStatic(FieldRemap field) {
		return fieldInsn(OpcodeField.GETSTATIC, field);
	}
	public Instructions getStatic(String runtimeOwner, String runtimeName, String runtimeDesc) {
		return fieldInsn(OpcodeField.GETSTATIC, runtimeOwner, runtimeName, runtimeDesc);
	}
	public Instructions putStatic(FieldRemap field) {
		return fieldInsn(OpcodeField.PUTSTATIC, field);
	}
	public Instructions putStatic(String runtimeOwner, String runtimeName, String runtimeDesc) {
		return fieldInsn(OpcodeField.PUTSTATIC, runtimeOwner, runtimeName, runtimeDesc);
	}
	public Instructions iincInsn(int var, int increment) { return addInstr(Instr.iincInsn(var, increment)); }
	public Instructions intInsn(OpcodeInt opcode, int operand) { return addInstr(Instr.intInsn(opcode, operand)); }
	public Instructions bipush(int operand) { return intInsn(OpcodeInt.BIPUSH, operand); }
	public Instructions sipush(int operand) { return intInsn(OpcodeInt.SIPUSH, operand); }
	public Instructions insn(OpcodesSimple opcode) { return addInstr(Instr.insn(opcode)); }
	public Instructions insn(int opcode) { return addInstr(Instr.insn(opcode)); }
	/**
	 * RETURNのこと（RETURNとRETは異なる）
	 */
	public Instructions ret() { return insn(OpcodesSimple.RETURN); }
	public Instructions iret() { return insn(OpcodesSimple.IRETURN); }
	public Instructions lret() { return insn(OpcodesSimple.LRETURN); }
	public Instructions fret() { return insn(OpcodesSimple.FRETURN); }
	public Instructions dret() { return insn(OpcodesSimple.DRETURN); }
	public Instructions aret() { return insn(OpcodesSimple.ARETURN); }
	public Instructions iconst0() { return insn(OpcodesSimple.ICONST_0); }
	public Instructions iconst1() { return insn(OpcodesSimple.ICONST_1); }
	public Instructions iconst2() { return insn(OpcodesSimple.ICONST_2); }
	public Instructions iconstM1() { return insn(OpcodesSimple.ICONST_M1); }
	public Instructions lconst0() { return insn(OpcodesSimple.LCONST_0); }
	public Instructions fconst0() { return insn(OpcodesSimple.FCONST_0); }
	public Instructions fconst1() { return insn(OpcodesSimple.FCONST_1); }
	public Instructions dconst0() { return insn(OpcodesSimple.DCONST_0); }
	public Instructions dconst1() { return insn(OpcodesSimple.DCONST_1); }
	public Instructions aconstNull() { return insn(OpcodesSimple.ACONST_NULL); }
	public Instructions pop() { return insn(OpcodesSimple.POP); }
	public Instructions dup() { return insn(OpcodesSimple.DUP); }
	public Instructions jumpInsn(OpcodeJump opcode, Label label) { return addInstr(Instr.jumpInsn(opcode, label)); }
	public Instructions jumpInsn(OpcodeJump opcode, int labelIndex) { return addInstr(Instr.jumpInsn(opcode, labelIndex)); }
	public Instructions jumpRep() { return addInstr(Instr.jumpRep()); }
	public Instructions jumpRep(OpcodeJump opcode) { return addInstr(new JumpRep(opcode)); }
	public Instructions label(Label label) { return addInstr(Instr.label(label)); }
	public Instructions label(int labelIndex) { return addInstr(Instr.label(labelIndex)); }
	public Instructions labelRep() { return addInstr(Instr.labelRep()); }
	public Instructions ldcInsn(Object cst) { return addInstr(Instr.ldcInsn(cst)); }
	public Instructions ldcRep() { return addInstr(Instr.ldcRep()); }
	public Instructions typeInsn(int opcode, String type) { return addInstr(Instr.typeInsn(opcode, type)); }
	public Instructions methodInsn(OpcodeMethod opcode, MethodRemap method) {
		return addInstr(Instr.methodInsn(opcode, method));
	}
	public Instructions methodInsn(OpcodeMethod opcode, String runtimeOwner, String runtimeName, String runtimeMethodDesc) {
		return addInstr(Instr.methodInsn(opcode, runtimeOwner, runtimeName, runtimeMethodDesc));
	}
	public Instructions invokeVirtual(MethodRemap method) {
		return methodInsn(OpcodeMethod.VIRTUAL, method);
	}
	public Instructions invokeVirtual(String runtimeOwner, String runtimeName, String runtimeMethodDesc) {
		return methodInsn(OpcodeMethod.VIRTUAL, runtimeOwner, runtimeName, runtimeMethodDesc);
	}
	public Instructions invokeStatic(MethodRemap method) {
		return methodInsn(OpcodeMethod.STATIC, method);
	}
	public Instructions invokeStatic(String runtimeOwner, String runtimeName, String runtimeMethodDesc) {
		return methodInsn(OpcodeMethod.STATIC, runtimeOwner, runtimeName, runtimeMethodDesc);
	}
	public Instructions invokeInterface(MethodRemap method) {
		return methodInsn(OpcodeMethod.INTERFACE, method);
	}
	public Instructions invokeInterface(String runtimeOwner, String runtimeName, String runtimeMethodDesc) {
		return methodInsn(OpcodeMethod.INTERFACE, runtimeOwner, runtimeName, runtimeMethodDesc);
	}
	public Instructions invokeSpecial(MethodRemap method) {
		return methodInsn(OpcodeMethod.SPECIAL, method);
	}
	public Instructions invokeSpecial(String runtimeOwner, String runtimeName, String runtimeMethodDesc) {
		return methodInsn(OpcodeMethod.SPECIAL, runtimeOwner, runtimeName, runtimeMethodDesc);
	}
	public Instructions methodRep(OpcodeMethod opcode, String runtimeOwner, String runtimeName) {
		return addInstr(new Instr.InvokeRep(opcode, runtimeOwner, runtimeName));
	}
	public Instructions varInsn(OpcodeVar opcode, int varIndex) {
		return addInstr(Instr.varInsn(opcode, varIndex));
	}
	public Instructions iload(int varIndex) { return varInsn(OpcodeVar.ILOAD, varIndex); }
	public Instructions lload(int varIndex) { return varInsn(OpcodeVar.LLOAD, varIndex); }
	public Instructions fload(int varIndex) { return varInsn(OpcodeVar.FLOAD, varIndex); }
	public Instructions dload(int varIndex) { return varInsn(OpcodeVar.DLOAD, varIndex); }
	public Instructions aload(int varIndex) { return varInsn(OpcodeVar.ALOAD, varIndex); }
	public Instructions istore(int varIndex) { return varInsn(OpcodeVar.ISTORE, varIndex); }
	public Instructions lstore(int varIndex) { return varInsn(OpcodeVar.LSTORE, varIndex); }
	public Instructions fstore(int varIndex) { return varInsn(OpcodeVar.FSTORE, varIndex); }
	public Instructions dstore(int varIndex) { return varInsn(OpcodeVar.DSTORE, varIndex); }
	public Instructions astore(int varIndex) { return varInsn(OpcodeVar.ASTORE, varIndex); }

	public Instructions rep() { return addInstr(Instr.REP); }

	public static Instructions create(Instr... instructions) { return new Instructions(Lists.newArrayList(instructions)); }

	public static class Instr {
		public static final Instr REP = new Instr(0, null) {
			@Override
			public void visit(MethodVisitor mv, MyMethodVisitor adapter) { }
			@Override
			protected boolean isRep() { return true; }
			@Override
			protected boolean repEquals(Instr other) { return true; }
		};
		private static final Instr LDC_REP = new Instr(0, VisitType.LDC) {
			@Override
			public void visit(MethodVisitor mv, MyMethodVisitor adapter) { }
			@Override
			protected boolean isRep() { return true; }
			@Override
			protected boolean repEquals(Instr other) { return other.type == VisitType.LDC; }
		};
		private static final Instr LABEL_REP = new Instr(0, VisitType.LABEL) {
			@Override
			public void visit(MethodVisitor mv, MyMethodVisitor adapter) { }
			@Override
			protected boolean isRep() { return true; }
			@Override
			protected boolean repEquals(Instr other) { return other.type == VisitType.LABEL; }
		};
		private static final Instr JUMP_REP = new Instr(0, VisitType.JUMP) {
			@Override
			public void visit(MethodVisitor mv, MyMethodVisitor adapter) { }
			@Override
			protected boolean isRep() { return true; }
			@Override
			protected boolean repEquals(Instr other) { return other.type == VisitType.JUMP; }
		};
		private static final Instr LOOKUP_SWITCH_REP = new Instr(0, VisitType.OTHER) {
			@Override
			public void visit(MethodVisitor mv, MyMethodVisitor adapter) { }
			@Override
			protected boolean isRep() { return true; }
			@Override
			protected boolean repEquals(Instr other) { return other == LOOKUP_SWITCH_REP || other instanceof LookupSwitch; }
		};

		public static class JumpRep extends Instr {
			private final OpcodeJump opcode;

			public JumpRep(OpcodeJump opcode) {
				super(opcode.opcode, VisitType.JUMP);
				this.opcode = opcode;
			}

			@Override
			public void visit(MethodVisitor mv, MyMethodVisitor adapter) { }
			@Override
			protected boolean isRep() { return true; }
			@Override
			protected boolean repEquals(Instr other) {
				if (other.type != VisitType.JUMP)
					return false;
				if (opcode.opcode != other.opcode)
					return false;
				return true;
			}
			@Override
			public int hashCode() {
				return opcode.hashCode();
			}
		}

		public static class InvokeRep extends Instr {
			private final OpcodeMethod opcode;
			private final String runtimeOwner;
			private final String runtimeMethodName;

			public InvokeRep(OpcodeMethod opcode, String runtimeOwner, String runtimeMethodName) {
				super(opcode.opcode, VisitType.METHOD);
				this.opcode = opcode;
				this.runtimeOwner = runtimeOwner.replace('.', '/');
				this.runtimeMethodName = runtimeMethodName;
			}

			@Override
			public void visit(MethodVisitor mv, MyMethodVisitor adapter) { }
			@Override
			protected boolean isRep() { return true; }
			@Override
			protected boolean repEquals(Instr other) {
				if (other.type != VisitType.METHOD)
					return false;
				if (opcode.opcode != other.opcode)
					return false;
				if (!runtimeOwner.equals(other.params[0]))
					return false;
				return runtimeMethodName.equals(other.params[1]);
			}
			@Override
			public int hashCode() {
				return Objects.hash(opcode, runtimeOwner, runtimeMethodName);
			}
		}

		public static class LookupSwitch extends Instr {
			private final Label defaulLabel;
			private final int[] keys;
			private final Label[] labels;
			public LookupSwitch(Label defaultLabel, int[] keys, Label[] labels) {
				super(Opcodes.LOOKUPSWITCH, VisitType.OTHER, defaultLabel, keys, labels);
				defaulLabel = defaultLabel;
				this.keys = keys;
				this.labels = labels;
			}

			public Label getDefaulLabel() {
				return defaulLabel;
			}

			public int[] getKeysCopy() {
				return keys.clone();
			}

			public Label[] getLabelsCopy() {
				return labels.clone();
			}

			@Override
			public void visit(@Nullable MethodVisitor mv, @Nullable MyMethodVisitor adapter) {
				if (mv != null)
					mv.visitLookupSwitchInsn(defaulLabel, keys, labels);
			}
			@Override
			public boolean equals(Object o) {
				if (this == o)
					return true;
				if (!(o instanceof Instr))
					return false;
				if (((Instr) o).isRep())
					return ((Instr) o).repEquals(this);
				if (o.getClass() != LookupSwitch.class)
					return false;
				LookupSwitch other = (LookupSwitch) o;
				if (defaulLabel != other.defaulLabel)
					return false;
				if (!Arrays.equals(keys, other.keys))
					return false;
				if (!Arrays.equals(labels, other.labels))
					return false;
				return true;
			}

			@Override
			public int hashCode() {
				return Objects.hash(defaulLabel, keys.length);
			}
		}

		public static Instr dynamicInsn(String runtimeName, String runtimeDesc, Handle bsm, Object... bsmArgs) {
			return new Instr(Opcodes.INVOKEDYNAMIC, VisitType.DYNAMIC, runtimeName, runtimeDesc.replace('.', '/'), bsm, bsmArgs);
		}
		public static Instr fieldInsn(OpcodeField opcode, FieldRemap field) {
			return fieldInsn(opcode.opcode, field);
		}
		public static Instr fieldInsn(OpcodeField opcode, String runtimeOwner, String runtimeName, String runtimeDesc) {
			return fieldInsn(opcode.opcode, runtimeOwner, runtimeName, runtimeDesc);
		}
		public static Instr fieldInsn(int opcode, FieldRemap field) {
			return new Instr(opcode, VisitType.FIELD, field.toRuntime());
		}
		public static Instr fieldInsn(int opcode, String runtimeOwner, String runtimeName, String runtimeDesc) {
			return new Instr(opcode, VisitType.FIELD, runtimeOwner.replace('.', '/'), runtimeName, AsmUtil.toDesc(runtimeDesc));
		}
		public static Instr iincInsn(int var, int increment) {
			return new Instr(Opcodes.IINC, VisitType.IINC, var, increment);
		}
		public static Instr intInsn(OpcodeInt opcode, int operand) {
			return intInsn(opcode.opcode, operand);
		}
		public static Instr intInsn(int opcode, int operand) {
			return new Instr(opcode, VisitType.INT, operand);
		}
		public static Instr insn(OpcodesSimple opcode) {
			return insn(opcode.opcode);
		}
		public static Instr insn(int opcode) {
			return new Instr(opcode, VisitType.INSN);
		}
		public static Instr jumpInsn(OpcodeJump opcode, Label label) {
			return jumpInsn(opcode.opcode, label);
		}
		public static Instr jumpInsn(OpcodeJump opcode, int labelIndex) {
			return jumpInsn(opcode.opcode, labelIndex);
		}
		public static Instr jumpInsn(int opcode, Label label) {
			return new Instr(opcode, VisitType.JUMP, label);
		}
		public static Instr jumpInsn(int opcode, int labelIndex) {
			return new Instr(opcode, VisitType.JUMP, labelIndex);
		}
		public static Instr jumpRep() {
			return JUMP_REP;
		}
		public static Instr label(Label label) {
			return new Instr(0, VisitType.LABEL, label);
		}
		public static Instr label(int labelIndex) {
			return new Instr(0, VisitType.LABEL, labelIndex);
		}
		public static Instr labelRep() {
			return LABEL_REP;
		}
		public static Instr ldcInsn(Object cst) {
			return new Instr(0, VisitType.LDC, cst);
		}
		public static Instr ldcRep() {
			return LDC_REP;
		}
		public static Instr typeInsn(int opcode, String type) {
			return new Instr(opcode, VisitType.TYPE, MyAsmNameRemapper.runtimeClass(type));
		}
		public static Instr methodInsn(OpcodeMethod opcode, MethodRemap method) {
			if (opcode == OpcodeMethod.INTERFACE)
				return methodInsn(opcode.opcode, method, true);
			else
				return methodInsn(opcode.opcode, method);
		}
		public static Instr methodInsn(OpcodeMethod opcode, String runtimeOwner, String runtimeName, String runtimeMethodDesc) {
			if (opcode == OpcodeMethod.INTERFACE)
				return methodInsn(opcode.opcode, runtimeOwner, runtimeName, runtimeMethodDesc, true);
			else
				return methodInsn(opcode.opcode, runtimeOwner, runtimeName, runtimeMethodDesc);
		}
		public static Instr methodInsn(int opcode, MethodRemap method) {
			return methodInsn(opcode, method, false);
		}
		public static Instr methodInsn(int opcode, String runtimeOwner, String runtimeName, String runtimeMethodDesc) {
			return methodInsn(opcode, runtimeOwner, runtimeName, runtimeMethodDesc, false);
		}
		public static Instr methodInsn(int opcode, MethodRemap method, boolean interfaceCall) {
			Object[] array = new Object[4];
			System.arraycopy(method.toRuntime(), 0, array, 0, 3);
			array[3] = interfaceCall;
			return new Instr(opcode, VisitType.METHOD, array);
		}
		public static Instr methodInsn(int opcode, String runtimeOwner, String runtimeName, String runtimeMethodDesc, boolean interfaceCall) {
			return new Instr(opcode, VisitType.METHOD, runtimeOwner.replace('.', '/'), runtimeName, runtimeMethodDesc.replace('.', '/'), interfaceCall);
		}
		public static Instr varInsn(OpcodeVar opcode, int varIndex) {
			return varInsn(opcode.opcode, varIndex);
		}
		public static Instr varInsn(int opcode, int varIndex) {
			return new Instr(opcode, VisitType.VAR, varIndex);
		}
		public static Instr lookupSwicthInsn(Label dflt, int[] keys, Label[] labels) {
			return new LookupSwitch(dflt, keys, labels);
		}
		public static Instr lookupSwitchRep() {
			return LOOKUP_SWITCH_REP;
		}

		public final int opcode;
		private final Object[] params;
		private final VisitType type;

		Instr(int opcode, VisitType type, Object... params) {
			this.opcode = opcode;
			this.type = type;
			this.params = params;
		}

		public Object[] getParamsCopy() {
			return params.clone();
		}

		public void visit(@Nullable MethodVisitor mv, @Nullable MyMethodVisitor adapter) {
			if (mv == null)
				return;
			switch (type) {
				case DYNAMIC:
					mv.visitInvokeDynamicInsn((String) params[0], (String) params[1], (Handle) params[2], (Object[]) params[3]);
					break;
				case FIELD:
					mv.visitFieldInsn(opcode, (String) params[0], (String) params[1], (String) params[2]);
					break;
				case IINC:
					mv.visitIincInsn((Integer) params[0], (Integer) params[1]);//なんかのエラーを防ぐためにintではなくInteger
					break;
				case INT:
					mv.visitIntInsn(opcode, (Integer) params[0]);
					break;
				case INSN:
					mv.visitInsn(opcode);
					break;
				case JUMP:
					if (params[0] instanceof Label) {
						mv.visitJumpInsn(opcode, (Label) params[0]);
					} else {
						if (adapter == null)
							throw new IllegalArgumentException("the adapter must not be null to solve the label");
						mv.visitJumpInsn(opcode, adapter.getLabel((Integer) params[0]));
					}
					break;
				case LABEL:
					if (params[0] instanceof Label) {
						mv.visitLabel((Label) params[0]);
					} else {
						if (adapter == null)
							throw new IllegalArgumentException("the adapter must not be null to solve the label");
						mv.visitLabel(adapter.getLabel((Integer) params[0]));
					}
					break;
				case LDC:
					mv.visitLdcInsn(params[0]);
					break;
				case METHOD:
					mv.visitMethodInsn(opcode, (String) params[0], (String) params[1], (String) params[2], (Boolean) params[3]);
					break;
				case TYPE:
					mv.visitTypeInsn(opcode, (String) params[0]);
					break;
				case VAR:
					mv.visitVarInsn(opcode, (Integer) params[0]);
					break;
				default:
					throw new RuntimeException("Invalid Type:" + type);
			}
		}

		public void solveLabel(MyMethodVisitor adapter) {
			if (isRep())
				return;
			if (type == VisitType.JUMP) {
				if (params[0] instanceof Integer) {
					Label label = adapter.tryGetLabel((Integer) params[0]);
					if (label != null)
						params[0] = label;
				}
			}
			if (type == VisitType.LABEL) {
				if (params[0] instanceof Integer) {
					Label label = adapter.tryGetLabel((Integer) params[0]);
					if (label != null)
						params[0] = label;
				}
			}
		}

		protected boolean isRep() { return false; }
		protected boolean repEquals(Instr other) { return false; }

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof Instr) {
				Instr other = (Instr) obj;
				return equals(this, other);
			}
			return false;
		}

		private static boolean equals(Instr a, Instr b) {
			if (a == REP || b == REP)
				return true;
			if (a.isRep())
				return a.repEquals(b);
			if (b.isRep())
				return b.repEquals(a);
			if (a.type != b.type)
				return false;
			if (a.opcode != b.opcode)
				return false;
			return Arrays.equals(a.params, b.params);
		}

		private enum VisitType {
			DYNAMIC,
			FIELD,
			IINC,
			INT,
			INSN,
			JUMP,
			LABEL,
			LDC,
			METHOD,
			TYPE,
			VAR,
			OTHER,
		}

		@Override
		public String toString() {
			return type +
					"(" +
					StringUtils.join(params, ",") +
					")";
		}
	}

	public enum OpcodesSimple {
		NOP(Opcodes.NOP),
		ACONST_NULL(Opcodes.ACONST_NULL),
		ICONST_M1(Opcodes.ICONST_M1),
		ICONST_0(Opcodes.ICONST_0),
		ICONST_1(Opcodes.ICONST_1),
		ICONST_2(Opcodes.ICONST_2),
		ICONST_3(Opcodes.ICONST_3),
		ICONST_4(Opcodes.ICONST_4),
		ICONST_5(Opcodes.ICONST_5),
		LCONST_0(Opcodes.LCONST_0),
		LCONST_1(Opcodes.LCONST_1),
		FCONST_0(Opcodes.FCONST_0),
		FCONST_1(Opcodes.FCONST_1),
		FCONST_2(Opcodes.FCONST_2),
		DCONST_0(Opcodes.DCONST_0),
		DCONST_1(Opcodes.DCONST_1),
		IALOAD(Opcodes.IALOAD),
		LALOAD(Opcodes.LALOAD),
		FALOAD(Opcodes.FALOAD),
		DALOAD(Opcodes.DALOAD),
		AALOAD(Opcodes.AALOAD),
		BALOAD(Opcodes.BALOAD),
		CALOAD(Opcodes.CALOAD),
		SALOAD(Opcodes.SALOAD),
		IASTORE(Opcodes.IASTORE),
		LASTORE(Opcodes.LASTORE),
		FASTORE(Opcodes.FASTORE),
		DASTORE(Opcodes.DASTORE),
		AASTORE(Opcodes.AASTORE),
		BASTORE(Opcodes.BASTORE),
		CASTORE(Opcodes.CASTORE),
		SASTORE(Opcodes.SASTORE),
		POP(Opcodes.POP),
		POP2(Opcodes.POP2),
		DUP(Opcodes.DUP),
		DUP_X1(Opcodes.DUP_X1),
		DUP_X2(Opcodes.DUP_X2),
		DUP2(Opcodes.DUP2),
		DUP2_X1(Opcodes.DUP2_X1),
		DUP2_X2(Opcodes.DUP2_X2),
		SWAP(Opcodes.SWAP),
		IADD(Opcodes.IADD),
		LADD(Opcodes.LADD),
		FADD(Opcodes.FADD),
		DADD(Opcodes.DADD),
		ISUB(Opcodes.ISUB),
		LSUB(Opcodes.LSUB),
		FSUB(Opcodes.FSUB),
		DSUB(Opcodes.DSUB),
		IMUL(Opcodes.IMUL),
		LMUL(Opcodes.LMUL),
		FMUL(Opcodes.FMUL),
		DMUL(Opcodes.DMUL),
		IDIV(Opcodes.IDIV),
		LDIV(Opcodes.LDIV),
		FDIV(Opcodes.FDIV),
		DDIV(Opcodes.DDIV),
		IREM(Opcodes.IREM),
		LREM(Opcodes.LREM),
		FREM(Opcodes.FREM),
		DREM(Opcodes.DREM),
		INEG(Opcodes.INEG),
		LNEG(Opcodes.LNEG),
		FNEG(Opcodes.FNEG),
		DNEG(Opcodes.DNEG),
		ISHL(Opcodes.ISHL),
		LSHL(Opcodes.LSHL),
		ISHR(Opcodes.ISHR),
		LSHR(Opcodes.LSHR),
		IUSHR(Opcodes.IUSHR),
		LUSHR(Opcodes.LUSHR),
		IAND(Opcodes.IAND),
		LAND(Opcodes.LAND),
		IOR(Opcodes.IOR),
		LOR(Opcodes.LOR),
		IXOR(Opcodes.IXOR),
		LXOR(Opcodes.LXOR),
		I2L(Opcodes.I2L),
		I2F(Opcodes.I2F),
		I2D(Opcodes.I2D),
		L2I(Opcodes.L2I),
		L2F(Opcodes.L2F),
		L2D(Opcodes.L2D),
		F2I(Opcodes.F2I),
		F2L(Opcodes.F2L),
		F2D(Opcodes.F2D),
		D2I(Opcodes.D2I),
		D2L(Opcodes.D2L),
		D2F(Opcodes.D2F),
		I2B(Opcodes.I2B),
		I2C(Opcodes.I2C),
		I2S(Opcodes.I2S),
		LCMP(Opcodes.LCMP),
		FCMPL(Opcodes.FCMPL),
		FCMPG(Opcodes.FCMPG),
		DCMPL(Opcodes.DCMPL),
		DCMPG(Opcodes.DCMPG),
		IRETURN(Opcodes.IRETURN),
		LRETURN(Opcodes.LRETURN),
		FRETURN(Opcodes.FRETURN),
		DRETURN(Opcodes.DRETURN),
		ARETURN(Opcodes.ARETURN),
		RETURN(Opcodes.RETURN),
		ARRAYLENGTH(Opcodes.ARRAYLENGTH),
		ATHROW(Opcodes.ATHROW),
		MONITORENTER(Opcodes.MONITORENTER),
		MONITOREXIT(Opcodes.MONITOREXIT),
		;

		public final int opcode;

		OpcodesSimple(int opcode) { this.opcode = opcode; }
	}

	public enum OpcodeVar {
		ILOAD(Opcodes.ILOAD),
		LLOAD(Opcodes.LLOAD),
		FLOAD(Opcodes.FLOAD),
		DLOAD(Opcodes.DLOAD),
		ALOAD(Opcodes.ALOAD),
		ISTORE(Opcodes.ISTORE),
		LSTORE(Opcodes.LSTORE),
		FSTORE(Opcodes.FSTORE),
		DSTORE(Opcodes.DSTORE),
		ASTORE(Opcodes.ASTORE),
		;

		public final int opcode;

		OpcodeVar(int opcode) { this.opcode = opcode; }
	}

	public enum OpcodeField {
		GET(Opcodes.GETFIELD),
		PUT(Opcodes.PUTFIELD),
		GETSTATIC(Opcodes.GETSTATIC),
		PUTSTATIC(Opcodes.PUTSTATIC),
		;

		public final int opcode;

		OpcodeField(int opcode) { this.opcode = opcode; }
	}

	public enum OpcodeMethod {
		VIRTUAL(Opcodes.INVOKEVIRTUAL),
		SPECIAL(Opcodes.INVOKESPECIAL),
		STATIC(Opcodes.INVOKESTATIC),
		INTERFACE(Opcodes.INVOKEINTERFACE),
		;

		public final int opcode;

		OpcodeMethod(int opcode) { this.opcode = opcode; }
	}

	public enum OpcodeInt {
		BIPUSH(Opcodes.BIPUSH),
		SIPUSH(Opcodes.SIPUSH),
		NEWARRAY(Opcodes.NEWARRAY),
		;

		public final int opcode;

		OpcodeInt(int opcode) { this.opcode = opcode; }
	}

	public enum OpcodeJump {
		IFEQ(Opcodes.IFEQ),
		IFNE(Opcodes.IFNE),
		IFLT(Opcodes.IFLT),
		IFGE(Opcodes.IFGE),
		IFGT(Opcodes.IFGT),
		IFLE(Opcodes.IFLE),
		IF_ICMPEQ(Opcodes.IF_ICMPEQ),
		IF_ICMPNE(Opcodes.IF_ICMPNE),
		IF_ICMPLT(Opcodes.IF_ICMPLT),
		IF_ICMPGE(Opcodes.IF_ICMPGE),
		IF_ICMPGT(Opcodes.IF_ICMPGT),
		IF_ICMPLE(Opcodes.IF_ICMPLE),
		IF_ACMPEQ(Opcodes.IF_ACMPEQ),
		IF_ACMPNE(Opcodes.IF_ACMPNE),
		GOTO(Opcodes.GOTO),
		JSR(Opcodes.JSR),
		IFNULL(Opcodes.IFNULL),
		IFNONNULL(Opcodes.IFNONNULL),
		;

		public final int opcode;

		OpcodeJump(int opcode) { this.opcode = opcode; }
	}

	//Listインターフェース

	@Override
	public int size() { return instructions.size(); }

	@Override
	public boolean isEmpty() { return instructions.isEmpty(); }

	@Override
	public boolean contains(Object o) {
		return instructions.contains(o);
	}

	@Override
	public Iterator<Instr> iterator() { return instructions.iterator(); }

	@Override
	public Object[] toArray() { return instructions.toArray(); }

	@Override
	public <T> T[] toArray(T[] a) { return instructions.toArray(a); }

	@Override
	public boolean add(Instr e) { return instructions.add(e); }

	@Override
	public boolean remove(Object o) { return instructions.remove(o); }

	@Override
	public boolean containsAll(Collection<?> c) { return instructions.containsAll(c); }

	@Override
	public boolean addAll(Collection<? extends Instr> c) { return instructions.addAll(c); }

	@Override
	public boolean addAll(int index, Collection<? extends Instr> c) { return instructions.addAll(index, c); }

	@Override
	public boolean removeAll(Collection<?> c) { return instructions.removeAll(c); }

	@Override
	public boolean retainAll(Collection<?> c) { return instructions.retainAll(c); }

	@Override
	public void clear() { instructions.clear(); }

	@Override
	public Instr get(int index) { return instructions.get(index); }

	@Override
	public Instr set(int index, Instr element) { return instructions.set(index, element); }

	@Override
	public void add(int index, Instr element) { instructions.add(index, element); }

	@Override
	public Instr remove(int index) { return instructions.remove(index); }

	@Override
	public int indexOf(Object o) { return instructions.indexOf(o); }

	@Override
	public int lastIndexOf(Object o) { return instructions.lastIndexOf(o); }

	@Override
	public ListIterator<Instr> listIterator() { return instructions.listIterator(); }

	@Override
	public ListIterator<Instr> listIterator(int index) { return instructions.listIterator(index); }

	@Override
	public List<Instr> subList(int fromIndex, int toIndex) { return instructions.subList(fromIndex, toIndex); }

}
