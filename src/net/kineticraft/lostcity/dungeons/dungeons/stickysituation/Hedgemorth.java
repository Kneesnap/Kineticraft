package net.kineticraft.lostcity.dungeons.dungeons.stickysituation;

import net.kineticraft.lostcity.dungeons.Dungeon;
import net.kineticraft.lostcity.dungeons.DungeonBoss;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * Hedgemorth - Dungeon #2 Final Boss.
 * TODO: Change spawn location.
 * Created by Kneesnap on 8/19/2017.
 */
public class Hedgemorth extends DungeonBoss {
    public Hedgemorth(Dungeon d) {
        super(new Location(d.getWorld(), 0, 10, 0), EntityType.SPIDER, "Hedgemorth", true);
    }
}
