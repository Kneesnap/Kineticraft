package net.kineticraft.lostcity.mechanics;

import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.configs.VoteConfig;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.Dog;
import net.kineticraft.lostcity.utils.TextBuilder;
import net.kineticraft.lostcity.utils.TimeInterval;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles players voting for the server.
 * Created by Kneesnap on 6/3/2017.
 */
public class Voting extends Mechanic {

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> Bukkit.getOnlinePlayers().stream().map(KCPlayer::getWrapper)
                .filter(k -> System.currentTimeMillis() - k.getLastVote() > 24 * 60 * 60 * 1000)
                .forEach(p -> p.getPlayer().sendMessage(ChatColor.AQUA + "You have not voted recently, please support us with "
                        + ChatColor.YELLOW + "/vote" + ChatColor.AQUA + ".")), 0L, 5 * 60 * 20L + 1);
        Utils.runCalendarTaskEvery(TimeInterval.MONTH, Voting::resetVotes);
    }

    @EventHandler
    public void onVote(VotifierEvent evt) {
        handleVote(evt.getVote().getUsername());
    }

    /**
     * Handle a player voting.
     * @param username
     */
    public static void handleVote(String username) {
        TextBuilder textBuilder = new TextBuilder(username).color(ChatColor.AQUA)
                .append(" voted and received a reward! Vote ").color(ChatColor.GRAY).append("HERE").underline().bold()
                .openURL(Configs.getMainConfig().getVoteURL()).color(ChatColor.AQUA);
        Core.broadcast(textBuilder.create());

        VoteConfig data = Configs.getVoteData();
        data.setTotalVotes(data.getTotalVotes() + 1); // Increment the total vote count

        int toParty = data.getVotesUntilParty();
        if (toParty > 0) {
            if (toParty % 5 == 0 || toParty <= 10)
                Dog.KINETICA.say("Thanks for voting " + ChatColor.YELLOW + username + ChatColor.WHITE + "! We need "
                        + ChatColor.YELLOW + toParty + ChatColor.WHITE + " more votes for a party.");
        } else {
            doVoteParty();
        }

        QueryTools.getData(username, player -> {
            if (!username.equalsIgnoreCase(player.getUsername()))
                return; // Players are using a short-hand version of their name to get the reward twice.

            player.setMonthlyVotes(player.getMonthlyVotes() + 1);
            player.setPendingVotes(player.getPendingVotes() + 1);
            player.setLastVote(System.currentTimeMillis());
            if (player.isOnline())
                giveRewards(player.getPlayer());
        });

        Configs.getVoteData().saveToDisk();
    }

    /**
     * Activate a vote party.
     */
    public static void doVoteParty() {
        Dog.KINETICA.say("Wooo! We made it! Parrrty!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack reward = generatePartyReward();
            Utils.giveItem(player, reward);
            player.sendMessage(ChatColor.GOLD + "You received " + ChatColor.YELLOW + reward.getAmount() + "x"
                    + Utils.getItemName(reward) + ChatColor.GOLD + " from the vote party.");
        }
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

        for (int i = 0; i < pending; i++) {
            Configs.getVoteData().getNormal().forEach(j -> Utils.giveItem(player, j));
            player.setLevel(player.getLevel() + 10); // Add 10 XP Levels
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);

        // Give achievement rewards
        for (VoteAchievement va : Configs.getVoteData().getAchievements()) {
            if (va.getVotesNeeded() > p.getTotalVotes() && p.getTotalVotes() + pending >= va.getVotesNeeded()) {
                Utils.giveItem(player, va.getItem());
                player.sendMessage(ChatColor.GREEN + "You have received a special reward for voting "
                        + va.getVotesNeeded() + " times.");
            }
        }

        p.setTotalVotes(p.getTotalVotes() + pending);
        p.setPendingVotes(0);
        calculateTopVoter();
    }

    /**
     * Generate a random vote party reward.
     * @return item
     */
    public static ItemStack generatePartyReward() {
        VoteConfig data = Configs.getVoteData();
        if (data.getParty().isEmpty()) // No vote rewards.
            return ItemManager.createItem(Material.DIRT, ChatColor.RED + "Vote Party Error", "This should only happen if there are no vote rewards.");

        PartyReward test = Utils.randElement(data.getParty()); // Get the reward we'll attempt to give them.
        return Utils.randChance(test.getChance()) ? test.getItem() : generatePartyReward();
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
                p.setPendingVotes(0);
                p.writeData();
            });

            data.setTopVoter(null);
            Core.announce("Votes have reset for the month of " + getMonthName() + "! Better start voting to get top voter! (/vote)");
            data.saveToDisk();
        });
    }

    /**
     * Get the name of the current month.
     * @return monthName
     */
    private static String getMonthName() {
        return Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
    }

    /**
     * Recalculate the top voter for this month.
     */
    public static void calculateTopVoter() {
        VoteConfig data = Configs.getVoteData();

        QueryTools.queryData(players -> {
            List<KCPlayer> sorted = players.filter(k -> !k.getRank().isAtLeast(EnumRank.MEDIA)).sorted(sortPlayers()).collect(Collectors.toList());
            KCPlayer topVoter = sorted.get(0);

            if (topVoter == null || topVoter.getUuid().equals(data.getTopVoter()) || topVoter.getMonthlyVotes() == 0)
                return; // The top voter hasn't changed.

            Player oldTop = Bukkit.getPlayer(data.getTopVoter());
            data.setTopVoter(topVoter.getUuid());
            Core.announce(ChatColor.YELLOW + topVoter.getUsername() + ChatColor.RED
                    + " is the new top voter! Monthly Votes: " + ChatColor.YELLOW + topVoter.getMonthlyVotes());

            // Tell new player.
            if (topVoter.isOnline()) {
                Player player = topVoter.getPlayer();
                player.sendMessage(ChatColor.LIGHT_PURPLE + " * You are now the top voter this month. *");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                topVoter.updatePlayer();
            }

            // Fix old top voter's playertab.
            if (oldTop != null)
                KCPlayer.getWrapper(oldTop).updatePlayer();

            data.saveToDisk();
        });
    }

    /**
     * Get a comparator for sorting all players.
     * @return players
     */
    public static Comparator<KCPlayer> sortPlayers() {
        return (x, y) -> {
            if (x.getMonthlyVotes() == y.getMonthlyVotes())
                return Long.compare(x.getLastVote(), y.getLastVote());
            return y.getMonthlyVotes() > x.getMonthlyVotes() ? 1 : -1;
        };
    }

    @AllArgsConstructor @Data
    public static class VoteAchievement implements Jsonable {
        private int votesNeeded;
        private ItemStack item;

        public VoteAchievement() {

        }
    }

    @AllArgsConstructor @Data
    public static class PartyReward implements Jsonable {
        private int chance;
        private ItemStack item;

        public PartyReward() {

        }
    }
}