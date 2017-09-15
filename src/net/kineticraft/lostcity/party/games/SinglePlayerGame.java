package net.kineticraft.lostcity.party.games;

import org.bukkit.entity.Player;

/**
 * Represents a player that can be played by one player only.
 * Created by Kneesnap on 9/14/2017.
 */
public class SinglePlayerGame extends PartyGame {

    /**
     * Get the player playing this game.
     * @return player
     */
    public Player getPlayer() {
        return getPlayers().iterator().next();
    }
}
