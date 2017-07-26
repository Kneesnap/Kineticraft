package net.kineticraft.lostcity.data.reflect.behavior.generic;

import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.behavior.MethodStore;
import net.kineticraft.lostcity.item.display.GUIItem;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Save and load UUIDS.
 * Created by Kneesnap on 7/25/2017.
 */
public class UUIDStore extends MethodStore<UUID> {

    public UUIDStore() {
        super(UUID.class, "UUID");
    }

    @Override
    public void editItem(GUIItem item, Field f, Jsonable data) {

    }
}
