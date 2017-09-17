package net.kineticraft.lostcity.party.games;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Represents a player that can be played by one player only.
 * Created by Kneesnap on 9/14/2017.
 */
@Getter
public class SinglePlayerGame extends PartyGame {
    private Player player;

    @Override
    public void onJoin(Player player) {
        this.player = player;
        start();
    }

    @Override
    public void stop() {
        super.stop();
        player = null;
    }

    @Override
    protected boolean canAdd(Player player) {
        if (isGoing()) {
            player.sendMessage(format(ChatColor.GREEN + getPlayer().getName() + ChatColor.BLUE + " is currently playing. Come back later."));
            return false;
        }
        return super.canAdd(player);
    }

    /**
     * Send a party message to the player playing the game.
     * @param message
     */
    protected void sendMessage(String message) {
        getPlayer().sendMessage(format(message));
    }
}
