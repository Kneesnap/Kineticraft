package net.kineticraft.lostcity.utils;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * General utilities for packets.
 * We'd like to keep our NMS all in the same places, so do NMS packet related stuff here.
 *
 *
 * Created by Kneesnap on 6/9/2017.
 */
public class PacketUtil {


    /**
     * Send a packet to the given player.
     * @param player
     * @param packet
     */
    public static void sendPacket(Player player, Packet<?> packet) {
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

    public static EntityPlayer getPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
