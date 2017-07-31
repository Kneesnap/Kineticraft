package net.kineticraft.lostcity.commands;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.Command;
import net.kineticraft.lostcity.commands.CommandType;
import net.kineticraft.lostcity.discord.DiscordSender;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.command.CommandSender;

/**
 * A command that can be executed only in discord.
 * Created by Kneesnap on 6/28/2017.
 */
@Getter @Setter
public abstract class DiscordCommand extends Command {

    private boolean deleteMessage;
    private EnumRank minRank;

    public DiscordCommand(String usage, String help, String... alias) {
        this(null, usage, help, alias);
    }

    public DiscordCommand(EnumRank minRank, String usage, String help, String... alias) {
        super(CommandType.DISCORD, usage, help, alias);
        this.minRank = minRank;
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        onCommand((DiscordSender) sender, args);

        if (isDeleteMessage())
            ((DiscordSender) sender).getMessage().delete().queue();
    }

    @Override
    public boolean canUse(CommandSender sender, boolean showMessage) {
        if (!(sender instanceof DiscordSender))
            return false;

        DiscordSender discord = (DiscordSender) sender;

        if (!DiscordAPI.isVerified(discord.getUser()) && getMinRank() != null) {
            if (showMessage)
                sender.sendMessage("You must be verified to use this command. Type /verify.");
            return false;
        }

        if (getMinRank() != null && !Utils.getRank(discord).isAtLeast(getMinRank())) {
            if (showMessage)
                sender.sendMessage("You must be at least rank " + getMinRank().getName() + " to use this command.");
            return false;
        }

        return true;
    }

    protected abstract void onCommand(DiscordSender sender,  String[] args);
}
