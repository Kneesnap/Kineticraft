package net.kineticraft.lostcity.guis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.guis.guis.donor.*;
import net.kineticraft.lostcity.guis.guis.staff.*;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.entity.Player;

/**
 * A list of all constructable display.
 *
 * Created by Kneesnap on 6/8/2017.
 */
@AllArgsConstructor @Getter
public enum GUIType {

    // Donor
    DONOR(GUIDonor.class),
    PARTICLES(GUIParticles.class),

    // Staff
    ITEM_VENDOR(GUIItemVendor.class),
    ITEM_EDITOR(GUIItemEditor.class),
    VOTE_EDITOR(GUIEditVoteRewards.class);

    private final Class<? extends GUI> guiClass;

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
