package kpan.b_line_break.asm.tf;

import kpan.b_line_break.asm.core.AsmTypes;
import kpan.b_line_break.asm.core.AsmUtil;
import kpan.b_line_break.asm.core.MyAsmNameRemapper.MethodRemap;
import kpan.b_line_break.asm.core.adapters.Instructions;
import kpan.b_line_break.asm.core.adapters.MyClassVisitor;
import kpan.b_line_break.asm.core.adapters.ReplaceInstructionsAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

public class TF_GuiNewChat {

    private static final String TARGET = "net.minecraft.client.gui.GuiNewChat";
    private static final String HOOK = AsmTypes.HOOK + "HK_" + "GuiNewChat";
    private static final MethodRemap func_146237_a = new MethodRemap(TARGET, "func_146237_a", AsmUtil.toMethodDesc(AsmTypes.VOID, "net.minecraft.util.IChatComponent", AsmTypes.INT, AsmTypes.INT, AsmTypes.BOOL), "func_146237_a");

    public static ClassVisitor appendVisitor(ClassVisitor cv, String className) {
        if (!TARGET.equals(className))
            return cv;
        ClassVisitor newcv = new MyClassVisitor(cv, className) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (func_146237_a.isTarget(name, desc)) {
                    mv = new ReplaceInstructionsAdapter(mv, name
                        , target()
                        , Instructions.create()
                        .aload(1)
                        .iload(5)
                        .invokeStatic(HOOK, "splitText", AsmUtil.composeRuntimeMethodDesc(ArrayList.class, "net.minecraft.util.IChatComponent", AsmTypes.INT))
                        .astore(8)
                    );
                    success();
                }
                return mv;
            }
        };
        return newcv;
    }


    private static Instructions target() {
        /*
L5
 LINENUMBER 175 L5
 INVOKESTATIC com/google/common/collect/Lists.newArrayList ()Ljava/util/ArrayList;
 ASTORE 8
L6
 LINENUMBER 176 L6
 ALOAD 1
 INVOKESTATIC com/google/common/collect/Lists.newArrayList (Ljava/lang/Iterable;)Ljava/util/ArrayList;
 ASTORE 9
L7
 LINENUMBER 178 L7
 ICONST_0
 ISTORE 10
L8
FRAME FULL [net/minecraft/client/gui/GuiNewChat net/minecraft/util/IChatComponent I I I I I net/minecraft/util/ChatComponentText java/util/ArrayList java/util/ArrayList I] []
 ILOAD 10
 ALOAD 9
 INVOKEVIRTUAL java/util/ArrayList.size ()I
 IF_ICMPGE L9
L10
 LINENUMBER 180 L10
 ALOAD 9
 ILOAD 10
 INVOKEVIRTUAL java/util/ArrayList.get (I)Ljava/lang/Object;
 CHECKCAST net/minecraft/util/IChatComponent
 ASTORE 11
L11
 LINENUMBER 181 L11
 ALOAD 0
 NEW java/lang/StringBuilder
 DUP
 INVOKESPECIAL java/lang/StringBuilder.<init> ()V
 ALOAD 11
 INVOKEINTERFACE net/minecraft/util/IChatComponent.getChatStyle ()Lnet/minecraft/util/ChatStyle;
 INVOKEVIRTUAL net/minecraft/util/ChatStyle.getFormattingCode ()Ljava/lang/String;
 INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
 ALOAD 11
 INVOKEINTERFACE net/minecraft/util/IChatComponent.getUnformattedTextForChat ()Ljava/lang/String;
 INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
 INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
 INVOKESPECIAL net/minecraft/client/gui/GuiNewChat.func_146235_b (Ljava/lang/String;)Ljava/lang/String;
 ASTORE 12
L12
 LINENUMBER 182 L12
 ALOAD 0
 GETFIELD net/minecraft/client/gui/GuiNewChat.mc : Lnet/minecraft/client/Minecraft;
 GETFIELD net/minecraft/client/Minecraft.fontRenderer : Lnet/minecraft/client/gui/FontRenderer;
 ALOAD 12
 INVOKEVIRTUAL net/minecraft/client/gui/FontRenderer.getStringWidth (Ljava/lang/String;)I
 ISTORE 13
L13
 LINENUMBER 183 L13
 NEW net/minecraft/util/ChatComponentText
 DUP
 ALOAD 12
 INVOKESPECIAL net/minecraft/util/ChatComponentText.<init> (Ljava/lang/String;)V
 ASTORE 14
L14
 LINENUMBER 184 L14
 ALOAD 14
 ALOAD 11
 INVOKEINTERFACE net/minecraft/util/IChatComponent.getChatStyle ()Lnet/minecraft/util/ChatStyle;
 INVOKEVIRTUAL net/minecraft/util/ChatStyle.createShallowCopy ()Lnet/minecraft/util/ChatStyle;
 INVOKEVIRTUAL net/minecraft/util/ChatComponentText.setChatStyle (Lnet/minecraft/util/ChatStyle;)Lnet/minecraft/util/IChatComponent;
 POP
L15
 LINENUMBER 185 L15
 ICONST_0
 ISTORE 15
L16
 LINENUMBER 187 L16
 ILOAD 6
 ILOAD 13
 IADD
 ILOAD 5
 IF_ICMPLE L17
L18
 LINENUMBER 189 L18
 ALOAD 0
 GETFIELD net/minecraft/client/gui/GuiNewChat.mc : Lnet/minecraft/client/Minecraft;
 GETFIELD net/minecraft/client/Minecraft.fontRenderer : Lnet/minecraft/client/gui/FontRenderer;
 ALOAD 12
 ILOAD 5
 ILOAD 6
 ISUB
 ICONST_0
 INVOKEVIRTUAL net/minecraft/client/gui/FontRenderer.trimStringToWidth (Ljava/lang/String;IZ)Ljava/lang/String;
 ASTORE 16
L19
 LINENUMBER 190 L19
 ALOAD 16
 INVOKEVIRTUAL java/lang/String.length ()I
 ALOAD 12
 INVOKEVIRTUAL java/lang/String.length ()I
 IF_ICMPGE L20
 ALOAD 12
 ALOAD 16
 INVOKEVIRTUAL java/lang/String.length ()I
 INVOKEVIRTUAL java/lang/String.substring (I)Ljava/lang/String;
 GOTO L21
L20
FRAME FULL [net/minecraft/client/gui/GuiNewChat net/minecraft/util/IChatComponent I I I I I net/minecraft/util/ChatComponentText java/util/ArrayList java/util/ArrayList I net/minecraft/util/IChatComponent java/lang/String I net/minecraft/util/ChatComponentText I java/lang/String] []
 ACONST_NULL
L21
FRAME SAME1 java/lang/String
 ASTORE 17
L22
 LINENUMBER 192 L22
 ALOAD 17
 IFNULL L23
 ALOAD 17
 INVOKEVIRTUAL java/lang/String.length ()I
 IFLE L23
L24
 LINENUMBER 194 L24
 ALOAD 16
 LDC " "
 INVOKEVIRTUAL java/lang/String.lastIndexOf (Ljava/lang/String;)I
 ISTORE 18
L25
 LINENUMBER 196 L25
 ILOAD 18
 IFLT L26
 ALOAD 0
 GETFIELD net/minecraft/client/gui/GuiNewChat.mc : Lnet/minecraft/client/Minecraft;
 GETFIELD net/minecraft/client/Minecraft.fontRenderer : Lnet/minecraft/client/gui/FontRenderer;
 ALOAD 12
 ICONST_0
 ILOAD 18
 INVOKEVIRTUAL java/lang/String.substring (II)Ljava/lang/String;
 INVOKEVIRTUAL net/minecraft/client/gui/FontRenderer.getStringWidth (Ljava/lang/String;)I
 IFLE L26
L27
 LINENUMBER 198 L27
 ALOAD 12
 ICONST_0
 ILOAD 18
 INVOKEVIRTUAL java/lang/String.substring (II)Ljava/lang/String;
 ASTORE 16
L28
 LINENUMBER 199 L28
 ALOAD 12
 ILOAD 18
 INVOKEVIRTUAL java/lang/String.substring (I)Ljava/lang/String;
 ASTORE 17
L26
 LINENUMBER 202 L26
FRAME APPEND [java/lang/String I]
 NEW net/minecraft/util/ChatComponentText
 DUP
 ALOAD 17
 INVOKESPECIAL net/minecraft/util/ChatComponentText.<init> (Ljava/lang/String;)V
 ASTORE 19
L29
 LINENUMBER 203 L29
 ALOAD 19
 ALOAD 11
 INVOKEINTERFACE net/minecraft/util/IChatComponent.getChatStyle ()Lnet/minecraft/util/ChatStyle;
 INVOKEVIRTUAL net/minecraft/util/ChatStyle.createShallowCopy ()Lnet/minecraft/util/ChatStyle;
 INVOKEVIRTUAL net/minecraft/util/ChatComponentText.setChatStyle (Lnet/minecraft/util/ChatStyle;)Lnet/minecraft/util/IChatComponent;
 POP
L30
 LINENUMBER 204 L30
 ALOAD 9
 ILOAD 10
 ICONST_1
 IADD
 ALOAD 19
 INVOKEVIRTUAL java/util/ArrayList.add (ILjava/lang/Object;)V
L23
 LINENUMBER 207 L23
FRAME CHOP 1
 ALOAD 0
 GETFIELD net/minecraft/client/gui/GuiNewChat.mc : Lnet/minecraft/client/Minecraft;
 GETFIELD net/minecraft/client/Minecraft.fontRenderer : Lnet/minecraft/client/gui/FontRenderer;
 ALOAD 16
 INVOKEVIRTUAL net/minecraft/client/gui/FontRenderer.getStringWidth (Ljava/lang/String;)I
 ISTORE 13
L31
 LINENUMBER 208 L31
 NEW net/minecraft/util/ChatComponentText
 DUP
 ALOAD 16
 INVOKESPECIAL net/minecraft/util/ChatComponentText.<init> (Ljava/lang/String;)V
 ASTORE 14
L32
 LINENUMBER 209 L32
 ALOAD 14
 ALOAD 11
 INVOKEINTERFACE net/minecraft/util/IChatComponent.getChatStyle ()Lnet/minecraft/util/ChatStyle;
 INVOKEVIRTUAL net/minecraft/util/ChatStyle.createShallowCopy ()Lnet/minecraft/util/ChatStyle;
 INVOKEVIRTUAL net/minecraft/util/ChatComponentText.setChatStyle (Lnet/minecraft/util/ChatStyle;)Lnet/minecraft/util/IChatComponent;
 POP
L33
 LINENUMBER 210 L33
 ICONST_1
 ISTORE 15
L17
 LINENUMBER 213 L17
FRAME CHOP 2
 ILOAD 6
 ILOAD 13
 IADD
 ILOAD 5
 IF_ICMPGT L34
L35
 LINENUMBER 215 L35
 ILOAD 6
 ILOAD 13
 IADD
 ISTORE 6
L36
 LINENUMBER 216 L36
 ALOAD 7
 ALOAD 14
 INVOKEVIRTUAL net/minecraft/util/ChatComponentText.appendSibling (Lnet/minecraft/util/IChatComponent;)Lnet/minecraft/util/IChatComponent;
 POP
 GOTO L37
L34
 LINENUMBER 220 L34
FRAME SAME
 ICONST_1
 ISTORE 15
L37
 LINENUMBER 223 L37
FRAME SAME
 ILOAD 15
 IFEQ L38
L39
 LINENUMBER 225 L39
 ALOAD 8
 ALOAD 7
 INVOKEVIRTUAL java/util/ArrayList.add (Ljava/lang/Object;)Z
 POP
L40
 LINENUMBER 226 L40
 ICONST_0
 ISTORE 6
L41
 LINENUMBER 227 L41
 NEW net/minecraft/util/ChatComponentText
 DUP
 LDC ""
 INVOKESPECIAL net/minecraft/util/ChatComponentText.<init> (Ljava/lang/String;)V
 ASTORE 7
L38
 LINENUMBER 178 L38
FRAME FULL [net/minecraft/client/gui/GuiNewChat net/minecraft/util/IChatComponent I I I I I net/minecraft/util/ChatComponentText java/util/ArrayList java/util/ArrayList I] []
 IINC 10 1
 GOTO L8
L9
 LINENUMBER 231 L9
FRAME CHOP 1
 ALOAD 8
 ALOAD 7
 INVOKEVIRTUAL java/util/ArrayList.add (Ljava/lang/Object;)Z
 POP
         */
        Instructions instructions = Instructions.create()
            .labelRep()//L5
            .invokeStatic("com/google/common/collect/Lists", "newArrayList", "()Ljava/util/ArrayList;")
            .astore(8);

        instructions.labelRep();//L6
        for (int i = 0; i < 3; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L7
        for (int i = 0; i < 2; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L8
        for (int i = 0; i < 4; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L10
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L11
        for (int i = 0; i < 14; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L12
        for (int i = 0; i < 6; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L13
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L14
        for (int i = 0; i < 6; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L15
        for (int i = 0; i < 2; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L16
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L18
        for (int i = 0; i < 10; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L19
        for (int i = 0; i < 10; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L20
        for (int i = 0; i < 1; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L21
        for (int i = 0; i < 1; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L22
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L24
        for (int i = 0; i < 4; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L25
        for (int i = 0; i < 11; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L27
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L28
        for (int i = 0; i < 4; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L26
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L29
        for (int i = 0; i < 6; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L30
        for (int i = 0; i < 6; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L23
        for (int i = 0; i < 6; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L31
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L32
        for (int i = 0; i < 6; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L33
        for (int i = 0; i < 2; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L17
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L35
        for (int i = 0; i < 4; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L36
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L34
        for (int i = 0; i < 2; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L37
        for (int i = 0; i < 2; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L39
        for (int i = 0; i < 4; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L40
        for (int i = 0; i < 2; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L41
        for (int i = 0; i < 5; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L38
        for (int i = 0; i < 2; i++) {
            instructions.rep();
        }
        instructions.labelRep();//L9
        for (int i = 0; i < 4; i++) {
            instructions.rep();
        }
        return instructions;

    }
}
