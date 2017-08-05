package net.kineticraft.lostcity.cutscenes;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.cutscenes.actions.entity.ActionCreateEntity;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a single cinematic cutscene.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter
public class Cutscene implements Jsonable {

    @Setter private transient String name;
    private JsonList<CutsceneStage> stages = new JsonList<>();

    /**
     * Play this cutscene for a given player.
     * @param player
     */
    public void play(Player player) {
        play(Arrays.asList(player));
    }

    /**
     * Play this cutscene for multiple players.
     * @param players
     */
    public void play(List<Player> players) {
        players.forEach(Utils::stopNBS); // Kill any nbs sounds playing.
        CutsceneStatus status = new CutsceneStatus(this, players);
        status.nextStage(); // Start the cutscene.
    }

    /**
     * Get the entity type from an entity name in this cutscene.
     * @param type
     * @return type
     */
    public EntityType getType(String type) {
        if (type.equalsIgnoreCase("Camera"))
            return EntityType.ARMOR_STAND;

        List<ActionCreateEntity> creations = getActions(ActionCreateEntity.class);
        ActionCreateEntity ce = creations.stream().filter(ac -> ac.getEntityName().equalsIgnoreCase(type)).findFirst().orElse(null);
        return ce != null ? ce.getEntityType() : null;
    }

    /**
     * Get all cutscene actions by the given class.
     * @param clazz
     * @param <T>
     * @return actions
     */
    @SuppressWarnings("unchecked")
    public <T extends CutsceneAction> List<T> getActions(Class<T> clazz) {
        List<T> actions = new ArrayList<>();
        for (CutsceneStage stage : getStages())
            for (CutsceneAction a : stage.getActions())
                if (clazz.isAssignableFrom(a.getClass()))
                    actions.add((T) a);
        return actions;
    }

    /**
     * Add a stage composed of the given actions.
     * @param actions
     * @return stage
     */
    protected CutsceneStage addStage(CutsceneAction... actions) {
        CutsceneStage stage = new CutsceneStage();
        Stream.of(actions).forEach(stage::addAction);
        getStages().add(stage);
        return stage;
    }

    /**
     * Add a stage composed of the given actions.
     * @param ticks
     * @param actions
     * @return stage
     */
    protected CutsceneStage addStage(int ticks, CutsceneAction... actions) {
        CutsceneStage stage = addStage(actions);
        stage.setTicks(ticks);
        return stage;
    }
}
