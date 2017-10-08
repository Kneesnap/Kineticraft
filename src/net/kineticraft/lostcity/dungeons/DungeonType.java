package net.kineticraft.lostcity.dungeons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.dungeons.dungeons.barleyshope.BarleysHope;
import net.kineticraft.lostcity.dungeons.dungeons.stickysituation.StickySituation;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.function.Function;

/**
 * A registry of our dungeons.
 * Created by Kneesnap on 7/11/2017.
 */
@AllArgsConstructor @Getter
public enum DungeonType {

    BARLEYS_HOPE(BarleysHope::new, "Barley's Hope", "Save Mr. Barley!", "Mr. Barley has been saved"),
    STICKY_SITUATION(StickySituation::new, "Sticky Situation", "Find Hedgemorth!", "Hedgemorth has been slain");

    private final Function<List<Player>, Dungeon> construct;
    private final String name;
    private final String entryMessage;
    private final String finishMessage;
    private final boolean released;

    DungeonType(Function<List<Player>, Dungeon> construct, String name, String entry, String finish) {
        this(construct, name, entry, finish, true);
    }

    /**
     * Get the color of this dungeon's name.
     * @return color
     */
    public ChatColor getColor() {
        return getRank().getColor();
    }

    /**
     * Get the display name of this dungeon.
     * @return displayName
     */
    public String getDisplayName() {
        return getRank().getColor().toString() + ChatColor.UNDERLINE + Utils.capitalize(name());
    }

    /**
     * Get the rank that unlocks this dungeon.
     * @return rank
     */
    public EnumRank getRank() {
        return EnumRank.values()[ordinal() + 1];
    }

    /**
     * Get the zipped up world for this dungeon.
     * @return world
     */
    public File getWorld() {
        return Core.getFile("dungeons/" + name().toLowerCase() + ".zip");
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
