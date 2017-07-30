package net.kineticraft.lostcity.cutscenes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.annotations.AllowNull;
import net.kineticraft.lostcity.cutscenes.gui.CutsceneActionEditor;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import org.bukkit.ChatColor;

/**
 * Represents an action taken during a cutscene stage.
 * Created by Kneesnap on 7/22/2017.
 */
@AllArgsConstructor @Getter
public abstract class CutsceneAction implements Jsonable {

    /**
     * Get annotated action data of this action.
     * @return data
     */
    public ActionData getData() {
        return getClass().getAnnotation(ActionData.class);
    }

    /**
     * Execute this action.
     * @param event
     */
    public abstract void execute(CutsceneEvent event);

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
        return (isValid() ? ChatColor.YELLOW : ChatColor.RED) + getClass().getSimpleName();
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
}
