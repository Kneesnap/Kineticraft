package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.PlayerDeath;
import net.kineticraft.lostcity.data.QueryTools;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Teleport to a player's last death point.
 * Created by Kneesnap on 6/2/2017.
 */
public class CommandDeathTeleport extends StaffCommand {
    public CommandDeathTeleport() {
        super("<player> [death] [restore]", "Teleport to a player's death location.", "death", "backtp");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        int deathId = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        boolean restore = args.length > 2 && args[2].equalsIgnoreCase("restore");

        QueryTools.getData(args[0], kcPlayer -> {
            if (!kcPlayer.getDeaths().hasIndex(deathId - 1)) {
                sender.sendMessage(ChatColor.RED + "Death " + deathId + " not found.");
                return;
            }

            PlayerDeath death = kcPlayer.getDeaths().getValues().get(deathId - 1);
            if (restore && kcPlayer.isOnline()) {
                death.restore(kcPlayer.getPlayer());
                sender.sendMessage(ChatColor.GREEN + "Restored " + kcPlayer.getUsername() + "'s death point.");
            } else {
                ((Player) sender).teleport(death.getLocation());
                sender.sendMessage(ChatColor.GREEN + "Teleported to " + kcPlayer.getUsername() + "'s death point.");
            }
        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage(ChatColor.RED + "The 3 most recent deaths are recorded.");
    }
}
