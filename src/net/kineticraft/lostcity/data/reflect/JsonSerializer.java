package net.kineticraft.lostcity.data.reflect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.behavior.*;
import net.kineticraft.lostcity.data.reflect.behavior.bukkit.*;
import net.kineticraft.lostcity.utils.GeneralException;
import net.kineticraft.lostcity.utils.ReflectionUtil;

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
    private static Map<Class<?>, List<Field>> fieldCache = new HashMap<>(); // Massive performance gain b

    static {
        add(new PrimitiveStore<>(Byte.TYPE, "Byte", Byte::new));
        add(new PrimitiveStore<>(Short.TYPE, "Short", Short::new));
        add(new PrimitiveStore<>(Integer.TYPE, "Int", Integer::new));
        add(new PrimitiveStore<>(Long.TYPE, "Long", Long::new));
        add(new PrimitiveStore<>(Float.TYPE, "Float", Float::new));
        add(new PrimitiveStore<>(Double.TYPE, "Double", Double::new));

        add(new MethodStore<>(Boolean.TYPE, "Boolean"));
        add(new MethodStore<>(String.class, "String"));
        add(new MethodStore<>(UUID.class, "UUID"));
        add(new MethodStore<>(JsonData.class, "setElement", "getData"));

        add(new MapStore());
        add(new EnumStore());
        add(new ListStore());
        add(new JsonableStore());
        add(new LocationStore());
        add(new ItemStackStore());
    }

    private static void add(DataStore<?> store) {
        serializers.add(store);
    }

    /**
     * Deserialize an object from stored json.
     * May fail if the object passed does not have a deserializer.
     *
     * @param load - The class to load.
     * @param data - The data to load from.
     * @param <T>
     * @return newObject
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadUnsafe(Class<T> load, JsonData data) {
        if (Jsonable.class.isAssignableFrom(load))
            return (T) fromJson((Class<Jsonable>) load, data);

        try {
            return ((DataStore<T>) getHandler(load, "unsafe object")).loadObject(data);
        } catch (Exception e) {
            throw new GeneralException("Could not load " + load.getClass().getSimpleName() + " from JSON.", e);
        }
    }

    /**
     * Deserialize an object from stored json.
     * @param load - The class of the object to load
     * @param data - The json data to load from
     * @param args - Any additional arguments passed to the constructor.
     * @param <T>
     * @return newObject - The object created.
     */
    public static <T extends Jsonable> T fromJson(Class<T> load, JsonData data, Object... args) {
        try {
            T val = ReflectionUtil.construct(load, args);
            val.load(data);
            return val;
        } catch (Exception e) {
            throw new GeneralException("Could not load " + load.getClass().getSimpleName() + " from JSON.", e);
        }
    }

    /**
     * Deserialize an object from stored json.
     * @param load - The class of the object to load
     * @param json - The json data to load from
     * @param args - Any additional arguments passed to the constructor.
     * @param <T>
     * @return newObject - The object created.
     */
    public static <T extends Jsonable> T fromJson(Class<T> load, JsonObject json, Object... args) {
        return fromJson(load, new JsonData(json), args);
    }

    /**
     * Load fields of a class from serialized json.
     *
     * @param refresh - The object we'd like to refresh the variables of.
     * @param data - The json data to load from.
     */
    public static void deserialize(Object refresh, JsonData data) {
        getFields(refresh).forEach(f -> {
            try {
                getHandler(f).loadField(data, refresh, f);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Save a java object as serialized json.
     * @param obj - The object to convert to json.
     * @return saved - The saved json.
     */
    @SuppressWarnings("unchecked")
    public static JsonElement save(Object obj) {
        JsonData data = new JsonData();

        if (obj instanceof Jsonable) {
            getFields(obj).forEach(f -> {
                try {
                    getHandler(f).saveField(data, f.get(obj), f.getName());
                } catch (Exception e) {
                    throw new GeneralException("Failed to save " + obj.getClass().getName() + " as JSON.", e);
                }
            });
            return data.getJsonObject();
        }

        Object result = getHandler(obj.getClass(), "object").serialize(obj);
        return result instanceof JsonData ? ((JsonData) result).getJsonObject() : (JsonElement) result;

    }

    /**
     * Get the serializer for a given field.
     * Alerts staff and returns null if not found.
     *
     * @param field
     * @return handler
     */
    private static DataStore getHandler(Field field) {
        return getHandler(field.getType(), field.getName());
    }

    /**
     * Get the serializer for a given class.
     * Alerts staff and returns null if not found.
     *
     * @param clazz
     * @param objName
     * @return handler
     */
    private static DataStore getHandler(Class<?> clazz, String objName) {
        DataStore app = serializers.stream().filter(d -> d.getApplyTo().isAssignableFrom(clazz)).findFirst().orElse(null);

        if (app == null)
            throw new GeneralException("Don't know how to handle " + objName + " as a " + clazz.getSimpleName() + ".");
        return app;
    }

    /**
     * Get applicable fields from an object.
     * @param obj
     * @return fields
     */
    private static List<Field> getFields(Object obj) {
        if (!fieldCache.containsKey(obj.getClass())) {
            List<Field> cache = ReflectionUtil.getAllFields(obj.getClass()).stream()
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .filter(f -> !Modifier.isTransient(f.getModifiers()))
                    .collect(Collectors.toList());
            cache.forEach(f -> f.setAccessible(true)); // Set all fields as accessable.
            fieldCache.put(obj.getClass(), cache);
        }
        return fieldCache.get(obj.getClass());
    }
}
