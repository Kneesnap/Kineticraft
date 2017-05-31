package net.kineticraft.lostcity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.Arrays;

/**
 * EnumRank - Basic data about each player rank.
 *
 * Created by Kneesnap on 5/29/2017.
 */
@AllArgsConstructor @Getter
public enum EnumRank {

    MU("μ", ChatColor.LIGHT_PURPLE),
    PHI("Φ", ChatColor.BLUE),
    SIGMA("Σ", ChatColor.AQUA),
    GAMMA("Γ", ChatColor.GREEN),
    BETA("β", ChatColor.YELLOW),
    ALPHA("α", ChatColor.RED),
    OMEGA("Ω", ChatColor.DARK_RED),
    THETA("Θ", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE),
    HELPER("HLPR", ChatColor.DARK_GRAY, ChatColor.DARK_GRAY),
    BUILDER("BLD", ChatColor.GOLD, ChatColor.YELLOW),
    MOD("MOD", ChatColor.DARK_GREEN, ChatColor.GREEN),
    ADMIN("ADMN", ChatColor.DARK_RED,  ChatColor.RED),
    DEV("DEV", ChatColor.DARK_AQUA, ChatColor.AQUA);

    private String rankSymbol;
    private final ChatColor color;
    private final ChatColor nameColor;

    EnumRank(String rankSymbol, ChatColor color) {
        this(rankSymbol, color, ChatColor.GRAY);
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
        return getColor() + (isAtLeast(HELPER) ? ChatColor.BOLD.toString() : "") + getRankSymbol() + getNameColor();
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
     * Get a rank by its name.
     * @param name
     * @return
     */
    public static EnumRank getByName(String name) {
        return Arrays.stream(values()).filter(rank -> rank.name().equalsIgnoreCase(name)).findAny().orElse(null);
    }
}
