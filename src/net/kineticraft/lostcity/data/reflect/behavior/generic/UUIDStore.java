package net.kineticraft.lostcity.data.reflect.behavior.generic;

import net.kineticraft.lostcity.data.reflect.behavior.MethodStore;
import net.kineticraft.lostcity.item.display.GUIItem;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Save and load UUIDS.
 * Created by Kneesnap on 7/25/2017.
 */
public class UUIDStore extends MethodStore<UUID> {

    public UUIDStore() {
        super(UUID.class, "UUID");
    }

    @Override
    public void editItem(GUIItem item, Object value, Consumer<Object> setter) {

    }
}
