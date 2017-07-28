package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.mechanics.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allows players to choose a nickname.
 * Created by Kneesnap on 6/16/2017.
 */
public class CommandNick extends PlayerCommand {

    public CommandNick() {
        super(EnumRank.OMEGA, "<nick|off>", "Change your nickname.", "nick", "nickname");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {

        KCPlayer p = KCPlayer.getWrapper(sender);
        String newNick = Chat.applyColor(sender, args[0]);
        String noColor = ChatColor.stripColor(newNick);

        // Disable nick.
        if (args[0].equalsIgnoreCase("off")) {
            p.setNickname(null);
            return;
        }

        // Nick size checks.
        if (noColor.length() > 12) {
            sender.sendMessage(ChatColor.RED + "Nickname too long.");
            return;
        }

        if (noColor.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Nickname too short.");
            return;
        }

        QueryTools.getData(noColor, d -> {
            if (!p.getUuid().equals(d.getUuid())) {
                sender.sendMessage(ChatColor.RED + "You cannot use the name of another player.");
                return;
            }

            p.setNickname(newNick);
        }, () -> p.setNickname(newNick));
    }
}
