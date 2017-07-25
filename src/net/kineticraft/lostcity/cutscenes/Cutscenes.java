package net.kineticraft.lostcity.cutscenes;

import lombok.Getter;
import net.kineticraft.lostcity.data.maps.JsonMap;
import net.kineticraft.lostcity.mechanics.system.Restrict;
import net.kineticraft.lostcity.mechanics.system.BuildType;
import net.kineticraft.lostcity.mechanics.system.MechanicManager;
import net.kineticraft.lostcity.mechanics.system.ModularMechanic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Manages Cutscenes.
 * TODO Entity pathfind from A to B.
 * TODO Entity follow track.
 *
 * TODO: Show players in cutscene at startLocation.
 * //TODO Ingame editor:
 * QUEST TODO Create a particle exclamation mark for quest NPCs.
 * Created by Kneesnap on 6/1/2017.
 */
@Restrict(BuildType.PRODUCTION)
public class Cutscenes extends ModularMechanic<Cutscene> {

    @Getter private static Map<Player, CutsceneStatus> dataMap = new HashMap<>();

    public Cutscenes() {
        super("cinematics", Cutscene.class);
    }

    @Override
    public void onDisable() {
        new HashSet<>(getDataMap().values()).forEach(CutsceneStatus::finish); // Cancel existing cutscenes.
        super.onDisable();
    }

    @Override
    public void onQuit(Player player) {
        CutsceneStatus state = getState(player);
        if (state != null)
            state.finish(player);
    }

    @EventHandler(ignoreCancelled = true) // Prevent dismounting from the camera mount.
    public void onSneak(PlayerToggleSneakEvent evt) {
        evt.setCancelled(isWatching(evt.getPlayer()) && evt.isSneaking());
    }

    /**
     * Is the given player currently watching a cutscene?
     * @param player
     * @return isWatching
     */
    public static boolean isWatching(Player player) {
        return getState(player) != null;
    }

    /**
     * Get the cutscene status of a player.
     * @param player
     * @return status
     */
    public static CutsceneStatus getState(Player player) {
        return getDataMap().get(player);
    }

    /**
     * Remove the quest information for a given player.
     * @param player
     */
    public static void removeState(Player player) {
        getDataMap().remove(player);
    }

    /**
     * Get the map of all cutscenes.
     * @return cutscenes
     */
    public static JsonMap<Cutscene> getCutscenes() {
        Cutscenes instance = MechanicManager.getInstance(Cutscenes.class);
        return instance != null ? instance.getMap() : new JsonMap<>();
    }
}
