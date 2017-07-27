package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.guis.staff.GUIPunish;
import net.kineticraft.lostcity.mechanics.Punishments;
import net.kineticraft.lostcity.mechanics.Punishments.PunishmentType;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Allow punishing players.
 * Created by Kneesnap on 6/17/2017.
 */
public class CommandPunish extends StaffCommand {

    public CommandPunish() {
        super("<player> [offense]", "Punish a player.", "punish");
        autocompleteOnline();
        autocomplete(PunishmentType.values());
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1 && !(sender instanceof Player)) {
            showUsage(sender);
            return;
        }

        // Get Player
        QueryTools.getData(args[0], p -> {
            if (args.length == 1) {
                new GUIPunish((Player) sender, p);
            } else {
                p.punish(Punishments.PunishmentType.valueOf(args[1].toUpperCase()), sender);
            }
        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage(Arrays.stream(PunishmentType.values()).map(PunishmentType::name).map(Utils::capitalize)
                .collect(Collectors.joining(ChatColor.RED + ", " + ChatColor.YELLOW,
                        ChatColor.RED + "Punishments: " + ChatColor.YELLOW, "")));
    }
}
