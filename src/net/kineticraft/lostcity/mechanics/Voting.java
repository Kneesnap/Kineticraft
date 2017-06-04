package net.kineticraft.lostcity.mechanics;

import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.Data;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.configs.VoteConfig;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Handles players voting for the server.
 *
 * Created by Kneesnap on 6/3/2017.
 */
public class Voting extends Mechanic {

    @Override
    public void onJoin(Player player) {
        giveRewards(player); // Give the player any rewards they got while offline.
    }

    @EventHandler
    public void handleVote(VotifierEvent evt) {
        handleVote(evt.getVote().getUsername());
    }

    /**
     * Handle a player voting.
     * @param username
     */
    public static void handleVote(String username) {
        // Announce the vote.
        ComponentBuilder cb = new ComponentBuilder(username).color(ChatColor.YELLOW)
                .append(" voted and received a reward! Vote ").color(ChatColor.GRAY).append("HERE").underlined(true)
                .bold(true).event(new ClickEvent(ClickEvent.Action.OPEN_URL, Configs.getMainConfig().getVoteURL()));
        Bukkit.broadcast(cb.create());

        QueryTools.getData(username, player ->  {
            player.setPendingVotes(player.getPendingVotes() + 1);
            giveRewards(player.getPlayer());
        });
    }

    /**
     * Give a player their pending vote rewards.
     * @param player
     */
    public static void giveRewards(Player player) {
        KCPlayer p = KCPlayer.getWrapper(player);
        int pending = p.getPendingVotes();

        if (pending <= 0)
            return; // They don't have any votes.

        player.sendMessage(ChatColor.GOLD + "Receiving " + ChatColor.YELLOW + pending + ChatColor.GOLD
                + " vote reward" + (pending > 1 ? "s" : "") + ".");

        p.setPendingVotes(0);
        for (int i = 0; i < pending; i++) {
            ItemStack reward = generateReward();
            Utils.giveItem(player, reward);
            player.sendMessage(ChatColor.GOLD + "You received " + ChatColor.YELLOW + reward.getAmount() + "x"
                    + Utils.getItemName(reward));
        }
    }

    /**
     * Generate a random vote reward.
     * @return
     */
    public static ItemStack generateReward() {
        return new ItemStack(Material.DIRT); // TODO
    }

    /**
     * Reset monthly vote count.
     */
    public static void resetVotes() {
        VoteConfig data = Configs.getVoteData();

        QueryTools.queryData(players -> {
            players.forEach(p -> {
                if (p.getMonthlyVotes() <= 0)
                    return;
                p.setMonthlyVotes(0);
                p.writeData();
            });

            data.setTopVoter(null);
            Core.announce("Votes have reset for the month of "
                    + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
                    + "! Better start voting to get top voter! (.vote)");
        });
    }

    /**
     * Recalculate the top voter for this month.
     */
    public static void calculateTopVoter() {
        VoteConfig data = Configs.getVoteData();

        QueryTools.queryData(players -> {
            KCPlayer topVoter = players.sorted(Comparator.comparing(KCPlayer::getMonthlyVotes)).collect(Collectors.toList()).get(0);
            if (topVoter.getUuid().equals(data.getTopVoter()))
                return; // The top voter hasn't changed.

            data.setTopVoter(topVoter.getUuid());
            Core.announce(ChatColor.YELLOW + topVoter.getUsername() + ChatColor.RED
                    + " is the new top voter! Votes: " + ChatColor.YELLOW + topVoter.getMonthlyVotes());

            if (topVoter.isOnline()) {
                Player player = topVoter.getPlayer();
                player.sendMessage(ChatColor.LIGHT_PURPLE + " * You are now the top voter this month. *");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
            }
        });
    }

    @Data
    public class VoteReward {
        private int chance;
        private ItemStack item;
    }
}
