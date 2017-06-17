package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allows players to choose a nickname.
 *
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandNick extends PlayerCommand {

    public CommandNick() {
        super(EnumRank.OMEGA, true, "<nick|off>", "Change your nickname.", "nick");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer p = KCPlayer.getWrapper((Player) sender);

        if (args[0].length() > 12) {
            sender.sendMessage(ChatColor.RED + "Nickname too long.");
            return;
        }

        if (args[0].equalsIgnoreCase("off")) {
            p.setNickname(null);
            sender.sendMessage(ChatColor.GOLD + "Nickname removed.");
        } else {

            if (args[0].length() < 4) {
                sender.sendMessage(ChatColor.RED + "Nickname too short.");
                return;
            }

            p.setNickname(ChatColor.translateAlternateColorCodes('&', args[0]));
            sender.sendMessage(ChatColor.GOLD + "Nickname set.");
        }

        p.updatePlayer();
    }
}
