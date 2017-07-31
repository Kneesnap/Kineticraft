package net.kineticraft.lostcity.events;

import lombok.Getter;
import net.kineticraft.lostcity.commands.Command;
import net.kineticraft.lostcity.commands.Commands;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.stream.Stream;

/**
 * Called when Corknut commands should register.
 * Created by Kneesnap on 7/22/2017.
 */
public class CommandRegisterEvent extends Event {
    @Getter private static final HandlerList handlerList = new HandlerList();

    /**
     * Register commands.
     * @param c
     */
    public void register(Command... c) {
        Stream.of(c).forEach(Commands::addCommand);
    }

    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
