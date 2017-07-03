package net.kineticraft.lostcity.commands.player

import net.kineticraft.lostcity.commands.PlayerCommand
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by egoscio on 7/3/17.
 */
class CommandCoords: PlayerCommand("", "Display your current coordinates.", "coord", "coords", "compass") {

    override fun onCommand(sender: CommandSender?, args: Array<out String>?) {
        val player = sender as Player
        val location = player.location
        player.sendMessage("${ChatColor.GREEN}X: ${location.x}, Y: ${location.y}, Z: ${location.z}")
    }

}