package net.kineticraft.lostcity.data.wrappers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.utils.NBTWrapper;
import org.bukkit.inventory.ItemStack;

/**
 * A wrapper around ItemStack so we can put this in classes like JsonList
 *
 * Created by Kneesnap on 6/10/2017.
 */
@AllArgsConstructor @Getter @Setter
public class JsonItem implements Jsonable {

    private ItemStack item;

    public JsonItem(JsonData data) {
        load(data);
    }

    @Override
    public void load(JsonData data) {
        setItem(new NBTWrapper(data).getItem());
    }

    @Override
    public JsonData save() {
        return NBTWrapper.toJson(getItem());
    }
}
