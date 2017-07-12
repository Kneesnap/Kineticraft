package net.kineticraft.lostcity.dungeons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.entity.Player;

/**
 * A registry of our dungeons.
 *
 * Created by Kneesnap on 7/11/2017.
 */
@AllArgsConstructor @Getter
public enum DungeonType {

    BARLEYS_HOPE(Dungeon.class);

    private final Class<? extends Dungeon> dungeonClass;

    public String getDisplayName() {
        return getRank().getColor() + Utils.capitalize(name());
    }

    /**
     * Get the rank that unlocks this dungeon.
     * @return rank
     */
    public EnumRank getRank() {
        return EnumRank.values()[ordinal() + 1];
    }

    /**
     * Has the given player unlocked this dungeon yet?
     * @param player
     * @return unlocked
     */
    public boolean hasUnlocked(Player player) {
        return KCPlayer.getWrapper(player).isRank(getRank());
    }
}
