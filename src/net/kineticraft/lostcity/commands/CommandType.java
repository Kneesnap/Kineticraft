package net.kineticraft.lostcity.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Differentiate command types.
 *
 * Created by Kneesnap on 5/29/2017.
 */
@AllArgsConstructor @Getter
public enum CommandType {

    CHAT("."),
    TRIGGER("/trigger "),
    SLASH("/");
    //COMMAND_BLOCK("");

    private final String prefix;
}
