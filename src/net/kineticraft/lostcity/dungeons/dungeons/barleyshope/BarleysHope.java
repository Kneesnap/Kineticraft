package net.kineticraft.lostcity.dungeons.dungeons.barleyshope;

import net.kineticraft.lostcity.dungeons.Dungeon;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Dungeon 1 - Barley's Hope.
 * Created by Kneesnap on 7/29/2017.
 */
public class BarleysHope extends Dungeon {
    public BarleysHope(List<Player> players) {
        super(players);
        registerPuzzles(new MatchPuzzle(), new GatherPuzzle(), new LazerPuzzle());
    }
}
