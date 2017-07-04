package net.kineticraft.lostcity.data.reflect;

import com.google.gson.JsonObject;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.behavior.*;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Basic utility for saving and loading objects as json.
 * Ignores static and transient fields.
 *
 * Created by Kneesnap on 7/3/2017.
 */
public class JsonSerializer {

    private static List<DataStore<?>> serializers = new ArrayList<>();

    static {
        add(new PrimitiveStore<>(Boolean.TYPE, "getBoolean", "setBoolean"));
        add(new PrimitiveStore<>(Byte.TYPE, "getByte"));
        add(new PrimitiveStore<>(Short.TYPE, "getShort"));
        add(new PrimitiveStore<>(Integer.TYPE, "getInt"));
        add(new PrimitiveStore<>(Long.TYPE, "getLong"));
        add(new PrimitiveStore<>(Float.TYPE, "getFloat"));
        add(new PrimitiveStore<>(Double.TYPE, "getDouble"));
        add(new PrimitiveStore<>(String.class, "getString", "setString"));
        add(new PrimitiveStore<>(UUID.class, "getUUID", "setUUID"));
        add(new PrimitiveStore<>(JsonData.class, "getData", "setElement"));
        add(new PrimitiveStore<>(ItemStack.class, "getItem", "setItem"));

        add(new EnumStore());
        add(new JsonListStore());
        add(new ListStore());
        add(new JsonableStore());
    }

    private static void add(DataStore<?> store) {
        serializers.add(store);
    }

    /**
     * Load a java object from serialized json.
     * @param data
     * @param obj
     * @param <T>
     * @return loaded
     */
    public static <T extends Jsonable> T load(Class<T> data, JsonObject obj) {
        try {
            T val = data.getDeclaredConstructor().newInstance();
            reload(val, obj);
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Could not load " + data.getClass().getSimpleName() + " from JSON.");
            return null;
        }
    }

    /**
     * Reload fields of a class from serialized json.
     *
     * @param j
     * @param obj
     */
    public static void reload(Jsonable j, JsonObject obj) {
        JsonData data = new JsonData(obj);
        getFields(obj).forEach(f -> loadField(j, f, data));
    }

    /**
     * Save a java object as serialized json.
     * @param obj
     * @return saved
     */
    public static JsonData save(Object obj) {
        JsonData data = new JsonData();
        DataStore<?> store = getHandler(obj.getClass());
        getFields(obj).forEach(f -> {
            f.setAccessible(true);
            try {
                store.saveField(data, f.get(obj), f.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return data;
    }

    /**
     * Load a field from json.
     * @param to
     * @param field
     * @param from
     */
    private static void loadField(Object to, Field field, JsonData from) {
        field.setAccessible(true);
        try {
            getHandler(to.getClass()).loadField(from, to, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the serializer for a given class.
     * Alerts staff and returns null if not found.
     *
     * @param clazz
     * @return handler
     */
    private static DataStore getHandler(Class<?> clazz) {
        DataStore app = serializers.stream().filter(d -> d.getApplyTo().isAssignableFrom(clazz)).findFirst().orElse(null);

        if (app == null)
            Core.warn("Don't know how to serialize " + clazz.getSimpleName());
        return app;
    }

    /**
     * Get applicable fields from an object.
     * @param obj
     * @return fields
     */
    private static List<Field> getFields(Object obj) {
        return ReflectionUtil.getAllFields(obj.getClass()).stream()
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> !Modifier.isTransient(f.getModifiers()))
                .collect(Collectors.toList());
    }
}
