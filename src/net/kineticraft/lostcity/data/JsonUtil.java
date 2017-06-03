package net.kineticraft.lostcity.data;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

/**
 * JSONUtil - Contains standard Json Utilities.
 * Created by Kneesnap on 6/1/2017.
 */
public class JsonUtil {

    /**
     * Construct an object from JSON.
     * @param type
     * @param object
     * @param <T>
     * @return
     */
    public static <T extends Jsonable> T fromJson(Class<T> type, JsonObject object) {
        try {
            return type.getDeclaredConstructor(JsonData.class).newInstance(new JsonData(object));
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to construct " + type.getClass() + " from Json.");
            return null;
        }
    }
}
