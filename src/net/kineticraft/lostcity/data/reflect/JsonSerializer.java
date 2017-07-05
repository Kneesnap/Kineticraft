package net.kineticraft.lostcity.data.reflect;

import com.google.gson.JsonObject;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.behavior.*;
import net.kineticraft.lostcity.utils.GeneralException;
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
    private static Map<Class<?>, List<Field>> fieldCache = new HashMap<>();

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
        add(new MethodStore<>(ItemStack.class, "Item"));

        add(new MapStore());
        add(new EnumStore());
        add(new ListStore());
        add(new JsonableStore());
    }

    private static void add(DataStore<?> store) {
        serializers.add(store);
    }

    /**
     * Deserialize an object from stored json.
     * @param load - The class of the object to load
     * @param json - The json data to load from
     * @param args - Any additional arguments passed to the constructor.
     * @param <T>
     * @return
     */
    public static <T extends Jsonable> T fromJson(Class<T> load, JsonObject json, Object... args) {
        try {
            T val = ReflectionUtil.construct(load, args);
            val.load(new JsonData(json));
            return val;
        } catch (Exception e) {
            throw new GeneralException("Could not load " + load.getClass().getSimpleName() + " from JSON.", e);
        }
    }

    /**
     * TODO: This is for testing purposes and should be deleted later.
     * @param load
     * @param json
     * @param <T>
     * @return
     */
    public static <T extends Jsonable> T loadNew(Class<T> load, JsonObject json) {
        try {
            T val = load.getDeclaredConstructor().newInstance();
            deserialize(val, json);
            return val;
        } catch (Exception e) {
            throw new GeneralException("Could not load " + load.getClass().getSimpleName() + " from JSON.", e);
        }
    }

    /**
     * Load fields of a class from serialized json.
     *
     * @param refresh - The object we'd like to refresh the variables of.
     * @param obj - The json data to load from.
     */
    public static void deserialize(Object refresh, JsonObject obj) {
        JsonData data = new JsonData(obj);
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
    public static JsonData save(Object obj) {
        JsonData data = new JsonData();
        getFields(obj).forEach(f -> {
            try {
                getHandler(f).saveField(data, f.get(obj), f.getName());
            } catch (Exception e) {
                throw new GeneralException("Failed to save " + obj.getClass().getName() + " as JSON.", e);
            }
        });
        return data;
    }

    /**
     * Get the serializer for a given field.
     * Alerts staff and returns null if not found.
     *
     * @param field
     * @return handler
     */
    private static DataStore getHandler(Field field) {
        Class<?> clazz = field.getType();
        DataStore app = serializers.stream().filter(d -> d.getApplyTo().isAssignableFrom(clazz)).findFirst().orElse(null);

        if (app == null)
            throw new GeneralException("Don't know how to handle " + field.getName() + " as a " + clazz.getSimpleName() + ".");
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
