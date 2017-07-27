package net.kineticraft.lostcity.commands.misc;

import lombok.Getter;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.item.items.books.ItemBook;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Open a book.
 * Created by Kneesnap on 6/30/2017.
 */
@Getter
public class CommandBook extends PlayerCommand {

    private ItemType bookType;

    public CommandBook(ItemType book, String... alias) {
        super("", "Open the " + alias[0] + ".", alias);
        this.bookType = book;
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        ((ItemBook) getBookType().makeSimple()).open((Player) sender);
    }
}
