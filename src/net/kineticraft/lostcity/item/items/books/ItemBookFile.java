package net.kineticraft.lostcity.item.items.books;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

/**
 * Represents a book loaded from a text file.
 * Created by Kneesnap on 6/30/2017.
 */
@Getter @Setter
public class ItemBookFile extends ItemBook {

    private String fileName;

    public ItemBookFile(ItemStack item) {
        super(item);
        setFileName(getTagString("file"));
    }

    public ItemBookFile(String file) {
        this(ItemType.FILE_BOOK, file);
    }

    public ItemBookFile(ItemType type, String fileName) {
        super(type, Utils.capitalize(fileName));
        this.fileName = fileName;
    }

    @Override
    public void updateItem() {
        addMarkup(Utils.readLines(fileName + ".txt").stream().collect(Collectors.joining("\n")));
        setTagString("file", getFileName());
        super.updateItem();
    }
}
