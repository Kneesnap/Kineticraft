package net.kineticraft.lostcity.item.items.books;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.item.ItemWrapper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * An easy book builder.
 *
 *
 *
 * Created by Kneesnap on 6/11/2017.
 */
@Getter @Setter
public class ItemBook extends ItemWrapper {
    private String title;
    private String author;
    private boolean signed;

    public ItemBook() {
        this(ItemType.CUSTOM_BOOK);
    }

    public ItemBook(ItemType type) {
        super(type);
    }

    public ItemBook(ItemStack item) {
        super(item);
        this.signed = item.getType() == Material.WRITTEN_BOOK;
        this.title = getMeta().getTitle();
        this.author = getMeta().getAuthor();
    }

    @Override
    public BookMeta getMeta() {
        return (BookMeta) super.getMeta();
    }

    @Override
    public ItemStack getRawStack() {
        return new ItemStack(isSigned() ? Material.WRITTEN_BOOK : Material.BOOK_AND_QUILL);
    }

    @Override
    public void updateItem() {
        getMeta().setTitle(getTitle());
        getMeta().setAuthor(getAuthor());
        getMeta().setGeneration(BookMeta.Generation.TATTERED);
    }
}
