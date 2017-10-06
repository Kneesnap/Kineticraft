package net.kineticraft.lostcity.party;

import lombok.Getter;
import net.kineticraft.lostcity.party.anniversary.*;
import net.kineticraft.lostcity.party.games.PartyGame;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * A list of parties and their games.
 * Created by Kneesnap on 9/14/2017.
 */
public enum Party {
    ANNIVERSARY(93.5, 75, 39.5, 90, 0, Boost::new, DJBooth::new, Splef::new, Pictionary::new, SkyParkour::new, Pinata::new);

    Party(double x, double y, double z, float yaw, float pitch, Supplier<PartyGame>... maker) {
        this.spawn = new Location(Parties.getPartyWorld(), x, y, z, yaw, pitch);
        this.gameMaker = Arrays.asList(maker);
    }

    @Getter private Location spawn;
    @Getter private List<PartyGame> games = new ArrayList<>();
    private List<Supplier<PartyGame>> gameMaker;

    /**
     * Teleport a player to this party.
     * @param player
     */
    public void teleportIn(Player player) {
        getSpawn().setWorld(Parties.getPartyWorld());
        player.teleport(getSpawn());
        player.sendMessage(ChatColor.BLUE + "Welcome to the " + getName() + " Party!");
    }

    /**
     * Get the name of this party, or what it celebrates.
     * @return partyName
     */
    public String getName() {
        return Utils.capitalize(name());
    }

    /**
     * Activate this party.
     */
    public void setup() {
        if (games.isEmpty()) // Construct the games if the party is active.
            gameMaker.stream().map(Supplier::get).forEach(games::add);
        Parties.getPartyWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
    }
}
