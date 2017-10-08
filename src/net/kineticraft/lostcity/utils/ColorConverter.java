package net.kineticraft.lostcity.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.stream.Stream;

/**
 * Convert color formats.
 * Created by Kneesnap on 6/12/2017.
 */
@Getter @AllArgsConstructor
public enum ColorConverter {
    BLACK(DyeColor.BLACK),
    DARK_BLUE(DyeColor.BLUE),
    DARK_GREEN(DyeColor.GREEN),
    DARK_AQUA(DyeColor.CYAN),
    DARK_RED(DyeColor.RED),
    DARK_PURPLE(DyeColor.PURPLE),
    GOLD(DyeColor.ORANGE, DyeColor.BROWN, "Orange"),
    GRAY(DyeColor.GRAY),
    DARK_GRAY(DyeColor.GRAY),
    BLUE(DyeColor.BLUE),
    GREEN(DyeColor.LIME),
    AQUA(DyeColor.LIGHT_BLUE),
    RED(DyeColor.RED),
    LIGHT_PURPLE(DyeColor.MAGENTA, DyeColor.PINK, "Pink"),
    YELLOW(DyeColor.YELLOW),
    WHITE(DyeColor.SILVER, DyeColor.WHITE, null);

    private final DyeColor dye;
    private final DyeColor alternate; // This is the choice we prefer not to use, unless needed.
    private final String altName;

    ColorConverter(DyeColor dye) {
        this(dye, null, null);
    }

    public ChatColor getChat() {
        return ChatColor.valueOf(name());
    }

    public static ColorConverter getColor(ChatColor c) {
        return Stream.of(values()).filter(cl -> cl.getChat() == c).findAny().orElse(null);
    }

    public static ColorConverter getDye(DyeColor dc) {
        return Stream.of(values()).filter(c -> c.getDye() == dc || c.getAlternate() == dc).findAny().orElse(null);
    }

    /**
     * Get the colored name of this color.
     * @return displayName
     */
    public String getDisplayName() {
        return getChat() + (getAltName() != null ? getAltName() : Utils.capitalize(name()));
    }
}