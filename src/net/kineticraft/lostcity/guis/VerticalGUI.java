package net.kineticraft.lostcity.guis;

import org.bukkit.entity.Player;

/**
 * A GUI that flows top to bottom instead of left to right.
 * NOT DONE YET.
 *
 *
 * Created by Kneesnap on 6/16/2017.
 */
public abstract class VerticalGUI extends GUI {
    public VerticalGUI(Player player, String title, int rows) {
        super(player, title, rows);
    }

    @Override
    public int getSlotIndex() {
        return (super.getSlotIndex() % getRows()) * ROW_SIZE + (super.getSlotIndex() / getRows());
    }
}
