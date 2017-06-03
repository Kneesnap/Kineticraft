package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.QueryTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Teleport to a player's last death point.
 *
 * Created by Kneesnap on 6/2/2017.
 */
public class CommandDeathTeleport extends StaffCommand {
    public CommandDeathTeleport() {
        super("<player> [times]", "Teleport to a player's death location.", "death", "backtp");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        int deathId = args.length > 1 ? Integer.parseInt(args[1]) : 1;

        QueryTools.getData(args[0], kcPlayer -> {
            if (deathId <= 0 || deathId > kcPlayer.getDeaths().size()) {
                sender.sendMessage(ChatColor.RED + "Death " + deathId + " not found.");
                return;
            }

            Location tp = kcPlayer.getDeaths().getValues().get(deathId - 1).getLocation();
            Bukkit.getScheduler().runTask(Core.getInstance(), () -> ((Player) sender).teleport(tp));
        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }
}
