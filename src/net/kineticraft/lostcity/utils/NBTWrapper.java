package net.kineticraft.lostcity.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

/**
 * NBTWrapper - Allows version independent NBT editting.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@Getter
public class NBTWrapper implements Jsonable {

    private ItemStack item;
    private Object tag;

    public NBTWrapper(String itemJson) {
        this(new JsonData(new JsonParser().parse(itemJson).getAsJsonObject()));
    }

    public NBTWrapper(JsonData data) {
        load(data);
    }

    public NBTWrapper(ItemStack item) {
        this.item = item;
        loadTag();
    }

    private NBTWrapper(ItemStack item, Object tag) throws Exception {
        this.item = item;
        this.tag = tag;
        applyTag(); // Since the item's NBT tag won't match our NBT tag, we should set it.
    }

    /**
     * Returns the entire NMS item nbt data.
     * This includes data like type and count.
     * getTag() will be what is mostly used.
     */
    public Object getFullTag() {
        try {
            return ReflectionUtil.exec(getNMS(), "save", newTagCompound());
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to generate full NBT Tag.");
        }
        return null;
    }

    /**
     * Loads full ItemStack json data.
     */
    @Override
    public void load(JsonData data) {
        try {
            this.item = new ItemStack(data.getEnum("id", Material.class), data.getInt("amt"), data.getShort("meta"));
            parseNBT(data.has("tag") ? data.getString("tag") : "{}");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to load NBTItem.");
            this.item = new ItemStack(Material.DIRT);
        }
    }

    /**
     * Saves this entire ItemStack object to Json.
     */
    @Override
    public JsonData save() {
        JsonData data = new JsonData();
        data.setEnum("id", getItem().getType());
        data.setNum("amt", getItem().getAmount());
        data.setNum("meta", getItem().getDurability());
        data.setString("tag", getTag().toString());
        return data;
    }

    public int getInt(String key) {
        return get("Int", key, 0);
    }

    public void setInt(String key, int val) {
        exec("setInt", key, val);
    }

    public String getString(String key) {
        return get("String", key, "");
    }

    public void setString(String key, String value) {
        exec("setString", key, value);
    }

    public Object remove(String key) {
        return exec("remove", key);
    }

    public boolean has(String key) {
        return (boolean) exec("hasKey", key);
    }

    @SuppressWarnings("unchecked")
    private <T> T get(String type, String key, T defaultValue) {
        return has(key) ? (T) exec("get" + type, key) : defaultValue;
    }

    private void loadTag() {
        // Load the tag from NMS.
        try {
            Object nmsStack = getNMS();
            this.tag = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
            if (this.tag == null)
                this.tag = newTagCompound();
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to load NBT tag from item.");
        }
    }

    private Object getNMS() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return ReflectionUtil.getCraftBukkit("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, getItem());
    }

    /**
     * Apply the saved NBT tag to the item.
     */
    private void applyTag() throws Exception {
        Object nms = getNMS();
        ReflectionUtil.exec(nms, "setTag", getTag());

        // Reload this item.
        this.item = (ItemStack) ReflectionUtil.getCraftBukkit("inventory.CraftItemStack")
                .getMethod("asBukkitCopy", nms.getClass()).invoke(null, nms);
    }

    private Object exec(String method, Object... args) {
        try {
            Object result = ReflectionUtil.exec(getTag(), method, args);

            // Updates the item's tag.
            applyTag();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to execute NBT method " + method + "!");
        }
        return null;
    }

    public static Object newTagCompound() {
        return ReflectionUtil.construct(ReflectionUtil.getNMS("NBTTagCompound"));
    }

    /**
     * Converts a mojangson string to an NBT tag.
     */
    private static Object parseNBT(String nbt) throws Exception {
        return ReflectionUtil.getNMS("MojangsonParser").getMethod("parse", String.class).invoke(null, nbt);
    }

    /**
     * Converts an ItemStack to Json.
     */
    public static JsonData toJson(ItemStack item) {
        return new NBTWrapper(item).save();
    }
}