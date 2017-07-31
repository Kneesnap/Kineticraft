package net.kineticraft.lostcity.discord;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.core.entities.*;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.commands.DiscordCommand;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Allows automated voting on server propositins.
 * Created by Kneesnap on 6/29/2017.
 */
public class CommandServerVote extends DiscordCommand {

    private static final float STAFF_NEEDED = .5F; // 50% of staff need to vote yes.
    private static final int MAX_VOTE_HOURS = 24;

    public CommandServerVote() {
        super(EnumRank.TRIAL, "<bill>", "Initiate a server proposal.", "vote");
        setDeleteMessage(true);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(),
                CommandServerVote::scanChannel, 0L, 20 * 60 * 5L);
    }

    @Override
    protected void showUsage(CommandSender sender) {
        super.showUsage(sender);
        sender.sendMessage("Please format your bill in a way that describes the action taken if the bill is passed.");
    }

    @Override
    protected void onCommand(DiscordSender sender, String[] args) {
        Date end = Date.from(Instant.ofEpochSecond((System.currentTimeMillis() / 1000) + (MAX_VOTE_HOURS * 60 * 60)));
        DiscordAPI.sendMessage(DiscordChannel.ORYX, "@everyone " + sender.getName()
                + " has issued a proposal: ``" + String.join(" ", args) + "``");
        DiscordAPI.getBot().sendMessage(DiscordChannel.ORYX, VoteResult.UNDETERMINED.getStatus(new HashMap<>(), end),
                m -> Arrays.stream(VoteResult.values()).forEach(v -> v.react(m)));
    }

    /**
     * Get the date format. We don't have a constant because SimpleDateFormat is not thread-safe if used in a different
     * thread than where it was created.
     * @return format
     */
    private static DateFormat getFormat() {
        return new SimpleDateFormat("(MM/dd) h:mm a", Locale.US);
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
        List<Role> staffRoles = Arrays.stream(EnumRank.values()).filter(EnumRank::isStaff).map(EnumRank::getName)
                .map(DiscordAPI::getRole).filter(Objects::nonNull).collect(Collectors.toList());
        return (int) DiscordAPI.getServer().getMembers().stream().map(Member::getRoles)
                .filter(m -> Utils.containsAny(m, staffRoles)).count();
    }

    /**
     * Scan the channel to update all existing proposals.
     */
    @SuppressWarnings("ConstantConditions")
    public static void scanChannel() {
        if (!DiscordAPI.isAlive())
            return;

        List<Message> messages = DiscordChannel.ORYX.getChannel().getHistory().retrievePast(75).complete();
        Collections.reverse(messages); // Oldest to newest.

        String bill = null;
        while (!messages.isEmpty()) {
            Message temp = messages.remove(0);

            try {
                if (temp.getContent().contains(" votes needed by tomorrow")) {
                    Date expiry = Date.from(Instant.ofEpochMilli(temp.getCreationTime().toInstant().toEpochMilli()
                            + (60L * 60 * 1000 * MAX_VOTE_HOURS)));

                    // Load votes.
                    Map<VoteResult, Integer> votes = new HashMap<>();
                    Arrays.stream(VoteResult.values()).forEach(vr -> votes.put(vr, updateReaction(temp, vr)));

                    VoteResult result = VoteResult.getResult(votes, expiry);

                    temp.editMessage(result.getStatus(votes, expiry)).queue();
                    result.announce(bill);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            bill = temp.getContent();
            if (bill.contains("``"))
                bill = bill.split("``")[1];
        }
    }

    /**
     * Update the bot's reaction to a message.
     * @param reaction
     * @return
     */
    private static int updateReaction(Message message, VoteResult reaction) {
        if (reaction.getIcon() == null)
            return 0;

        MessageReaction mr = message.getReactions().stream()
                .filter(r -> r.getEmote().getName().equalsIgnoreCase(reaction.getIcon())).findAny().orElse(null);

        int count = mr != null ? mr.getCount() : 0;
        if (mr == null || mr.getCount() == 0) {
            message.addReaction(reaction.getIcon()).queue();
        } else {
            List<User> users = mr.getUsers().complete(); // It's ok to run this blocking because this is on an async thread.
            if (users.contains(DiscordAPI.getUser()) && mr.getCount() > 1) {
                mr.removeReaction().queue();
                count--;
            }
        }

        return count;
    }

    @AllArgsConstructor @Getter
    private enum VoteResult {
        PASS("✅"),
        FAIL("❌"),
        UNDETERMINED(null);

        private final String icon;

        /**
         * Add this as a default reaction, so players can click on it.
         */
        public void react(Message m) {
            if (getIcon() != null)
                m.addReaction(getIcon()).queue();
        }

        /**
         * Get the friendly display of this result.
         * @return display
         */
        public String getDisplay() {
            return name().toLowerCase() + "ed";
        }

        /**
         * Get the status message of this vote.
         * @param votes
         * @param expiry - When will this vote end?
         * @return status
         */
        public String getStatus(Map<VoteResult, Integer> votes, Date expiry) {
            int yes = votes.getOrDefault(PASS, 0);
            return this == UNDETERMINED ?
                    (getStaffNeeded() - yes) + " votes needed by tomorrow " + getFormat().format(expiry) + "."
                    : getDisplay().toUpperCase() + " (" + yes + "-" + votes.getOrDefault(FAIL, 0) + ")";
        }

        /**
         * Announce the result of a bill.
         */
        public void announce(String bill) {
            if (getIcon() != null)
                DiscordAPI.sendMessage(DiscordChannel.ORYX, getIcon() + " ``" + bill + "`` has " + getDisplay() + ".");
        }

        /**
         * Get the result of a vote.
         * @param votes
         * @param expiry
         * @return result
         */
        public static VoteResult getResult(Map<VoteResult, Integer> votes, Date expiry) {
            int yes = votes.getOrDefault(PASS, 0);
            int no = votes.getOrDefault(FAIL, 0);

            boolean expire = expiry.before(Calendar.getInstance().getTime());

            if (yes >= getStaffNeeded() || (expire && yes >= no))
                return PASS;
            if (no >= getTotalStaff() - getStaffNeeded() || expire)
                return FAIL;

            return UNDETERMINED;
        }
    }
}
