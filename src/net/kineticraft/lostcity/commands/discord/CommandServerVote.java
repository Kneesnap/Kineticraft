package net.kineticraft.lostcity.commands.discord;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.Role;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.DiscordSender;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Allows automated voting.
 *
 * Created by Kneesnap on 6/29/2017.
 */
public class CommandServerVote extends DiscordCommand {

    private static final float STAFF_NEEDED = .5F; // 50% of staff need to vote yes.
    private static final int MAX_VOTE_HOURS = 24;

    private static final String YES = "✅";
    private static final String NO = "x"; //❌

    public CommandServerVote() {
        super("<bill>", "Initiate a server proposal.", "vote");
        setDeleteMessage(true);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(),
                CommandServerVote::scanChannel, 0L, 20 * 60 * 5L);
    }

    @Override
    protected void onCommand(DiscordSender sender, String[] args) {
        Date end = Date.from(Instant.ofEpochSecond(System.currentTimeMillis() + (MAX_VOTE_HOURS * 60 * 60 * 1000)));
        DiscordAPI.sendMessage(DiscordChannel.ORYX, sender.getName()
                + " has issued a proposal: ``" + String.join(" ", args) + "``");
        DiscordAPI.getBot().sendMessage(DiscordChannel.ORYX, makeVoteInfo(0, 0, end), m -> {
            m.addReaction(YES).queue();
            m.addReaction(NO).queue();
        });
    }

    /**
     * Get the date format. We don't have a constant because SimpleDateFormat is not thread-safe if used in a different
     * thread than where it was created.
     * @return format
     */
    private static DateFormat getFormat() {
        return new SimpleDateFormat("(MM/dd) h:mm a", Locale.ENGLISH);
    }

    /**
     * Return the number of staff needed to pass a vote.
     * @return staffNeeded
     */
    private static int getStaffNeeded() {
        return (int) (STAFF_NEEDED * getTotalStaff());
    }

    /**
     * Return the total number of staff members in discord.
     * @return totalStaff
     */
    private static int getTotalStaff() {
        List<Role> staffRoles = Arrays.stream(EnumRank.values()).filter(EnumRank::isStaff).map(EnumRank::getDiscordRole)
                .map(DiscordAPI::getRole).filter(Objects::nonNull).collect(Collectors.toList());
        return (int) DiscordAPI.getServer().getMembers().stream().map(Member::getRoles)
                .filter(m -> Utils.containsAny(m, staffRoles)).count();
    }

    /**
     * Scan the channel to update all existing proposals.
     */
    public static void scanChannel() {
        if (!DiscordAPI.isAlive())
            return;

        List<Message> messages = new ArrayList<>(DiscordChannel.ORYX.getChannel().getHistory().getRetrievedHistory());

        String bill = null;
        while (!messages.isEmpty()) {
            Message temp = messages.remove(0);

            try {
                if (temp.getContent().contains(" votes needed by tomorrow")) {
                    Date expiry = getFormat().parse(temp.getContent().substring(temp.getContent().lastIndexOf(" ") + 1).split(".")[0]);
                    int yes = updateReaction(temp, YES);
                    int no = updateReaction(temp, NO);

                    temp.editMessage(makeVoteInfo(yes, no, expiry)).queue();

                    if (yes >= getStaffNeeded()) {
                        DiscordAPI.sendMessage(DiscordChannel.ORYX, "\\:" + YES + ": " + bill + " has passed.");
                    } else if (no >= (getTotalStaff() - getStaffNeeded())) {
                        DiscordAPI.sendMessage(DiscordChannel.ORYX, "\\:" + NO + ": " + bill + " has failed.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            bill = temp.getContent();
            bill = bill.substring(bill.lastIndexOf("has issued a proposal: ") + 1).split("``")[0];
        }
    }

    /**
     * Update the bot's reaction to a message.
     * @param reaction
     * @return
     */
    private static int updateReaction(Message message, String reaction) {
        MessageReaction mr = message.getReactions().stream().filter(r -> r.getEmote().getName().equalsIgnoreCase(reaction))
                .findAny().orElse(null);

        if (mr == null || mr.getCount() == 0) {
            message.addReaction(reaction).queue();
            return 0;
        } else if (mr.getCount() >= 2) {
            mr.removeReaction(DiscordAPI.getUser()).queue();
        }

        return mr.getCount();
    }

    /**
     * Create a user-friendly string describing the current vote status.
     * @param yesVotes
     * @param noVotes
     * @param expiry
     * @return info
     */
    private static String makeVoteInfo(int yesVotes, int noVotes, Date expiry) {
        if (yesVotes >= getStaffNeeded()) {
            return "PASSED";
        } else if (noVotes >= (getTotalStaff() - getStaffNeeded())) {
            return "FAILED";
        }
        return (getStaffNeeded() - yesVotes) + " votes needed by tomorrow " + getFormat().format(expiry) + ".";
    }
}
