package net.kineticraft.lostcity.guis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.guis.guis.staff.GUIEditVoteRewards;
import net.kineticraft.lostcity.guis.guis.staff.GUIItemEditor;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.entity.Player;

/**
 * A list of all constructable guis.
 *
 * Created by Kneesnap on 6/8/2017.
 */
@AllArgsConstructor @Getter
public enum GUIType {

    // Staff
    ITEM_EDITOR(GUIItemEditor.class, "Item Editor"),
    VOTE_EDITOR(GUIEditVoteRewards.class, "Vote Rewards");


    private final Class<? extends GUI> guiClass;
    private final String title;

    GUIType(Class<? extends GUI> cls) {
        this(cls, "");
    }

    /**
     * Construct this as a GUI for the given player.
     * @param player
     * @return gui
     */
    public GUI construct(Player player) {
        assert KCPlayer.getWrapper(player) != null;
        return ReflectionUtil.construct(getGuiClass(), new Class[] {Player.class}, player);
    }
}
