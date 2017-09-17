package net.kineticraft.lostcity.party.games;

import org.bukkit.entity.Player;

/**
 * Represents a multiplayer game that can be played that is always active and can be freely joined or left.
 * Created by Kneesnap on 9/14/2017.
 */
public class FreeplayGame extends MultiplayerGame {
    public FreeplayGame() {
        super(0);
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
        spawnPlayer(player);
    }

    @Override
    public void stop() {
        //You can't stop a freeplay game, it is forever active.
    }
}
