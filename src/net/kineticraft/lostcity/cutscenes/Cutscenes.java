package net.kineticraft.lostcity.cutscenes;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.cutscenes.actions.*;
import net.kineticraft.lostcity.cutscenes.actions.entity.*;
import net.kineticraft.lostcity.cutscenes.commands.CommandCutscene;
import net.kineticraft.lostcity.cutscenes.commands.CommandCutsceneEditor;
import net.kineticraft.lostcity.data.maps.JsonMap;
import net.kineticraft.lostcity.events.CommandRegisterEvent;
import net.kineticraft.lostcity.mechanics.system.MechanicManager;
import net.kineticraft.lostcity.mechanics.system.ModularMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.*;
import java.util.stream.Stream;

/**
 * Manages Cutscenes.
 * TODO: Show players in cutscene at startLocation.
 * Created by Kneesnap on 6/1/2017.
 */
public class Cutscenes extends ModularMechanic<Cutscene> {

    @Getter private static Map<Player, CutsceneStatus> dataMap = new HashMap<>();
    @Getter private static List<Class<? extends CutsceneAction>> actions = new ArrayList<>();

    public Cutscenes() {
        super("cinematics", Cutscene.class);
    }

    @Override
    public void load() {
        super.load();
        getCutscenes().forEach((k, v) -> v.setName(k));
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

    @EventHandler
    public void onCommandRegister(CommandRegisterEvent evt) {
        evt.register(new CommandCutscene(), new CommandCutsceneEditor());
    }

    @EventHandler(ignoreCancelled = true) // Prevent dismounting from the camera mount.
    public void onSneak(PlayerToggleSneakEvent evt) {
        evt.setCancelled(isWatching(evt.getPlayer()) && evt.isSneaking());
    }

    @EventHandler(ignoreCancelled = true) // Force entities to target their pathfinder goal.
    public void onEntityTarget(EntityTargetEvent evt) {
        evt.setCancelled(isProp(evt.getEntity()));
    }

    /**
     * Is this entity a cutscene prop?
     * @param entity
     * @return isProp
     */
    public static boolean isProp(Entity entity) {
        return getDataMap().values().stream().anyMatch(cs -> cs.getEntityMap().values().contains(entity));
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

    /**
     * Play a cutscene for all the given players.
     * @param players
     * @param cutscene
     */
    public static void playCutscene(List<Player> players, String cutscene) {
        Cutscene c = getCutscenes().get(cutscene);
        if (c != null) {
            c.play(players);
        } else {
            Core.warn("Cutscene '" + cutscene + "' not found.");
        }
    }

    // Register all CutsceneActions.
    static {
        Stream.of(ActionCreateEntity.class, ActionEntityAnimation.class, ActionEntityCameraTrack.class, ActionEntityGear.class,
                ActionEntityNodHead.class, ActionEntityPathfind.class, ActionEntityShakeHead.class, ActionEntityTalk.class,
                ActionEntityVelocity.class, ActionRemoveEntity.class, ActionTeleportEntity.class, ActionUpdateEntity.class,
                ActionBlockUpdate.class, ActionCameraFilter.class, ActionPlaySound.class, ActionSendMessage.class,
                ActionSpawnParticles.class, ActionPlayNBS.class, ActionArmorStandPose.class, ActionPotionEffect.class,
                ActionLoadEntity.class, ActionCompleteDungeon.class, ActionSetEndLocation.class).forEach(getActions()::add);
    }
}
