package net.kineticraft.lostcity.commands.player

import net.kineticraft.lostcity.commands.StaffCommand
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class CommandSab: StaffCommand("", "Prints if you're Sab.", "sab", "superanimeboi") {
    override fun onCommand(sender: CommandSender?, args: Array<out String>?) {
        if (sender?.name.equals("SuperAnimeBoi")) {
            sender?.sendMessage("${ChatColor.BLUE}You are Sab! Welcome back my dude!")
        }
    }
}