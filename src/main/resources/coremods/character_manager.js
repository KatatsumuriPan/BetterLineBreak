function initializeCoreMod() {
    var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
    var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
    var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
    var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
    return {
        'splitLines1': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.util.text.CharacterManager',
                'methodName': 'func_238353_a_',
                'methodDesc': '(Ljava/lang/String;ILnet/minecraft/util/text/Style;ZLnet/minecraft/util/text/CharacterManager$ISliceAcceptor;)V'
            },
            'transformer': function (method) {
                ASMAPI.log('INFO', 'Patching CharacterManager#splitLines1');

                method.instructions.clear();
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "kpan/b_line_break/LineBreakingUtil", "splitLines", "(Lnet/minecraft/util/text/CharacterManager;Ljava/lang/String;ILnet/minecraft/util/text/Style;ZLnet/minecraft/util/text/CharacterManager$ISliceAcceptor;)V"));
                method.instructions.add(new InsnNode(Opcodes.RETURN));

                return method;
            }
        },
        'splitLines2': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.util.text.CharacterManager',
                'methodName': 'func_243242_a',
                'methodDesc': '(Lnet/minecraft/util/text/ITextProperties;ILnet/minecraft/util/text/Style;Ljava/util/function/BiConsumer;)V'
            },
            'transformer': function (method) {
                ASMAPI.log('INFO', 'Patching CharacterManager#splitLines2');

                method.instructions.clear();
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "kpan/b_line_break/LineBreakingUtil", "splitLines", "(Lnet/minecraft/util/text/CharacterManager;Lnet/minecraft/util/text/ITextProperties;ILnet/minecraft/util/text/Style;Ljava/util/function/BiConsumer;)V"));
                method.instructions.add(new InsnNode(Opcodes.RETURN));

                return method;
            }
        }
    }
}
