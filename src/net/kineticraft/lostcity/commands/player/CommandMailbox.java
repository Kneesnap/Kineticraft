package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.dungeons.Dungeons;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Pickup items in your mailbox
 * Created by Kneesnap on 7/17/2017.
 */
public class CommandMailbox extends PlayerCommand {

    public CommandMailbox() {
        super("[player]", "Receive mail from the server.", "mailbox");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (args.length > 0 && Utils.getRank(sender).isAtLeast(EnumRank.MOD)) {
            ItemStack send = ((Player) sender).getEquipment().getItemInMainHand();
            if (Utils.isAir(send)) {
                sender.sendMessage(ChatColor.RED + "You must hold an item to mail it.");
                return;
            }

            if (args[0].equalsIgnoreCase("all") || args[0].equals("*")) {
                QueryTools.queryData(s -> {
                    s.forEach(kc -> {
                        kc.getMailbox().add(send);
                        kc.updatePlayer();
                        kc.writeData();
                    });
                    sender.sendMessage(ChatColor.GREEN + "Item has been mass-mailed.");
                });
            } else {
                QueryTools.getData(args[0], k -> {
                    k.getMailbox().add(send);
                    k.updatePlayer();
                    sender.sendMessage(ChatColor.GREEN + "Mailed item to " + k.getUsername() + ".");
                }, () -> sender.sendMessage(ChatColor.RED + "Player not found."));
            }
            return;
        }

        if (Dungeons.isDungeon((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "This command cannot be run in a dungeon.");
            return;
        }

        KCPlayer kc = KCPlayer.getPlayer((Player) sender);

        if (kc.getMailbox().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You have no items in the mail.");
            return;
        }

        kc.getMailbox().forEach(i -> {
            sender.sendMessage(ChatColor.GOLD + "You received " + ChatColor.YELLOW + i.getAmount() + "x" + Utils.getItemName(i));
            Utils.giveItem((Player) sender, i);
        });

        kc.getMailbox().clear();
    }
}
