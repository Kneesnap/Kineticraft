package net.kineticraft.lostcity.events;

import lombok.Getter;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.mechanics.system.MechanicManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.stream.Stream;

/**
 * Called when plugins should register their game-mechanics.
 * Created by Kneesnap on 7/22/2017.
 */
public class MechanicRegisterEvent extends Event {
    @Getter private static final HandlerList handlerList = new HandlerList();

    /**
     * Register mechanic(s).
     * @param m
     */
    public void register(Class<? extends Mechanic> m) {
        Stream.of(m).forEach(MechanicManager::addMechanic);
    }

    /**
     * Register mechanic(s).
     * @param m
     */
    public void register(Mechanic... m) {
        Stream.of(m).forEach(MechanicManager::addMechanic);
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
