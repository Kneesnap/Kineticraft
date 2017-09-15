package net.kineticraft.lostcity.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.party.games.PartyGame;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A base party. TODO: Move this to an enum if possible.
 * Created by Kneesnap on 9/14/2017.
 */
@Getter @AllArgsConstructor
public abstract class Party {
    private String name;
    private Location spawn;

    /**
     * Teleport a player to this party.
     * @param player
     */
    public void teleportIn(Player player) {
        getSpawn().setWorld(Parties.getPartyWorld());
        player.teleport(getSpawn());
        player.sendMessage(ChatColor.BLUE + "Welcome to the " + getName() + " Party!");
    }

    public List<PartyGame> getGames() {
        return null;
    }
}
