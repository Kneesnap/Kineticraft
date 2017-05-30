package net.kineticraft.lostcity;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import org.bukkit.Location;

/**
 * Home - Represents a home that a player can travel to.
 *
 * Created by Kneesnap on 5/29/2017.
 */
@Getter @Setter
public class Home implements Jsonable {

    private Location location;

    public Home() {
        this(null);
    }

    public Home(Location location) {
        setLocation(location);
    }

    @Override
    public void load(JsonData data) {
        setLocation(Utils.loadLocation(data.getJsonObject()));
    }

    @Override
    public JsonData save() {
        return new JsonData(Utils.saveLocation(getLocation()));
    }
}
