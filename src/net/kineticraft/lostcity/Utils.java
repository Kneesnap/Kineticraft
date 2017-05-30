package net.kineticraft.lostcity;

import com.google.gson.JsonObject;
import net.kineticraft.lostcity.data.JsonData;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utils - Contains basic static utilties.
 * Created by Kneesnap on 5/29/2017.
 */
public class Utils {


    /**
     * Capitalize every letter after a space.
     * @param sentence
     * @return
     */
    public static String capitalize(String sentence) {
        String[] split = sentence.split(" ");
        List<String> out = new ArrayList<>();
        for (String s : split)
            out.add(s.length() > 0 ? s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() : "");
        return String.join(" ", out);
    }

    /**
     * Save a Location as Json.
     * @param loc
     * @return
     */
    public static JsonObject saveLocation(Location loc) {
        return new JsonData().setNum("x", loc.getX()).setNum("y", loc.getY()).setNum("z", loc.getZ()).setNum("yaw", loc.getYaw())
                .setNum("pitch", loc.getPitch()).setString("world", loc.getWorld().getName()).getJsonObject();
    }

    /**
     * Load a location from Json.
     * @param jsonObject
     * @return
     */
    public static Location loadLocation(JsonObject jsonObject) {
        JsonData data = new JsonData(jsonObject);
        return new Location(Bukkit.getWorld(data.getString("world")), data.getDouble("x"), data.getDouble("y"),
                data.getDouble("z"), data.getFloat("yaw"), data.getFloat("pitch"));
    }

    /**
     * Get a random number between the given range.
     * @param min
     * @param max
     */
    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min) + min;
    }
}
