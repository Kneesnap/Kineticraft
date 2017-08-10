package net.kineticraft.lostcity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;

/**
 * EnumRank - Basic data about each player rank.
 * Created by Kneesnap on 5/29/2017.
 */
@AllArgsConstructor @Getter
public enum EnumRank {

    MU("μ", ChatColor.LIGHT_PURPLE, 5, 3, 0, 0),
    PHI("Φ", ChatColor.BLUE, 5, 5, 1, 10),
    SIGMA("Σ", ChatColor.AQUA, 4, 10, 12, 20),
    GAMMA("Γ", ChatColor.GREEN, 4, 15, 24, 30),
    BETA("β", ChatColor.YELLOW, 3, 20, 96, 40),
    ALPHA("α", ChatColor.RED, 3, 25, 240, 50),
    OMEGA("Ω", ChatColor.DARK_RED, 2, 30, 480, 80),

    THETA("Θ", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE),
    VOTER("VOTR", ChatColor.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE), // We don't actually set players to this rank.
    MEDIA("∈", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE),

    TRIAL("JR MOD", ChatColor.GREEN, ChatColor.GREEN),
    BUILDER("BLD", ChatColor.GOLD, ChatColor.YELLOW),
    MOD("MOD", ChatColor.DARK_GREEN, ChatColor.GREEN),
    ADMIN("ADMN", ChatColor.DARK_RED,  ChatColor.RED),
    DEV("DEV", ChatColor.DARK_AQUA, ChatColor.AQUA);

    private final String rankSymbol;
    private final ChatColor color;
    private final ChatColor nameColor;
    private final int tpTime;
    private final int homes;
    private final int hoursNeeded;
    private final int accomplishmentsNeeded;

    EnumRank(String rankSymbol, ChatColor color, ChatColor nameColor) {
        this(rankSymbol, color, nameColor, 2, 50, -1, -1);
    }

    EnumRank(String rankSymbol, ChatColor color, int tpTime, int homes, int hours, int acc) {
        this(rankSymbol, color, ChatColor.GRAY, tpTime, homes, hours, acc);
    }

    /**
     * Gets the display of this rank's full name.
     * @return fullName
     */
    public String getFullName() {
        return getColor().toString() + ChatColor.BOLD + getName();
    }

    /**
     * Get the display name for this rank.
     * @return
     */
    public String getName() {
        return Utils.capitalize(name());
    }

    /**
     * Get the prefix that should show in chat for this rank.
     * @return
     */
    public String getChatPrefix() {
        return getColor() + (getRankSymbol().length() > 1 ? ChatColor.BOLD.toString() : "") + getRankSymbol() + getNameColor();
    }

    /**
     * Is this rank at least the lineage of another rank?
     * @param other
     * @return
     */
    public boolean isAtLeast(EnumRank other) {
        return ordinal() >= other.ordinal();
    }

    /**
     * Is this a staff rank?
     * @return staff
     */
    public boolean isStaff() {
        return isAtLeast(TRIAL);
    }

    /**
     * Get a rank by its name.
     * @param name
     * @return
     */
    public static EnumRank getByName(String name) {
        return Arrays.stream(values()).filter(rank -> rank.name().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    private String getTeamName() { // Letters are added alphabetically to sort ranks by priority on the tab-list.
        return (isAtLeast(THETA) ? (char) (97 + ordinal()) + Utils.capitalize(name()) : "aDefault");
    }

    /**
     * Get the scoreboard team for this rank.
     * @return team
     */
    public Team getTeam() {
        return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getTeamName());
    }

    /**
     * Create the scoreboard team for this rank.
     */
    protected void createTeam() {
        if (getTeam() != null)
            return; // This team, already exists.
        Team t = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(getTeamName());
        t.setColor(getNameColor());
        t.setPrefix(getNameColor().toString());
    }

    /**
     * Create the teams in order for each rank.
     */
    public static void createTeams() {
        // Remove all existing teams. Since they are dynamically generated this makes sure we don't have problems.
        Bukkit.getScoreboardManager().getMainScoreboard().getTeams().forEach(Team::unregister);
        Arrays.asList(values()).forEach(EnumRank::createTeam); // Create the team for each rank.
    }
}