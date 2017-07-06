package net.kineticraft.lostcity.data.reflect.behavior.bukkit;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.reflect.behavior.SpecialStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Field;

/**
 * Save / load bukkit locations as json.
 *
 * Created by Kneesnap on 7/5/2017.
 */
public class LocationStore extends SpecialStore<Location> {

    public LocationStore() {
        super(Location.class);
    }

    @Override
    public JsonData serialize(Location l) {
        return new JsonData().setNum("x", l.getX()).setNum("y", l.getY()).setNum("z", l.getZ())
                .setNum("yaw", l.getYaw()).setNum("pitch", l.getPitch()).setString("world", l.getWorld().getName());
    }

    @Override
    public Location getField(JsonData data, String key, Field field) {
        data = data.getData(key);
        return new Location(Bukkit.getWorld(data.getString("world")), data.getDouble("x"),
                data.getDouble("y"), data.getDouble("z"), data.getFloat("yaw"), data.getFloat("pitch"));
    }
}
