package net.kineticraft.lostcity.party.anniversary;

import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.party.games.ParkourGame;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Anniversary Parkour
 * Created by Kneesnap on 10/5/2017.
 */
public class SkyParkour extends ParkourGame {
    public SkyParkour() {
        addCheckPoint(12.5, 1, 121.5, 0, 0);
        addCheckPoint(9.725, 38, 126.75, -65, 0);
        addCheckPoint(20.5, 110, 132.5, 0, 0);
        addCheckPoint(9, 131, 137.5, -175, 0);
        setArena(-3, 0, 119, 28, 255, 148);
        setExit(15.5, 63.5, 109.5, 25, -12);
    }

    @Override
    public void onComplete(Player player) {
        super.onComplete(player);
        if (!MetadataManager.updateCooldown(player, "skyReward", 20 * 60 * 60 * 24)) // Once every 24 hours.
            Utils.giveItem(player, ItemManager.createItem(Material.ELYTRA, ChatColor.BLUE + "Sky Elytra", "Won from the Sky Parkour", "Anniversary Party, 2017"));
    }
}
