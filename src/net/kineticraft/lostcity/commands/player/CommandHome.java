package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.utils.Utils;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * CommandHome - Teleport to your home.
 * TODO: Remove the admin code from this once the manager gui is added.
 *
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

        if (args[0].contains(":") && kcPlayer.getRank().isStaff()) {
            String[] split = args[0].split(":");
            QueryTools.getData(split[0], k -> {
                if (split.length == 1) {
                    listHomes(sender, k);
                } else {
                    String homeName = IntStream.range(1, split.length).mapToObj(i -> split[i]).collect(Collectors.joining(":"));
                    if (homeName.equalsIgnoreCase("bed")) {
                        Location bedSpawn = player.getBedSpawnLocation();
                        if (bedSpawn == null) {
                            sender.sendMessage(ChatColor.RED + k.getUsername() + " doesn't have a bed set.");
                        } else {
                            Utils.teleport(player, k.getUsername() + "'s Bed", player.getBedSpawnLocation());
                        }
                    } else if (k.getHomes().containsKey(homeName)) {
                        Utils.teleport(player, k.getUsername() + "'s home named \"" + homeName + "\"", k.getHomes().get(homeName).getLocation());
                    } else {
                        sender.sendMessage(ChatColor.RED + k.getUsername() + " doesn't have a home named \"" + homeName + "\"");
                    }
                }
            }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
            return;
        }

        if (args[0].equalsIgnoreCase("bed")) {
            Location bedSpawn = player.getBedSpawnLocation();
            if (bedSpawn == null) {
                sender.sendMessage(ChatColor.RED + "You don't have a bed set.");
            } else {
                Utils.teleport(player, "Bed", player.getBedSpawnLocation());
            }
            return;
        }

        if (kcPlayer.getHomes().containsKey(args[0])) {
            Utils.teleport(player, "Home", kcPlayer.getHomes().get(args[0]).getLocation());
            return;
        }

        showUsage(sender);
    }

    private static void listHomes(CommandSender sender, KCPlayer target) {
        sender.sendMessage(target.getHomes().keySet().stream().map(s -> ChatColor.GREEN + s)
                .collect(Collectors.joining(ChatColor.GRAY + ", ", ChatColor.GRAY + "Homes: ", "")));
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);

        KCPlayer kcPlayer = KCPlayer.getWrapper(sender);
        listHomes(sender, kcPlayer);
        if (((Player) sender).getBedSpawnLocation() != null)
            sender.sendMessage(ChatColor.GRAY + "You can access your bed with " + ChatColor.DARK_PURPLE + "/home bed");
    }
}
