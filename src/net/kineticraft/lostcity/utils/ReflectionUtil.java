package net.kineticraft.lostcity.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by Kneesnap on 6/2/2017.
 */
public class ReflectionUtil {

    private static Map<Class<?>, Class<?>> REPLACE = new HashMap<>();

    public static String getVersion() {
        String cls = Bukkit.getServer().getClass().getPackage().getName();
        return cls.substring(cls.lastIndexOf(".") + 1);
    }

    /**
     * Returns the method as a supplier
     */
    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> getSupplier(Object o, String method) {
        try {
            Method m = o.getClass().getMethod(method); //Doing it here caches it.
            return () -> {
                try {
                    return (T) m.invoke(o);
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getLogger().warning("Failed to invoke " + method + " as supplier.");
                    return null;
                }};
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to return " + method + " as a supplier.");
            return null;
        }
    }

    /**
     * Execute a method of a given class.
     */
    public static Object exec(Object obj, String methodName, Object... args) {

        // Generate a list of the classes used to get the method.
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            Class<?> cls = args[i].getClass();
            argTypes[i] = REPLACE.containsKey(cls) ? REPLACE.get(cls) : cls; // Primitives need to be replaced.
        }

        try {
            if (obj instanceof Class<?>) { // Static.
                return ((Class<?>) obj).getMethod(methodName, argTypes).invoke(null, args);
            } else {
                return obj.getClass().getMethod(methodName, argTypes).invoke(obj, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to execute reflected method " + methodName + "!");
        }
        return null;
    }

    /**
     * Gets an NMS class by the given name.
     */
    public static Class<?> getNMS(String clazz) {
        return getClass("net.minecraft.server." + getVersion() + '.' + clazz);
    }

    /**
     * Gets a CB class by the given name.
     */
    public static Class<?> getCraftBukkit(String clazz) {
        return getClass("org.bukkit.craftbukkit." + getVersion() + '.' + clazz);
    }

    private static Class<?> getClass(String path) {
        try {
            return Class.forName(path);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to load class: " + path);
        }
        return null;
    }

    static {
        REPLACE.put(Boolean.class, Boolean.TYPE);
        REPLACE.put(Byte.class, Byte.TYPE);
        REPLACE.put(Short.class, Short.TYPE);
        REPLACE.put(Integer.class, Integer.TYPE);
        REPLACE.put(Long.class, Long.TYPE);
        REPLACE.put(Float.class, Float.TYPE);
        REPLACE.put(Double.class, Double.TYPE);
    }
}
