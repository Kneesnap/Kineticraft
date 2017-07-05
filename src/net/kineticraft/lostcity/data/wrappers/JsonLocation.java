package net.kineticraft.lostcity.data.wrappers;

import lombok.Getter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * JsonLocation - For storing and loading locations as json.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@Getter
public class JsonLocation implements Jsonable {

    private Location location;

    public JsonLocation() {

    }

    public JsonLocation(Location loc) {
        this.location = loc;
    }

    @Override
    public void load(JsonData data) {
        this.location = new Location(Bukkit.getWorld(data.getString("world")), data.getDouble("x"),
                data.getDouble("y"), data.getDouble("z"), data.getFloat("yaw"), data.getFloat("pitch"));
    }

    @Override
    public JsonData save() {
        Location loc = getLocation();
        return new JsonData().setNum("x", loc.getX()).setNum("y", loc.getY()).setNum("z", loc.getZ()).setNum("yaw", loc.getYaw())
                .setNum("pitch", loc.getPitch()).setString("world", loc.getWorld().getName());
    }
}
