package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import net.kineticraft.lostcity.dungeons.Dungeon;
import net.kineticraft.lostcity.dungeons.DungeonBoss;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

/**
 * Frilda - The fire witch zombie boss
 * Created by Kneesnap on 8/2/2017.
 */
public class Frilda extends DungeonBoss {
    public Frilda(Dungeon d) {
        super(new Location(d.getWorld(), -5, 11, 56), EntityType.ZOMBIE, "Frilda", true);
        setGear(Material.STONE_SWORD, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);
    }
}
