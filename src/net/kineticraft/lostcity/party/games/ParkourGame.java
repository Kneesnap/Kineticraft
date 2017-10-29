package net.kineticraft.lostcity.party.games;

import com.destroystokyo.paper.Title;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.party.Parties;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Parkour game base.
 * Created by Kneesnap on 10/5/2017.
 */
public class ParkourGame extends FreeplayGame {
    private List<Location> checkpoints = new ArrayList<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        if (!isPlaying(evt.getPlayer()))
            return;
        double maxY = MetadataManager.getValue(evt.getPlayer(), "pkMaxY", 0);
        if (evt.getTo().clone().subtract(0, .1, 0).getBlock().getType() != Material.AIR) { // If on ground.
            MetadataManager.setMetadata(evt.getPlayer(), "pkMaxY", evt.getPlayer().getLocation().getY()); // Update accepted Y levels.
        } else if (evt.getTo().getY() <= maxY - 3) { // If they've fallen off
            Location checkpoint = checkpoints.get(MetadataManager.getValue(evt.getPlayer(), "pkCheckpoint", 0));
            MetadataManager.setMetadata(evt.getPlayer(), "pkMaxY", checkpoint.getY()); // Update accepted Y levels.
            evt.setTo(checkpoint); // Teleport them to checkpoint
        }
    }

    @Override
    public void onJoin(Player player) {
        MetadataManager.setMetadata(player, "pkCheckpoint", 0);
        MetadataManager.setMetadata(player, "pkMaxY", player.getLocation().getY());
        player.teleport(checkpoints.get(0));
    }

    @Override
    public void signAction(String action, Player player, Sign sign) {
        if (action.equals("Checkpoint")) {
            MetadataManager.setMetadata(player, "pkCheckpoint", Integer.parseInt(sign.getLine(1)));
            player.sendMessage(ChatColor.GREEN + "Checkpoint set.");
        } else if (action.equals("Complete") && isPlaying(player)) {
            onComplete(player);
            removePlayer(player);
        }
    }

    protected void onComplete(Player player) {
        Core.broadcast(ChatColor.AQUA + player.getName() + ChatColor.GREEN + " has completed the " + Parties.getParty().getName() + " Party parkour!");
        player.sendTitle(new Title(ChatColor.GOLD + "Congratulations!"));
    }

    protected void addCheckPoint(double x, double y, double z, float yaw, float pitch) {
        checkpoints.add(new Location(Parties.getPartyWorld(), x, y, z, yaw, pitch));
    }
}
