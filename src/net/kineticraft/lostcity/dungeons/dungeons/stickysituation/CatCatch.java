package net.kineticraft.lostcity.dungeons.dungeons.stickysituation;

import net.kineticraft.lostcity.dungeons.puzzle.Puzzle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Catch Hagatha's cats.
 * Created by Kneesnap on 10/21/2017.
 */
public class CatCatch extends Puzzle {

    public CatCatch() {
        super(EntityDeathEvent.getHandlerList());
    }

    //TODO: Bats spit at player.
    //TODO: Spawn bats
    //TODO: Player attempts to leave -> Witch attack (Should we just block the exit instead?) It'd be difficult to implement this, and keep it stable.

    @EventHandler
    public void onBatDeath(EntityDeathEvent evt) {

    }
}
