package net.kineticraft.lostcity.dungeons;

import lombok.Getter;
import net.kineticraft.lostcity.entity.CustomEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * A base for a DungeonBoss.
 * Created by Kneesnap on 7/11/2017.
 */
@Getter
public class DungeonBoss extends CustomEntity {
    private boolean finalBoss;

    public DungeonBoss(Location loc, EntityType type, String name, boolean finalBoss) {
        super(loc, type, name);
        this.finalBoss = finalBoss;
        getBukkit().setCustomName(ChatColor.RED + name);
        getBukkit().setCustomNameVisible(true);
    }

    @Override
    public void onDeath() {
        getDungeon().playCutscene("complete");
    }

    /**
     * Get the dungeon this boss belongs to.
     * @return dungeon.
     */
    public Dungeon getDungeon() {
        return Dungeons.getDungeon(getBukkit());
    }
}
