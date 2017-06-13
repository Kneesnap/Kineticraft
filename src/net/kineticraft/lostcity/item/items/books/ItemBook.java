package net.kineticraft.lostcity.item.items.books;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.item.event.ItemEvent;
import net.kineticraft.lostcity.item.event.ItemListener;
import net.kineticraft.lostcity.item.event.ItemUsage;
import net.kineticraft.lostcity.item.event.events.ItemInteractEvent;
import net.kineticraft.lostcity.utils.PacketUtil;
import net.kineticraft.lostcity.utils.TextBuilder;
import net.kineticraft.lostcity.utils.TextUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An easy book builder.
 * TODO: Allow book editting.
 *
 * Created by Kneesnap on 6/11/2017.
 */
@Getter @Setter
public class ItemBook extends ItemWrapper {
    private boolean signed;
    private boolean writeLines; // Whether or not the lines should be stored in the book. (Allows dynamic updating.)
    private List<TextBuilder> pages = new ArrayList<>();
    private int page; // The current page we're writing to.

    private static final int LINES_PER_PAGE = 15;

    public ItemBook() {
        this(ItemType.CUSTOM_BOOK);
        setWriteLines(true); // Books that don't have their own classes can't be reconstructed, so we must write pages to NBT.
    }

    public ItemBook(ItemType type) {
        this(type, true);
    }

    public ItemBook(ItemType type, boolean signed) {
        super(type);
        this.signed = signed;
    }

    public ItemBook(ItemStack item) {
        super(item);
        this.signed = item.getType() == Material.WRITTEN_BOOK;
    }

    /**
     * Generates the complete book, with book pages.
     * @return book
     */
    public ItemStack generateFullBook() {
        boolean save = isWriteLines();
        setWriteLines(true);
        ItemStack itemStack = generateItem();
        setWriteLines(save);
        return itemStack;
    }

    /**
     * Get the title of this book.
     * @return title
     */
    public String getTitle() {
        return getMeta().getTitle();
    }

    /**
     * Get the author of this book.
     * @return author
     */
    public String getAuthor() {
        return getMeta().getAuthor();
    }

    /**
     * Set the title of this book.
     * @param title
     * @return this
     */
    public ItemBook setTitle(String title) {
        getMeta().setTitle(title);
        return this;
    }

    /**
     * Set the author of this book.
     * @param author
     * @return this
     */
    public ItemBook setAuthor(String author) {
        getMeta().setAuthor(author);
        return this;
    }

    protected TextBuilder getPage() {
        if (pages.size() <= this.page)
            pages.add(new TextBuilder());
        return pages.get(page);
    }

    /**
     * Append text to the current line.
     * @param text
     * @return this
     */
    public ItemBook addText(String text) {
        if (getPage().getLineCount(TextUtils.BOOK_SIZE) >= LINES_PER_PAGE)
            nextPage();
        getPage().append(text);
        return this;
    }

    /**
     * Append this text to a new line.
     * @param text
     * @return this
     */
    public ItemBook addLine(String text) {
        addText("\n"); // New line
        return addText(text);
    }

    /**
     * Color the current text.
     * @param color
     * @return this
     */
    public ItemBook color(ChatColor color) {
        getPage().color(color);
        return this;
    }

    /**
     * Make the current text bold.
     * @return this
     */
    public ItemBook bold() {
        getPage().bold(true);
        return this;
    }

    /**
     * Underline the current text.
     * @return this
     */
    public ItemBook underline() {
        getPage().underlined(true);
        return this;
    }

    /**
     * Add italics to the current text.
     * @return this
     */
    public ItemBook italicize() {
        getPage().italic(true);
        return this;
    }

    /**
     * Strike through the current text.
     * @return this
     */
    public ItemBook strikethrough() {
        getPage().strikethrough(true);
        return this;
    }

    /**
     * Obfuscate the current text.
     * @return this
     */
    public ItemBook obfuscate() {
        getPage().obfuscated(true);
        return this;
    }

    /**
     * Show an item when the current text is hovered.
     * @param itemStack
     * @return this
     */
    public ItemBook showItem(ItemStack itemStack) {
        getPage().showItem(itemStack);
        return this;
    }

    /**
     * Show text when hovered.
     * @param text
     * @return this
     */
    public ItemBook showText(String text) {
        getPage().showText(text);
        return this;
    }

    /**
     * Run a command when clicked.
     * @param cmd
     * @return this
     */
    public ItemBook runCommand(String cmd) {
        getPage().runCommand(cmd);
        return this;
    }

    /**
     * Open a url when the current text is clicked.
     * @param url
     * @return book
     */
    public ItemBook openURL(String url) {
        getPage().openURL(url);
        return this;
    }

    /**
     * Change the page the player is currently viewing.
     * @param newPage
     * @return this
     */
    public ItemBook changePage(int newPage) {
        getPage().changePage(newPage);
        return this;
    }

    /**
     * Start writing to the next page.
     * @return this
     */
    public ItemBook nextPage() {
        this.page++;
        return this;
    }

    @Override
    public CraftMetaBook getMeta() {
        return (CraftMetaBook) super.getMeta();
    }

    @Override
    public ItemStack getRawStack() {
        return new ItemStack(isSigned() ? Material.WRITTEN_BOOK : Material.BOOK_AND_QUILL);
    }

    @Override
    public void updateItem() {

        getMeta().setPages(Arrays.asList(""));
        if (isWriteLines()) // Save the book pages to the item.
            getMeta().pages.addAll(getPages().stream().map(TextBuilder::getComponents)
                    .map(TextUtils::toNMSComponent).collect(Collectors.toList()));

        getMeta().setGeneration(BookMeta.Generation.TATTERED);
        this.page = 0; // Reset writer.
    }

    @ItemListener(ItemUsage.RIGHT_CLICK)
    public void onInteract(ItemInteractEvent evt) {
        open(evt.getPlayer());
    }

    /**
     * Open this book for the given player.
     * @param player
     */
    public void open(Player player) {
        player.sendMessage("hi.");
        getPages().stream().map(ComponentBuilder::create).forEach(player::sendMessage);
        PacketUtil.openBook(player, generateFullBook());
    }
}
