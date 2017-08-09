package net.kineticraft.lostcity.guis.staff;

import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.item.display.DisplayItem;
import net.kineticraft.lostcity.mechanics.Callbacks;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Allows staff to edit items.
 *
 * Created by Kneesnap on 6/9/2017.
 */
public class GUIItemEditor extends GUI {

    private ItemStack original;
    private DisplayItem edit;
    private Consumer<ItemWrapper> onFinish;

    public GUIItemEditor(Player player) {
        this(player, null);
    }

    public GUIItemEditor(Player player, Consumer<ItemWrapper> onFinish) {
        this(player, player.getEquipment().getItemInMainHand(), onFinish);
    }

    public GUIItemEditor(Player player, ItemStack item,  Consumer<ItemWrapper> onFinish) {
        super(player, "Item Editor");
        this.original = item != null && item.getType() != Material.AIR ? item : new ItemStack(Material.DIRT);
        this.edit = new DisplayItem(original);
        this.onFinish = onFinish;
    }

    @Override
    public void addItems() {
        ItemStack display = edit.generateItem();

        addItem(Material.BUCKET, ChatColor.AQUA + "Load Item", "Drop an item onto here to edit it.").anyClick(e -> {
            if (e.getEvent().getCursor() == null || e.getEvent().getCursor().getType() == Material.AIR)
                return;

            this.edit = new DisplayItem(e.getEvent().getCursor());
            reconstruct();
        });

        center(1);
        addItem(display).anyClick(e -> Utils.giveItem(getPlayer(), edit.generateItem()));

        toRight(1);
        addItem(Material.WOOL, ChatColor.GREEN + "Done", "Click here when you're done making changes.")
                .anyClick(e -> close()).setColor(DyeColor.LIME);

        nextRow();
        nextRow();

        center(5);

        // Edit Item Material
        addItem(display.getType(), ChatColor.AQUA + "Item Type", "Click here to change the item type")
                .anyClick(e -> new GUIMaterialPicker(getPlayer(), material -> {
                    edit.getItem().setType(material);
                    reconstruct();
                }));

        // Edit item amount
        addItem(Material.GOLD_INGOT, ChatColor.AQUA + "Item Amount", "Click here to change the item amount")
                .anyClick(e -> {
                    getPlayer().sendMessage(ChatColor.GREEN + "Please enter the new item amount.");
                    Callbacks.listenForNumber(getPlayer(), 1, 64, n -> {
                        edit.setAmount(n);
                        update("Stack size updated");
                    });
                }).setAmount(display.getAmount());

        nextSlot();

        // Edit Item Meta
        addItem(Material.INK_SACK, ChatColor.YELLOW + "Item Meta", "Click here to change the item meta value.")
                .anyClick(e -> {
                    getPlayer().sendMessage(ChatColor.YELLOW + "Please enter the new meta value.");
                    Callbacks.listenForNumber(getPlayer(), Short.MIN_VALUE, Short.MAX_VALUE, n -> {
                        edit.setMeta((short) ((int) n)); // We must double cast because Integer wrapper cannot be directly converted to short.
                        update("Meta updated");
                    });
                }).setColor(DyeColor.ORANGE);

        // Edit item name
        addItem(Material.NAME_TAG, ChatColor.YELLOW + "Item Name", "Click here to change the item's name")
                .anyClick(e -> {
                    getPlayer().sendMessage(ChatColor.GREEN + "Please enter the new name for this item.");
                    Callbacks.listenForChat(getPlayer(), msg -> {
                        edit.setDisplayName(ChatColor.translateAlternateColorCodes('&', msg));
                        update("Named updated");
                    });
                });

        nextRow();
        center(3);

        // Add Lore line
        addItem(Material.INK_SACK, ChatColor.GREEN + "Add Lore", "Click here to add lore.")
                .anyClick(e -> {
                    getPlayer().sendMessage(ChatColor.GREEN + "Please enter a line of lore.");
                    Callbacks.listenForChat(getPlayer(), m -> {
                        edit.addLore(ChatColor.translateAlternateColorCodes('&', m));
                        update("Lore updated");
                    });
                }).setColor(DyeColor.LIME);

        nextSlot();
        // Clear Lore
        addItem(Material.INK_SACK, ChatColor.RED + "Clear Lore", "Click here to clear lore")
                .anyClick(e -> {
                    edit.clearLore();
                    update(ChatColor.RED + "Lore cleared");
                });

        nextRow();

        // Add Enchantment editor.
        for (Enchantment e : display.getEnchantments().keySet()) {
            int level = display.getEnchantmentLevel(e);
            ChatColor color = (level == 0 ? ChatColor.GRAY : (level > 0 ? ChatColor.YELLOW : ChatColor.RED));
            addItem(Material.ENCHANTED_BOOK, ChatColor.YELLOW + Utils.capitalize(e.getName()),
                    "",
                    "Left-Click: " + ChatColor.WHITE + "Set Level",
                    "Right-Click: " + ChatColor.WHITE + "Remove Enchantment",
                    "",
                    "Current Level: " + color + level)
                    .leftClick(evt -> {
                        getPlayer().sendMessage(ChatColor.GREEN + "Please enter the new enchantment level.");
                        Callbacks.listenForNumber(getPlayer(), newLevel -> {
                            edit.getRawStack().addUnsafeEnchantment(e, newLevel);
                            update("Enchantment level set");
                            reconstruct();
                        });
                    }).rightClick(evt -> {
                        edit.getRawStack().removeEnchantment(e);
                        update(ChatColor.RED + "Enchant removed");
                        reconstruct();
                    });
        }

        addItem(Material.WOOL, ChatColor.YELLOW + "Add Enchant", "Click here to add an enchant.")
                .anyClick(e -> {
                    List<ItemStack> enchants = new ArrayList<>();
                    for (Enchantment ench : Enchantment.values()) {
                        if (ench.getName() == null)
                            continue;

                        if (!display.containsEnchantment(ench)) {
                            ItemStack i = ItemManager.createItem(Material.ENCHANTED_BOOK,
                                    ChatColor.YELLOW + Utils.capitalize(ench.getName()));
                            i.addUnsafeEnchantment(ench, 1);
                            enchants.add(i);
                        }
                    }

                    new GUIItemPicker(getPlayer(), enchants, i -> edit.getRawStack().addUnsafeEnchantments(i.getEnchantments()));
                }).setColor(DyeColor.YELLOW);
    }

    private void update(String message) {
        getPlayer().sendMessage(ChatColor.GREEN + message + ".");
    }

    @Override
    public void onClose() {
        if (this.onFinish != null) {
            this.onFinish.accept(edit);
            openPrevious();
        } else {
            Utils.replaceItem(getPlayer(), original, edit.generateItem());
        }
    }
}
