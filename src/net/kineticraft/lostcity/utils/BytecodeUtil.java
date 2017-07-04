package net.kineticraft.lostcity.utils;

import jdk.internal.org.objectweb.asm.Type;

import java.util.Arrays;

/**
 * Allows dynamic usage of bytecode.
 *
 * Created by Kneesnap on 7/3/2017.
 */
public class BytecodeUtil {

    /**
     * Searches for a method to return the method signature of.
     * Does not care about supplied arguments.
     *
     * @param cls
     * @param method
     * @return signature
     */
    public static String getSignature(Class<?> cls, String method) {
        return Type.getMethodDescriptor(Arrays.stream(cls.getMethods()).filter(m -> m.getName().equals(method))
                .findAny().orElse(null));
    }
}
