package net.kineticraft.lostcity.dungeons;

import lombok.Getter;
import net.kineticraft.lostcity.mechanics.Mechanic;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Runs the Dungeons of the game.
 *
 * TODO: Monster spawns.
 * TODO: Cutscene integration.
 * TODO: Monster spawn.
 *
 * Created by Kneesnap on 7/11/2017.
 */
public class Dungeons extends Mechanic {

    @Getter
    private static List<Dungeon> dungeons = new ArrayList<>();

    /**
     * Get all current dungeons that are the supplied dungeon type.
     * @param type
     * @return dungeons
     */
    public static List<Dungeon> getDungeons(DungeonType type) {
        return getDungeons().stream().filter(d -> d.getType() == type).collect(Collectors.toList());
    }

    /**
     * Is the supplied world a dungeon?
     * @param w
     * @return isDungeon
     */
    public static boolean isDungeon(World w) {
        return getDungeon(w) != null;
    }

    /**
     * Get the dungeon housed in the given world. Null if not a dungeon.
     * @param world
     * @return dungeon
     */
    public static Dungeon getDungeon(World world) {
        return getDungeons().stream().filter(d -> d.getWorld().equals(world)).findFirst().orElse(null);
    }

    /**
     * Create a dungeon for the given player.
     * Returns null if the player cannot play this dungeon.
     *
     * @param player
     * @param type
     * @return dungeon
     */
    public static Dungeon createDungeon(Player player, DungeonType type) {
        if (!type.hasUnlocked(player))
            return null;

        Dungeon dungeon = ReflectionUtil.construct(type.getDungeonClass(), Arrays.asList(player));
        dungeon.setup(type);
        getDungeons().add(dungeon);
        return dungeon;
    }
}
