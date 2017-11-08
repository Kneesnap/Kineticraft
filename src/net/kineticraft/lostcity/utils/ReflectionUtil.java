package net.kineticraft.lostcity.utils;

import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Static utils for using reflection.
 * Created by Kneesnap on 6/2/2017.
 */
public class ReflectionUtil {

    private static final Map<Class<?>, Class<?>> REPLACE = new HashMap<>();
    private static final Map<String, Class<?>> classCacheMap = new HashMap<>();

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
     * @param clazz
     * @param methodName
     * @param argTypes
     * @param args
     * @return
     */
    public static Object exec(Class<?> clazz, String methodName, Class[] argTypes, Object... args) {
        try {
            return clazz.getMethod(methodName, argTypes).invoke(null, args);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to execute reflected method " + methodName + "!");
        }
        return null;
    }

    /**
     * Execute a method of a given class and object
     * @param obj
     * @param type
     * @param methodName
     * @param args
     * @return result
     */
    public static Object exec(Object obj, Class<?> type, String methodName, Object... args) {
        // Generate a list of the classes used to get the method.
        Class<?>[] argTypes = getClasses(args);

        try {
            return type.getMethod(methodName, argTypes).invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to execute reflected method " + methodName + "!");
        }
        return null;
    }

    /**
     * Execute a method of a given class.
     */
    public static Object exec(Object obj, String methodName, Object... args) {
        if (obj instanceof Class<?>) { // Static.
            return exec(null, (Class<?>) obj, methodName, args);
        } else {
            return exec(obj, obj.getClass(), methodName, args);
        }
    }

    /**
     * Construct a class with the given arguments.
     * @param clazz
     * @param args
     * @return object
     * @throws Exception
     */
    public static <T> T construct(Class<T> clazz, Object... args) {
        if (clazz == null)
            throw new GeneralException("Tried to construct null class.");
        return construct(clazz, getClasses(args), args);
    }

    /**
     * Force construct a class with default or null values, if possible.
     * @param clazz
     * @param <T>
     * @return constructed
     */
    @SuppressWarnings("unchecked")
    public static <T> T forceConstruct(Class<T> clazz) {
        if (clazz.equals(ItemStack.class)) // Hardcoded override.
            return (T) new ItemStack(Material.DIRT);

        try {
            Constructor c = null;
            int minParams = Integer.MAX_VALUE;
            for (Constructor cls : clazz.getConstructors()) {
                if (cls.getParameterCount() < minParams) {
                    minParams = cls.getParameterCount();
                    c = cls;
                }
            }

            if (minParams == Integer.MAX_VALUE) {
                Core.alertStaff("Failed to force-construct " + clazz.getSimpleName() + ". No constructor found.");
                return null;
            }

            Object[] args = new Object[minParams];
            for (int i = 0; i < minParams; i++) {
                Class<?> cls = c.getParameterTypes()[i];
                if (ReflectionUtil.getNumbers().contains(cls)) {
                    args[i] = 0;
                } else if (Boolean.class.isAssignableFrom(cls)) {
                    args[i] = false;
                }
            }

            return (T) c.newInstance(args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().warning("Failed to force-construct" + clazz.getSimpleName() + ".");
            return null;
        }
    }

    /**
     * Construct a class with the given arguments.
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

    /**
     * Set a field in an object.
     * @param o
     * @param from
     * @param varName
     * @param val
     */
    public static void setField(Object o, Class<?> from, String varName, Object val) {
        try {
            Field field = from.getDeclaredField(varName);
            field.setAccessible(true);
            field.set(o, val);
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to set field '" + varName + "'.");
        }
    }

    /**
     * Set a field value in an object.
     * @param o
     * @param varName
     * @param val
     */
    public static void setField(Object o, String varName, Object val) {
        setField(o, o.getClass(), varName, val);
    }

    /**
     * Get a static value from a class.
     * @param clazz
     * @param field
     * @return value
     */
    public static Object getField(Class<?> clazz, String field) {
        return getField(null, clazz, field);
    }

    public static Object getField(Object o, String field) {
        return getField(o, o.getClass(), field);
    }

    public static Object getField(Object o, Class clazz, String field) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(o);
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

    /**
     * Get a list of methods that match the given signature.
     * Does not include private methods.
     *
     * @param argTypes
     * @return methods
     */
    public static List<Method> getMethods(Class<?> clazz, Class... argTypes) {
        List<Class<?>> args = Arrays.asList(argTypes);
        return Arrays.stream(clazz.getMethods()).filter(m -> Arrays.asList(m.getParameterTypes()).equals(args))
                .collect(Collectors.toList());
    }

    /**
     * Get a list of all inherited and non-inherited fields.
     * @param cls
     * @return fields
     */
    public static List<Field> getAllFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        if (cls.getSuperclass() != null)
            fields.addAll(getAllFields(cls.getSuperclass()));
        return fields;
    }

    /**
     * Get a class by its path.
     * @param path
     * @return class
     */
    public static Class<?> getClass(String path) {
        try {
            if (!classCacheMap.containsKey(path))
                classCacheMap.put(path, Class.forName(path));
            return classCacheMap.get(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a class by its type.
     * @param type
     * @return class
     * @throws ClassNotFoundException
     */
    public static Class<?> getClass(Type type) {
        return getClass(type.toString().substring("class ".length()));
    }

    /**
     * Get the generic type of a field.
     * If there is no generic type, it will return null.
     *
     * @param f
     * @return generic
     */
    public static Class<?> getGenericType(Field f) {
        return f.getGenericType() instanceof ParameterizedType ?
                getClass(((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]) : null;
    }

    public static Collection<Class<?>> getNumbers() {
        return REPLACE.values();
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
