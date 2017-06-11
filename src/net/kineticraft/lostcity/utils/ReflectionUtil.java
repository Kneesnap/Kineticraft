package net.kineticraft.lostcity.utils;

import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
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
        Class<?>[] argTypes = getClasses(args);

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
     * Construct a class with the given arguments.
     * @param clazz
     * @param args
     * @return object
     * @throws Exception
     */
    public static <T> T construct(Class<T> clazz, Object... args) {
        return construct(clazz, getClasses(args), args);
    }

    /**
     * Constructa class with the given arguments.
     * @param clazz
     * @param args
     * @param <T>
     * @return object
     */
    public static <T> T construct(Class<T> clazz, Class[] argTypes, Object... args) {

        try {
            return clazz.getDeclaredConstructor(argTypes).newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Could not construct " + clazz.getSimpleName() + ".");
            return null;
        }
    }

    /**
     * Get an array of classes from the arrays of the objects supplied.
     * @param objects
     * @return classes
     */
    private static Class<?>[] getClasses(Object... objects) {
        if (objects == null)
            objects = new Object[0];

        Class<?>[] classes = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Class<?> cls = objects[i].getClass();
            classes[i] = REPLACE.containsKey(cls) ? REPLACE.get(cls) : cls; // Primitives need to be replaced.
        }
        return classes;
    }

    public static Object getField(Object o, String field) {
        try {
            return o.getClass().getField(field).get(o);
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to get field '" + field + "'.");
            return null;
        }
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
