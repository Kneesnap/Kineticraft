package net.kineticraft.lostcity.data.reflect.behavior.bukkit;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.reflect.behavior.SpecialStore;
import net.kineticraft.lostcity.item.display.GUIItem;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * Save / load bukkit locations as json.
 * Created by Kneesnap on 7/5/2017.
 */
public class LocationStore extends SpecialStore<Location> {

    public LocationStore() {
        super(Location.class);
    }

    @Override
    public JsonData serialize(Location l) {
        JsonData data = new JsonData();
        if (l != null) {
            data.setNum("x", l.getX()).setNum("y", l.getY()).setNum("z", l.getZ()).setNum("yaw", l.getYaw()).setNum("pitch", l.getPitch());
            if (l.getWorld() != null)
                data.setString("world", l.getWorld().getName());
        }
        return data;
    }

    @Override
    public Location getField(JsonData data, String key, Field field) {
        data = data.getData(key);
        return new Location(data.has("world") ? Bukkit.getWorld(data.getString("world")) : null, data.getDouble("x"),
                data.getDouble("y"), data.getDouble("z"), data.getFloat("yaw"), data.getFloat("pitch"));
    }

    @Override
    public void editItem(GUIItem item, Object value, Consumer<Object> setter) {
        Location loc = (Location) value;
        if (loc != null) {
            item.leftClick(ce -> {
                if (!ce.getEvent().isShiftClick()) {
                    Location l = loc;
                    if (loc.getWorld() == null) {
                        l = l.clone();
                        l.setWorld(ce.getPlayer().getWorld());
                    }
                    ce.getPlayer().teleport(l);
                }
            }).addLore("Location: " + ChatColor.GOLD + Utils.toCleanString(loc), "").addLoreAction("Left", "Teleport");
        }
        item.shiftClick(ce -> setter.accept(ce.getPlayer().getLocation()))
                .setIcon(Material.ELYTRA).addLoreAction("Shift", "Set Location");
    }
}
