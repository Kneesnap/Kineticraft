package net.kineticraft.lostcity.item;

import lombok.Getter;
import net.kineticraft.lostcity.mechanics.enchants.CustomEnchant;
import net.kineticraft.lostcity.utils.Utils;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemWrapper - Advanced Item Wrapper.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@Getter
public abstract class ItemWrapper {

    private ItemStack item;
    private ItemMeta meta;
    private NBTTagCompound tag;
    private ItemType type;
    private boolean needsReset; // Prevents lore from generating twice if the object is not changed.

    public ItemWrapper(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.type = getEnum("type", ItemType.class);
    }

    public ItemWrapper(ItemType type) {
        this.type = type;
    }

    /**
     * Generate this item
     * @return
     */
    public ItemStack generateItem() {
        this.item = getRawStack();
        updateItem();
        setEnum("type", getType());
        updateTag();
        this.needsReset = true;
        return getItem();
    }

    public net.minecraft.server.v1_11_R1.ItemStack getNMSCopy() {
        return CraftItemStack.asNMSCopy(getItem());
    }

    /**
     * Return this item's NBT tag.
     * @return
     */
    public NBTTagCompound getTag() {
        if (this.tag == null) {
            net.minecraft.server.v1_11_R1.ItemStack nms = getNMSCopy();
            this.tag = nms != null && nms.hasTag() ? nms.getTag() : new NBTTagCompound();
        }
        return this.tag;
    }

    /**
     * Does the NBT tag contain this key?
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return getTag().hasKey(key);
    }

    /**
     * Remove an NBT tag
     * @param key
     */
    public void remove(String key) {
        getTag().remove(key);
    }

    /**
     * Retrieve a boolean from NBT data.
     * @param key
     * @return
     */
    public boolean getTagBoolean(String key) {
        return getTag().getBoolean(key);
    }

    /**
     * Set a boolean in NBT data.
     * @param key
     * @param state
     */
    public void setTagBoolean(String key, boolean state) {
        remove(key);
        if (state)
            getTag().setBoolean(key, state);
    }

    /**
     * Load a string from NBT. Defaults to "" if not found.
     * @param key
     * @return
     */
    public String getTagString(String key) {
        return getTag().getString(key);
    }

    /**
     * Set a string in NBT.
     * @param key
     * @param value
     */
    public void setTagString(String key, String value) {
        remove(key);
        if (value != null && value.length() > 0)
            getTag().setString(key, value);
    }

    /**
     * Set an enum value in NBT
     * @param key
     * @param value
     */
    public void setEnum(String key, Enum<?> value) {
        remove(key);
        if (value != null)
            setTagString(key, value.name());
    }

    /**
     * Load an enum value from saved NBT.
     * @param key
     * @param enumClass
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass) {
        return Utils.getEnum(getTagString(key), enumClass);
    }

    /**
     * Load an enum value from saved NBT.
     * @param key
     * @param defaultValue
     */
    public <T extends Enum<T>> T getEnum(String key, T defaultValue) {
        return Utils.getEnum(getTagString(key), defaultValue);
    }

    /**
     * Save an integer into NBT.
     * @param key
     * @return
     */
    public int getTagInt(String key) {
        return getTag().getInt(key);
    }

    /**
     * Load an integer value from saved NBT.
     * @param key
     * @param val
     */
    public void setTagInt(String key, int val) {
        remove(key);
        if (val != 0)
            getTag().setInt(key, val);
    }

    /**
     * Give or remove the 'enchant glow' to this item.
     * @param glowing
     * @return this
     */
    public ItemWrapper setGlowing(boolean glowing) {
        getItem().removeEnchantment(CustomEnchant.GLOWING.getEnchant());
        if (glowing)
            getItem().addUnsafeEnchantment(CustomEnchant.GLOWING.getEnchant(), 1);
        return this;
    }

    /**
     * Clear all lore from the item.
     * @return this
     */
    public ItemWrapper clearLore() {
        getMeta().setLore(new ArrayList<>());
        return this;
    }

    /**
     * Add lore to this item
     * @param lore
     * @return this
     */
    public ItemWrapper addLore(List<String> lore) {
        return addLore(lore.toArray(new String[lore.size()]));
    }

    /**
     * Add lore to this item.
     * @param lore
     * @return this
     */
    public ItemWrapper addLore(String... lore) {
        List<String> loreList = getMeta().hasLore() ? getMeta().getLore() : new ArrayList<>();

        if (needsReset) {
            loreList.clear();
            needsReset = false;
        }

        for (String loreLine : lore)
            loreList.add(ChatColor.GRAY + loreLine);
        getMeta().setLore(loreList);

        return this;
    }

    /**
     * Apply our editted NBT to this item.
     * Invalidates the old ItemStack.
     */
    protected void updateTag() {
        // Update Item Meta.
        ItemStack withMeta = getItem().clone();
        withMeta.setItemMeta(getMeta());
        net.minecraft.server.v1_11_R1.ItemStack nms = CraftItemStack.asNMSCopy(withMeta);

        if (nms != null && nms.hasTag()) {
            // There's some data we should merge with our NBT tags.
            NBTTagCompound merge = nms.getTag();
            for (String key : merge.c())
                getTag().set(key, merge.get(key));
            getItem().setItemMeta(getMeta());
        }

        // Update NBT Tags
        net.minecraft.server.v1_11_R1.ItemStack newItem = CraftItemStack.asNMSCopy(getItem());
        if (newItem != null)
            newItem.setTag(getTag());
        this.item = CraftItemStack.asBukkitCopy(newItem);
    }

    /**
     * Is this item the specified type?
     * @param item
     * @param type
     * @return
     */
    public static boolean isType(ItemStack item, ItemType type) {
        net.minecraft.server.v1_11_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
        return nms.hasTag() && nms.getTag().hasKey("type") && type.name().equals(nms.getTag().getString("type"));
    }

    public static ItemType getType(ItemStack item) {
        net.minecraft.server.v1_11_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
        return nms != null && nms.hasTag() && nms.getTag().hasKey("type")
                ? ItemType.valueOf(nms.getTag().getString("type")) : null;
    }

    /**
     * Return the raw ItemStack ready for editting.
     * @return
     */
    public abstract ItemStack getRawStack();

    /**
     * Apply any changes to this item.
     */
    public abstract void updateItem();
}
