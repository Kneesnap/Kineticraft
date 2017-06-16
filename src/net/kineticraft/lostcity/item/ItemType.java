package net.kineticraft.lostcity.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.item.display.*;
import net.kineticraft.lostcity.item.items.ItemArmorStand;
import net.kineticraft.lostcity.item.items.books.ItemBook;
import net.kineticraft.lostcity.item.items.books.ItemTPABook;

/**
 * ItemRegistry for automatic constructing of items.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@AllArgsConstructor @Getter
public enum ItemType {

    TPA_BOOK(ItemTPABook.class),
    CUSTOM_BOOK(ItemBook.class),
    ARMOR_STAND(ItemArmorStand.class),
    DISPLAY(DisplayItem.class);

    private final Class<? extends ItemWrapper> itemClass;

    /**
     * Can this item be constructed without any extra arguments?
     * @return simple
     */
    public boolean isSimple() {
        return makeSimple() != null;
    }

    /**
     * Attempts to create this item, without any extra parameters, if it can't be done, it returns null.
     * @return itemWrapper
     */
    public ItemWrapper makeSimple() {
        try {
            return getItemClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null; //TODO: Only return null if the constructor isn't found, if the error is something else, panic.
        }
    }
}
