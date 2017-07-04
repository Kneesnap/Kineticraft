package net.kineticraft.lostcity.data.reflect;

import com.destroystokyo.paper.event.executor.asm.ClassDefiner;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.utils.BytecodeUtil;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.objectweb.asm.*;

import java.lang.reflect.Field;

import static org.objectweb.asm.Opcodes.*;

/**
 * Allows dynamic serialization and deserialization to be created using Java byte-code.
 * <p>
 * Created by Kneesnap on 7/3/2017.
 */
public interface NewJsonable {

    default void bytecodeTest() {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        String suffix = "1";

        //  CREATE BLANK CONSTRUCTOR  //
        cw.visit(52, ACC_PUBLIC + ACC_SUPER, Type.getInternalName(getClass()) + suffix, null, Type.getInternalName(Object.class), null);
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V", false); // super()
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        //  GENERATE LOAD METHOD  //
        /*{
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "load", "(Lnet/kineticraft/lostcity/data/JsonData;)V", null, null);
            mv.visitCode();

            //TODO: Update this to check if the key exists first.


            for (Field field : new Field[0]) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitLdcInsn(field.getName());
                mv.visitLdcInsn("DEFAULT VALUE");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/kineticraft/lostcity/data/JsonData", "LOAD METHOD",
                        BytecodeUtil.getSignature(JsonData.class, "LOAD METHOD"),false);
                mv.visitFieldInsn(Opcodes.PUTFIELD, Type.getInternalName(getClass()), field.getName(), Type.getInternalName(field.getType()));
            }


            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(4, 2);
            mv.visitEnd();

            //TODO: Enums need special care. (Do lists too?)
            //mv.visitLdcInsn("buildType");
            //mv.visitFieldInsn(GETSTATIC, "net/kineticraft/lostcity/BuildType", "DEV", "Lnet/kineticraft/lostcity/BuildType;");
            //mv.visitMethodInsn(INVOKEVIRTUAL, "net/kineticraft/lostcity/data/JsonData", "getEnum", "(Ljava/lang/String;Ljava/lang/Enum;)Ljava/lang/Enum;", false);
            //mv.visitTypeInsn(CHECKCAST, "net/kineticraft/lostcity/BuildType");
            //mv.visitFieldInsn(PUTFIELD, "net/kineticraft/lostcity/config/configs/MainConfig", "buildType", "Lnet/kineticraft/lostcity/BuildType;");
        }*/

        {
            mv = cw.visitMethod(ACC_PUBLIC, "save", "()Lnet/kineticraft/lostcity/data/JsonData;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "net/kineticraft/lostcity/data/JsonData");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "net/kineticraft/lostcity/data/JsonData", "<init>", "()V", false);

            for (Field field : new Field[0]) {
                mv.visitLdcInsn(field.getName());
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, Type.getInternalName(getClass()), field.getName(), Type.getInternalName(field.getType()));
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/kineticraft/lostcity/data/JsonData", "SETTER",
                        BytecodeUtil.getSignature(JsonData.class, "SETTER"), false);
            }

            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 1);
            mv.visitEnd();
        }

        cw.visitEnd();
        cw.toByteArray();


        Class<?> cls = ClassDefiner.getInstance().defineClass(getClass().getClassLoader(), getClass().getName() + suffix, cw.toByteArray());
        ((JsonData) ReflectionUtil.exec(ReflectionUtil.construct(cls), "save")).toFile("test");
    }
}
