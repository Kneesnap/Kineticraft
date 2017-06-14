package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Rankup to the next rank, if possible.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandRankup extends PlayerCommand {

    public CommandRankup() {
        super("", "Advance to the next rank.", "rankup");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer player = KCPlayer.getWrapper((Player) sender);

        if (player.getRank().isAtLeast(EnumRank.OMEGA)) {
            sender.sendMessage(ChatColor.RED + "You cannot rankup further.");
            if (player.getRank() == EnumRank.OMEGA)
                sender.sendMessage(ChatColor.RED + "You can rankup to rank Theta by donating with /donate.");
            return;
        }

        EnumRank nextRank = EnumRank.values()[player.getRank().ordinal() + 1];
        int secondsNeeded = nextRank.getHoursNeeded() * 60 * 60;

        if (secondsNeeded > player.getSecondsPlayed()) {
            int time = (secondsNeeded - player.getSecondsPlayed()) * 1000;
            sender.sendMessage(ChatColor.RED + "You must play " + Utils.formatTime(time) + " more first.");
            return;
        }

        int accomplishments = 0; //TODO - Get once 1.12 is implemented.
        if (nextRank.getAccomplishmentsNeeded() > accomplishments) {
            sender.sendMessage(ChatColor.RED + "You need to complete "
                    + (nextRank.getAccomplishmentsNeeded() - accomplishments) + " more accomplishments.");
            return;
        }

        player.setRank(nextRank);
    }
}
