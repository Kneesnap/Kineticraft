package net.kineticraft.lostcity.utils;

import lombok.Getter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * NBTWrapper - Allows version independent NBT editting.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@Getter
public class NBTWrapper implements Jsonable {

    private Material type = Material.DIRT;
    private int amt = 1;
    private short meta;
    private String tag = "{}";

    public NBTWrapper() {

    }

    public NBTWrapper(ItemStack item) {
        this.type = item.getType();
        this.amt = item.getAmount();
        this.meta = item.getDurability();
        this.tag = loadTag(item);
    }

    /**
     * Returns the entire NMS item nbt data.
     * This includes data like type and count.
     * getTag() will be what is mostly used.
     *
     * @return fullTag
     */
    public Object getFullTag() {
        try {
            return ReflectionUtil.exec(getNMS(getItem()), "save", newTagCompound());
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to generate full NBT Tag.");
        }
        return null;
    }

    /**
     * Get the NBT tag of this item.
     *
     * @param item - The item to load the tag of.
     * @return tag
     */
    private static String loadTag(ItemStack item) {
        // Load the tag from NMS.
        Object tag = ReflectionUtil.exec(getNMS(item), "getTag");
        return tag != null ? tag.toString() : "{}";
    }

    public ItemStack getItem() {
        ItemStack bukkit = new ItemStack(getType(), getAmt(), getMeta());
        Object nms = getNMS(bukkit);
        ReflectionUtil.exec(nms, "setTag", parseNBT(getTag()));
        return (ItemStack) ReflectionUtil.exec(ReflectionUtil.getCraftBukkit("inventory.CraftItemStack"), "asBukkitCopy", nms);
    }

    /**
     * Get the NMS item object for this item.
     * @param item
     * @return nmsCopy
     */
    private static Object getNMS(ItemStack item) {
            return ReflectionUtil.exec(ReflectionUtil.getCraftBukkit("inventory.CraftItemStack"),
                    "asNMSCopy", new Class[] {ItemStack.class}, item);
    }

    public static Object newTagCompound() {
        return ReflectionUtil.construct(ReflectionUtil.getNMS("NBTTagCompound"));
    }

    /**
     * Converts a mojangson string to an NBT tag.
     * @param nbt
     * @return tag
     */
    private static Object parseNBT(String nbt) {
        return ReflectionUtil.exec(ReflectionUtil.getNMS("MojangsonParser"), "parse", nbt);
    }

    /**
     * Converts an ItemStack to Json.
     * @param item
     * @return json
     */
    public static JsonData toJson(ItemStack item) {
        return item != null && item.getType() != Material.AIR ? new JsonData(new NBTWrapper(item)) : null;
    }
}