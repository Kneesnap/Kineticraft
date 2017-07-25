package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.utils.Utils;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

/**
 * CommandHome - Teleport to your home.
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandHome extends PlayerCommand {
    public CommandHome() {
        super("<home>", "Teleport home.", "home");
        autocomplete(p -> KCPlayer.getWrapper(p).getHomes().keySet());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        KCPlayer kcPlayer = KCPlayer.getWrapper(player);

        if (args[0].equalsIgnoreCase("bed") && player.getBedSpawnLocation() != null) {
            Utils.teleport(player, "Bed", player.getBedSpawnLocation());
            return;
        }

        if (!kcPlayer.getHomes().containsKey(args[0])) {
            showUsage(sender);
            return;
        }

        Utils.teleport(player, "Home", kcPlayer.getHomes().get(args[0]));
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);

        sender.sendMessage(KCPlayer.getWrapper(sender).getHomes().keySet().stream().map(s -> ChatColor.GREEN + s)
                .collect(Collectors.joining(ChatColor.GRAY + ", ", ChatColor.GRAY + "Homes: ", "")));
        if (((Player) sender).getBedSpawnLocation() != null)
            sender.sendMessage(ChatColor.GRAY + "You can access your bed with " + ChatColor.RED + "/home bed");
    }
}
