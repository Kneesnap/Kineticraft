package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.QueryTools;
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
        super(EnumRank.OMEGA, true, "<nick|off>", "Change your nickname.", "nick", "nickname");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer p = KCPlayer.getWrapper((Player) sender);
        String newNick = ChatColor.translateAlternateColorCodes('&', args[0]);

        if (ChatColor.stripColor(newNick).length() > 12) {
            sender.sendMessage(ChatColor.RED + "Nickname too long.");
            return;
        }

        if (args[0].equalsIgnoreCase("off")) {
            p.setNickname(null);
            sender.sendMessage(ChatColor.GOLD + "Nickname removed.");
            p.updatePlayer();
        } else {

            if (ChatColor.stripColor(newNick).length() < 4) {
                sender.sendMessage(ChatColor.RED + "Nickname too short.");
                return;
            }

            QueryTools.getData(args[0], d -> {
                if (p.getUuid() == d.getUuid()) {
                    setNick(p, newNick);
                } else {
                    sender.sendMessage(ChatColor.RED + "You cannot use the name of another player.");
                }
            }, () -> {
                setNick(p, newNick);
            });
        }
    }

    private static void setNick(KCPlayer player, String newNick) {
        player.setNickname(newNick);
        player.getPlayer().sendMessage(ChatColor.GOLD + "Nickname set.");
        player.updatePlayer();
    }
}
