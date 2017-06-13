package net.kineticraft.lostcity.utils;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * General utilities for packets.
 * We'd like to keep our NMS all in the same places, so do NMS packet related stuff here.
 *
 * Created by Kneesnap on 6/9/2017.
 */
public class PacketUtil {


    /**
     * Send a packet to the given player.
     * @param player
     * @param packet
     */
    private static void sendPacket(Player player, Packet<?> packet) {
        getPlayer(player).playerConnection.sendPacket(packet);
    }

    /**
     * Update the title of the window the player is currently looking in.
     * @param player
     * @param title
     */
    public static void updateWindowTitle(Player player, String title, int size) {
        sendPacket(player, new PacketPlayOutOpenWindow(getPlayer(player).activeContainer.windowId, "minecraft:chest",
                new ChatMessage(title), size));
        player.updateInventory();
    }

    /**
     * Force open a book for the given player.
     * @param player
     * @param book
     */
    public static void openBook(Player player, ItemStack book) {
        ItemStack saved = player.getEquipment().getItemInMainHand();
        player.getEquipment().setItemInMainHand(book); // Change hand item to book.

        PacketDataSerializer pds = new PacketDataSerializer(Unpooled.buffer());
        pds.a(EnumHand.MAIN_HAND);
        sendPacket(player, new PacketPlayOutCustomPayload("MC|BOpen", pds));

        player.getEquipment().setItemInMainHand(saved); // Restore hand item.
    }

    /**
     * Get the NMS player object of a bukkit player.
     * @param player
     * @return nmsPlayer
     */
    public static EntityPlayer getPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
