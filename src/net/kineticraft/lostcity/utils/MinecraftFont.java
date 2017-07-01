package net.kineticraft.lostcity.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Contains information on Minecraft's font, such as the size of each character.
 *
 * Created by Kneesnap on 6/11/2017.
 */
@AllArgsConstructor
public enum MinecraftFont {

    F("F", 5, 4),
    I("I", 3, 1),
    K("K", 5, 4),
    L("L", 5, 2),
    T("T", 5, 3),
    SQUIGLE("~", 6),
    PIPE("|", 1),
    CURLY_OPEN("{", 4),
    CURLY_SHUT("}", 4),
    OPEN_BRACKET("[", 3),
    CLOSE_BRACKET("]", 3),
    AT_SYMBOL("@", 6),
    GREAT(">", 4),
    LESS("<", 4),
    PERIOD(".", 1),
    COMMA(",", 1),
    ASTERISK("*", 4),
    GRAVE("`", 2),
    SINGLE_QUOTE("'", 2),
    OPEN_PAREN("(", 4),
    CLOSE_PAREN(")", 4),
    EXCLAMATION("!", 1),
    QUOTE("\"", 4),
    SPACE(" ", 3),
    COLON(":", 1),
    SEMI_COLON(";", 1),
    DEFAULT("NONE", 5);

    private final String character;
    private final int upperCaseSize;
    private final int lowerCaseSize;

    MinecraftFont(String character, int size) {
        this(character, size, size);
    }

    /**
     * Gets the default size of this
     * @return
     */
    public int getCharWidth() {
        return getCharWidth(character);
    }

    /**
     * Get the width of this character, dependent on case.
     * @param s
     * @return width
     */
    private int getCharWidth(String s) {
        return (s.equals(this.character) ? upperCaseSize : lowerCaseSize) + 1; // +1 for the distance between characters.
    }

    /**
     * Return the MC Character for this character.
     * @param testFor
     * @return font
     */
    private static MinecraftFont getCharacter(String testFor) {
        return Arrays.stream(values()).filter(c -> c.character.equalsIgnoreCase(testFor)).findAny().orElse(DEFAULT);
    }

    /**
     * Get the width of a given character.
     * @param character
     * @param bold
     * @return width
     */
    public static int getWidth(String character, boolean bold) {
        if (character.equals("\n"))
            return 0; // These characters are invisible.

        MinecraftFont minecraftFont = getCharacter(character);
        int size = minecraftFont.getCharWidth(character);
        if (bold && minecraftFont != SPACE)
            size++; // Bold has an extra pixel.
        return size;
    }
}
