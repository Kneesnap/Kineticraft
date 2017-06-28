package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.guis.staff.GUIPunish;
import net.kineticraft.lostcity.mechanics.Punishments;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allow punishing players.
 *
 * Created by Kneesnap on 6/17/2017.
 */
public class CommandPunish extends StaffCommand {

    public CommandPunish() {
        super("<player> [offense]", "Punish a player.", "punish");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {

        // Get Player
        QueryTools.getData(args[0], p -> {
            if (args.length == 1) {
                new GUIPunish((Player) sender, p);
            } else {
                p.punish(Punishments.PunishmentType.valueOf(args[1].toUpperCase()), sender);
            }
        }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
    }
}
