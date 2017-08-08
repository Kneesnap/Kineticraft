package net.kineticraft.lostcity.dungeons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.dungeons.dungeons.barleyshope.Frilda;

import java.util.function.Function;

/**
 * A list of all dungeon bosses.
 * Created by Kneesnap on 8/2/2017.
 */
@AllArgsConstructor @Getter
public enum BossType {
    FRILDA(Frilda::new);

    private Function<Dungeon, DungeonBoss> construct;

    /**
     * Spawn this boss.
     * @param d
     * @return boss
     */
    public DungeonBoss spawnBoss(Dungeon d) {
        return construct.apply(d);
    }
}
