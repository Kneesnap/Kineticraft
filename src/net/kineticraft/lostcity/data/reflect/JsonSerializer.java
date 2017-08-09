package net.kineticraft.lostcity.data.reflect;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.behavior.*;
import net.kineticraft.lostcity.data.reflect.behavior.bukkit.*;
import net.kineticraft.lostcity.data.reflect.behavior.generic.*;
import net.kineticraft.lostcity.utils.ReflectionUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Basic utility for saving and loading objects as json.
 * Ignores static and transient fields.
 * Use JsonList in place of Lists or arrays.
 * Use JsonMap in place of HashMap.
 *
 * Created by Kneesnap on 7/3/2017.
 */
public class JsonSerializer {

    private static List<DataStore<?>> serializers = new ArrayList<>();
    private static Map<Class<?>, List<Field>> fieldCache = new HashMap<>(); // Massive performance gain b

    static {
        add(new PrimitiveStore<>(Byte.TYPE, "Byte", Byte::new, Byte::parseByte));
        add(new PrimitiveStore<>(Short.TYPE, "Short", Short::new, Short::parseShort));
        add(new PrimitiveStore<>(Integer.TYPE, "Int", Integer::new, Integer::parseInt));
        add(new PrimitiveStore<>(Long.TYPE, "Long", Long::new, Long::parseLong));
        add(new PrimitiveStore<>(Float.TYPE, "Float", Float::new, Float::parseFloat));
        add(new PrimitiveStore<>(Double.TYPE, "Double", Double::new, Double::parseDouble));

        add(new BooleanStore());
        add(new StringStore());
        add(new UUIDStore());
        add(new MapStore());
        add(new EnumStore());
        add(new ListStore());
        add(new JsonableStore());
        add(new LocationStore());
        add(new ItemStackStore());
        add(new ObjectStore()); // Needs to be at the bottom to be the fallback.
    }

    private static void add(DataStore<?> store) {
        serializers.add(store);
    }

    /**
     * Deserialize an object from a stored json file.
     * @param load
     * @param f
     * @param args
     * @param <T>
     * @return newObject
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> T fromFile(Class<T> load, File f, Object... args) {
        return fromJson(load, JsonData.fromFile(f).getJsonObject(), args);
    }

    /**
     * Deserialize an object from stored json text.
     * @param load
     * @param data
     * @param args
     * @param <T>
     * @return newObject
     */
    public static <T> T fromJson(Class<T> load, String data, Object... args) {
        return fromJson(load, new JsonParser().parse(data), args);
    }

    /**
     * Deserialize an object from stored json.
     * @param load - The class of the object to load
     * @param data - The json data to load from
     * @param args - Any additional arguments passed to the constructor.
     * @return newObject - The object created.
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(Class<T> load, JsonElement data, Object... args) {
        if (data.isJsonObject() && data.getAsJsonObject().has("class")) {
            load = (Class<T>) ReflectionUtil.getClass(data.getAsJsonObject().get("class").getAsString());
            data.getAsJsonObject().remove("class"); // Prevent infinite loop.
        }

        assert load != null;
        try {
            DataStore<T> handler = getHandler(load);
            boolean forceJsonable = handler instanceof ObjectStore;
            if (!Jsonable.class.isAssignableFrom(load) && !forceJsonable)
                return handler.loadObject(data);

            T val;
            if (forceJsonable) {
                val = ReflectionUtil.forceConstruct(load);
                deserialize(val, new JsonData(data.getAsJsonObject()));
            } else {
                val = ReflectionUtil.construct(load, args);
                ((Jsonable) val).load(data);
            }
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load fields of a class from serialized json.
     * @param refresh - The object we'd like to refresh the variables of.
     * @param data - The json data to load from.
     */
    public static void deserialize(Object refresh, JsonData data) {
        getFields(refresh).forEach(f -> {
            try {
                getHandler(f).loadField(data, refresh, f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Save a json object to serialized json.
     * @param obj
     * @return saved
     */
    public static JsonElement save(Object obj) {
        return save(obj, false);
    }

    /**
     * Save a java object as serialized json.
     * @param obj - The object to convert to json.
     * @param forceJson - Force this object to be handled as if it was Jsonable.
     * @return saved - The saved json.
     */
    @SuppressWarnings("unchecked")
    public static JsonElement save(Object obj, boolean forceJson) {
        JsonData data = new JsonData();

        if (obj instanceof Jsonable || forceJson) {
            getFields(obj).forEach(f -> {
                try {
                    getHandler(f).saveField(data, f.get(obj), f.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return data.getJsonObject();
        }

        Object result = getHandler(obj.getClass()).serialize(obj);
        if (result instanceof JsonData)
            return ((JsonData) result).getJsonObject();

        return result instanceof JsonElement ? (JsonElement) result : ReflectionUtil.construct(JsonPrimitive.class, result);
    }

    /**
     * Get the serializer for a given field.
     * Alerts staff and returns null if not found.
     * @param field
     * @return handler
     */
    public static DataStore getHandler(Field field) {
        return getHandler(field.getType());
    }

    /**
     * Get the serializer for a given class.
     * Alerts staff and returns null if not found.
     * @param clazz
     * @return handler
     */
    public static DataStore getHandler(Class<?> clazz) {
        return serializers.stream().filter(d -> d.getApplyTo().isAssignableFrom(clazz)).findFirst().orElse(null);
    }

    /**
     * Get applicable fields from an object.
     * @param obj
     * @return fields
     */
    public static List<Field> getFields(Object obj) {
        if (!fieldCache.containsKey(obj.getClass())) {
            List<Field> cache = ReflectionUtil.getAllFields(obj.getClass()).stream()
                    .filter(f -> !Modifier.isStatic(f.getModifiers())) // Not static
                    .filter(f -> !Modifier.isTransient(f.getModifiers())) // Not marked as not serialized
                    .filter(f -> !f.getType().isArray()) // Not an array.
                    .collect(Collectors.toList());
            cache.forEach(f -> f.setAccessible(true)); // Set all fields as accessable.
            fieldCache.put(obj.getClass(), cache);
        }
        return fieldCache.get(obj.getClass());
    }

    /**
     * Identify a json object's class.
     * @param object
     * @param parentClass
     * @param je
     * @return je
     */
    public static JsonElement addClass(Object object, Class<?> parentClass, JsonElement je) {
        if (parentClass == null) {
            parentClass = object.getClass().getSuperclass();
            if (parentClass.getSimpleName().equals("Object"))
                return je;
        }

        if (je.isJsonObject() && !object.getClass().getName().equals(parentClass.getName()))
            je.getAsJsonObject().addProperty("class", object.getClass().getName());
        return je;
    }
}
