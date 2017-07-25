package net.kineticraft.lostcity.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Differentiate command types.
 * Created by Kneesnap on 5/29/2017.
 */
@AllArgsConstructor @Getter
public enum CommandType {

    DISCORD("/"),
    CHAT("."),
    TRIGGER("/trigger "),
    SLASH("/"),
    COMMAND_BLOCK("");

    private final String prefix;

    /**
     * Is the given command input possibly this command type?
     * Returns true if the command prefix matches.
     * @param input
     * @return matches
     */
    public boolean matches(String input) {
        return input.startsWith(getPrefix());
    }
}
