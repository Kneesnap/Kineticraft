package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.data.lists.StringList;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Send mail to another user.
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandMail extends PlayerCommand {

    public CommandMail() {
        super("<send|clear|read> [player] [message]", "Mail a message to another player.", "mail", "send");
        autocompleteOnline(1);
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {

        if (args[0].equalsIgnoreCase("send")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /mail send <player> <message>");
                return;
            }

            String mail = ChatColor.GRAY + "[" + Utils.getSenderName(sender) + ChatColor.GRAY + "]: " + ChatColor.WHITE
                    + String.join(" ", skipArgs(args, 2));

            QueryTools.getData(args[1], p -> {
                if (p.getMail().size() >= 10) {
                    sender.sendMessage(ChatColor.RED + p.getUsername() + "'s inbox is full.");
                    return;
                }

                sender.sendMessage(ChatColor.GOLD + "Mail sent: ");
                sender.sendMessage(mail);

                if (p.isIgnoring(sender))
                    return;

                p.getMail().add(mail);
                p.updatePlayer();
            }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
        } else if (args[0].equalsIgnoreCase("clear")){
            KCPlayer.getWrapper(sender).getMail().clear();
            sender.sendMessage(ChatColor.GOLD + "Mail cleared.");
        } else {
            StringList mail = KCPlayer.getWrapper(sender).getMail();

            if (mail.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "You have no mail.");
                return;
            }

            sender.sendMessage(ChatColor.GOLD + "Mailbox:");
            mail.forEach(sender::sendMessage);
            sender.sendMessage(ChatColor.GOLD + "Clear your mail with /mail clear.");
        }
    }
}
