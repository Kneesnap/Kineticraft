package net.kineticraft.lostcity.dungeons;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

/**
 * A sign that performs an action in a dungeon.
 * Created by Kneesnap on 10/15/2017.
 */
@Getter
public class ActionSign {
    private String type;
    private Sign sign;

    public ActionSign(Sign sign) {
        this.type = sign.getLine(0).substring(1, sign.getLine(0).length() - 1);
        this.sign = sign;
    }

    /**
     * Get a line of text on the sign.
     * @param line
     * @return text
     */
    public String getLine(int line) {
        return getSign().getLine(line);
    }

    /**
     * Get the sign's location.
     * @return location
     */
    public Location getLocation() {
        return getSign().getLocation();
    }

    /**
     * Get the dungeon this sign is located in.
     * @return dungeon
     */
    public Dungeon getDungeon() {
        return Dungeons.getDungeon(getLocation());
    }

    /**
     * Safely disable this sign from the dungeon.
     */
    public void disable() {
        getDungeon().getSigns().remove(this);
        if (!getDungeon().isEditMode()) // Remove this sign so it isn't reloaded, as long as this isn't edit-mode.
            getSign().getBlock().setType(Material.AIR);
    }
}
