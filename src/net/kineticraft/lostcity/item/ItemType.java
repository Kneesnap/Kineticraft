package net.kineticraft.lostcity.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.item.display.*;
import net.kineticraft.lostcity.item.items.ItemArmorStand;
import net.kineticraft.lostcity.item.items.books.*;

import java.lang.reflect.InvocationTargetException;

/**
 * ItemRegistry for automatic constructing of items.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@AllArgsConstructor @Getter
public enum ItemType {

    PLAYER_NOTEBOOK(ItemBookNotes.class),
    PATCHNOTES_BOOK(ItemPatchBook.class),
    FILE_BOOK(ItemBookFile.class),
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
        } catch (InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            Core.warn("Error creating simple " + getItemClass().getSimpleName() + ".");
        } catch (IllegalAccessException | NoSuchMethodException e) {

        }

        return null;
    }
}
