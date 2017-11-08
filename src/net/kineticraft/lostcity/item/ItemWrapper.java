package net.kineticraft.lostcity.item;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kineticraft.lostcity.mechanics.enchants.CustomEnchant;
import net.kineticraft.lostcity.utils.Utils;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemWrapper - Advanced Item Wrapper.
 *
 * If this implements Listener, it will automatically be registered, however keep in mind it is only registered once,
 * and does not fire in an item's specific instance. (IE: Write your listeners the same way you would if they were in another file.)
 * All bukkit listeners MUST have a no-args constructor.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@Getter
public abstract class ItemWrapper {

    private org.bukkit.inventory.ItemStack item;
    private ItemMeta meta;
    private NBTTagCompound tag;
    private ItemType type;
    private boolean needsReset; // Prevents lore from generating twice if the object is not changed.

    public ItemWrapper(org.bukkit.inventory.ItemStack item) {
        this.item = item;
        this.meta = item != null ? item.getItemMeta() : null;
        this.type = getEnum("type", ItemType.class);
    }

    public ItemWrapper(ItemType type) {
        this.type = type;
    }

    /**
     * Generate this item
     * @return item
     */
    public org.bukkit.inventory.ItemStack generateItem() {
        this.item = getRawStack();
        updateItem();
        setEnum("type", getType());
        updateTag();
        this.needsReset = true;
        return getItem();
    }

    /**
     * Get this item's ItemMeta.
     * @return meta
     */
    public ItemMeta getMeta() {
        if (this.meta == null)
            this.meta = getItem() != null ? getItem().getItemMeta() : getRawStack().getItemMeta();
        return this.meta;
    }

    /**
     * Get a NMS copy of this item.
     * @return nms
     */
    public ItemStack getNMSCopy() {
        return CraftItemStack.asNMSCopy(getItem());
    }

    /**
     * Return this item's NBT tag.
     * @return tag
     */
    public NBTTagCompound getTag() {
        if (this.tag == null) {
            ItemStack nms = getNMSCopy();
            this.tag = nms != null && nms.hasTag() ? nms.getTag() : new NBTTagCompound();
        }
        return this.tag;
    }

    /**
     * Save this item + nbt tag into an NBT compound.
     * @return fullTag
     */
    public NBTTagCompound getFullTag() {
        return getNMSCopy().save(new NBTTagCompound());
    }

    /**
     * Does the NBT tag contain this key?
     * @param key
     * @return contains
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
     * Set an NBT Tag Compound.
     * @param key
     * @param newTag
     * @param format
     */
    @SneakyThrows
    public void setTag(String key, String newTag, Object... format) {
        getTag().set(key, MojangsonParser.parse(String.format(newTag, format)));
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
     * Set this item's ItemMeta.
     * @param meta
     */
    protected void setMeta(ItemMeta meta) {
        this.meta = meta;
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
        List<String> loreList = getLore();

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
     * Add a lore action. Does not get added if an action with the same click type exists.
     * @param click - The mouse click type that fires this action.
     * @param action - The action to display.
     * @return this
     */
    public ItemWrapper addLoreAction(String click, String action) {
        String control = ChatColor.WHITE + click + "-Click: " + ChatColor.GRAY;
        for (String s : getLore())
            if (s.startsWith(ChatColor.GRAY + control))
                return this;
        return addLore(control + action);
    }

    /**
     * Get the lore of this item.
     * @return lore
     */
    private List<String> getLore() {
        return getMeta().hasLore() ? getMeta().getLore() : new ArrayList<>();
    }

    /**
     * Apply our editted NBT to this item.
     * Invalidates the old ItemStack.
     */
    protected void updateTag() {
        // Update Item Meta. If for some reason our method does not work, we can try CraftMetaItem#applyItemMeta
        org.bukkit.inventory.ItemStack withMeta = getItem().clone();
        withMeta.setItemMeta(getMeta());
        ItemStack nms = CraftItemStack.asNMSCopy(withMeta);

        if (nms != null && nms.hasTag()) {
            // There's some data we should merge with our NBT tags.
            NBTTagCompound merge = nms.getTag();
            for (String key : merge.c())
                getTag().set(key, merge.get(key));
            getItem().setItemMeta(getMeta());
        }

        // Update NBT Tags
        ItemStack newItem = CraftItemStack.asNMSCopy(getItem());
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
    public static boolean isType(org.bukkit.inventory.ItemStack item, ItemType type) {
        ItemStack nms = CraftItemStack.asNMSCopy(item);
        return nms.hasTag() && nms.getTag().hasKey("type") && type.name().equals(nms.getTag().getString("type"));
    }

    /**
     * Get the ItemType for a bukkit ItemStack.
     * @param item
     * @return type
     */
    public static ItemType getType(org.bukkit.inventory.ItemStack item) {
        ItemStack nms = CraftItemStack.asNMSCopy(item);
        return nms != null && nms.hasTag() && nms.getTag().hasKey("type")
                ? ItemType.valueOf(nms.getTag().getString("type")) : null;
    }

    /**
     * Return whether or not the supplied item has a type.
     * @param item
     * @return isCustom
     */
    public static boolean isCustom(org.bukkit.inventory.ItemStack item) {
        return getType(item) != null;
    }

    /**
     * Return the raw ItemStack ready for editting.
     * @return
     */
    public abstract org.bukkit.inventory.ItemStack getRawStack();

    /**
     * Apply any changes to this item.
     */
    public abstract void updateItem();
}
