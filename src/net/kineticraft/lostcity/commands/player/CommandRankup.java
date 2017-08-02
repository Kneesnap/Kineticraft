package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Rankup to the next rank, if possible.
 * Created by Kneesnap on 6/10/2017.
 */
public class CommandRankup extends PlayerCommand {

    public CommandRankup() {
        super("", "Advance to the next rank.", "rankup");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer player = KCPlayer.getWrapper(sender);

        if (player.getRank().isAtLeast(EnumRank.OMEGA)) {
            sender.sendMessage(ChatColor.RED + "You cannot rankup further.");
            if (player.getRank() == EnumRank.OMEGA)
                sender.sendMessage(ChatColor.RED + "However, you may rankup to rank Theta by donating with /donate.");
            return;
        }

        EnumRank nextRank = EnumRank.values()[player.getRank().ordinal() + 1];
        int secondsNeeded = nextRank.getHoursNeeded() * 60 * 60;

        if (secondsNeeded > player.getSecondsPlayed()) {
            long time = (secondsNeeded - player.getSecondsPlayed()) * 1000;
            sender.sendMessage(ChatColor.RED + "You must play " + Utils.formatTime(time) + " more first.");
            return;
        }

        int accomplishments = getAdvancements((Player) sender);
        if (nextRank.getAccomplishmentsNeeded() > accomplishments) {
            sender.sendMessage(ChatColor.RED + "You need to complete "
                    + (nextRank.getAccomplishmentsNeeded() - accomplishments) + " more accomplishments.");
            return;
        }

        player.setRank(nextRank);
    }

    /**
     * Ranks the player up to the next rank, if possible.
     * @param p
     */
    public static void silentRankup(Player p) {
        KCPlayer player = KCPlayer.getWrapper(p);
        if (player.getRank().isAtLeast(EnumRank.OMEGA))
            return; // can't rankup further.

        EnumRank nextRank = EnumRank.values()[player.getRank().ordinal() + 1];
        if (player.getSecondsPlayed() >= nextRank.getHoursNeeded() * 60 * 60
                && getAdvancements(p) >= nextRank.getAccomplishmentsNeeded())
            player.setRank(nextRank);
    }

    /**
     * Return the number of advancements the player has completed.
     * @param player
     * @return count
     */
    private static int getAdvancements(Player player) {
        List<Advancement> advancements = new ArrayList<>();
        Bukkit.advancementIterator().forEachRemaining(advancements::add);
        return (int) advancements.stream().map(player::getAdvancementProgress).filter(AdvancementProgress::isDone).count();
    }
}
