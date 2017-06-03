package net.kineticraft.lostcity.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.item.*;
import net.kineticraft.lostcity.item.guis.*;

/**
 * ItemRegistry for automatic constructing of items.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@AllArgsConstructor @Getter
public enum ItemType {

    DISPLAY(DisplayItem.class);

    private final Class<? extends ItemWrapper> itemClass;
}
