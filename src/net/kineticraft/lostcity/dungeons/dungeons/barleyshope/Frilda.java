package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import net.kineticraft.lostcity.dungeons.Dungeon;
import net.kineticraft.lostcity.dungeons.DungeonBoss;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * Frilda - The fire witch zombie boss
 * TODO: Attack Stages
 * TODO: Gear
 * Created by Kneesnap on 8/2/2017.
 */
public class Frilda extends DungeonBoss {
    public Frilda(Dungeon d) {
        super(new Location(d.getWorld(), 0, 0, 0), EntityType.ZOMBIE, "Frilda", true);
    }
}
