package net.kineticraft.lostcity.cutscenes;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.annotations.AllowNull;
import net.kineticraft.lostcity.cutscenes.gui.CutsceneActionEditor;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents an action taken during a cutscene stage.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter @Setter
public abstract class CutsceneAction implements Jsonable {

    private transient CutsceneEvent event;

    /**
     * Get annotated action data of this action.
     * @return data
     */
    public ActionData getData() {
        return getClass().getAnnotation(ActionData.class);
    }

    /**
     * Execute this action.
     */
    public abstract void execute();

    /**
     * Returns if this action is valid. An action is valid if all the required fields are not null.
     * @return isValid.
     */
    public boolean isValid() {
        return JsonSerializer.getFields(this).stream().filter(f -> !f.isAnnotationPresent(AllowNull.class)).allMatch(f -> {
                    try {
                        return f.get(this) != null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                });
    }

    /**
     * Get the display name of this action.
     * @return name
     */
    public String getName() {
        return getColor() + getClass().getSimpleName();
    }

    /**
     * Return the color of this action, based on if it's valid or not.
     * @return color
     */
    public ChatColor getColor() {
        return isValid() ? ChatColor.YELLOW : ChatColor.RED;
    }

    /**
     * Add any special buttons to the editor gui.
     * @param gui
     */
    @SuppressWarnings("unused")
    public void setupGUI(CutsceneActionEditor gui) {

    }

    @Override
    public String toString() {
        return toJsonString();
    }

    /**
     * Create a duplicate Location with the correct world.
     * @return fixed
     */
    protected Location fixLocation(Location old) {
        return fixLocation(old, getWorld());
    }

    /**
     * Create a duplicate location with a set world.
     * @param old
     * @param world
     * @return fixed
     */
    protected Location fixLocation(Location old, World world) {
        Location fixed = old.clone();
        fixed.setWorld(world);
        return fixed;
    }

    /**
     * Get the world this cutscene is taking place in.
     * @return world
     */
    protected World getWorld() {
        return getEvent().getStatus().getCamera().getWorld();
    }

    /**
     * Get the players watching this cutscene.
     * @return players
     */
    protected List<Player> getPlayers() {
        return getEvent().getStatus().getPlayers();
    }

    /**
     * Get the camera entity.
     * @return camera
     */
    protected LivingEntity getCamera() {
        return getEvent().getStatus().getCamera();
    }

    /**
     * Format a string sent to a player.
     * @param message
     * @param p
     * @return formatted.
     */
    protected String formatString(String message, Player p) {
        return ChatColor.translateAlternateColorCodes('&',
                String.format(message, ChatColor.YELLOW + p.getName() + ChatColor.GREEN));
    }
}
