package net.kineticraft.lostcity.utils;

import net.kineticraft.lostcity.item.guis.GenericItem;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An extension of md5's ComponentBuilder.
 * Contains boilerplate utilities.
 *
 * Created by Kneesnap on 6/12/2017.
 */
public class TextBuilder extends ComponentBuilder {

    public TextBuilder() {
        this("");
    }

    public TextBuilder(String text) {
        super(text);
    }

    @Override // Don't retain any previous formatting.
    public TextBuilder append(String text) {
        append(text, FormatRetention.NONE);
        return this;
    }

    /**
     * Bold the current text
     * @return this
     */
    public TextBuilder bold() {
        bold(true);
        return this;
    }

    /**
     * Underline the current text
     * @return this
     */
    public TextBuilder underline() {
        underlined(true);
        return this;
    }

    /**
     * Obfuscate the current text
     * @return this
     */
    public TextBuilder obfuscate() {
        obfuscated(true);
        return this;
    }

    /**
     * Italicize the current text.
     * @return
     */
    public TextBuilder italicize() {
        italic(true);
        return this;
    }

    /**
     * Strikethrough the current text
     * @return this.
     */
    public TextBuilder strikethrough() {
        strikethrough(true);
        return this;
    }

    /**
     * Change the color of the current text.
     * Accepts the Bukkit ChatColor, instead of bungee.
     *
     * @param color
     * @return this
     */
    public TextBuilder color(ChatColor color) {
        color(ColorConverter.toBungee(color));
        return this;
    }

    /**
     * Make the player run a command when the current text is clicked.
     * @param command
     * @return this
     */
    public TextBuilder runCommand(String command) {
        event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }

    /**
     * Make the player change the page of the book they are viewing when clicked.
     * @param page
     * @return this
     */
    public TextBuilder changePage(int page) {
        event(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, String.valueOf(page)));
        return this;
    }

    /**
     * Make the player open a website when clicked.
     * @param url
     * @return this
     */
    public TextBuilder openURL(String url) {
        event(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return this;
    }

    /**
     * Show text when the current text is hovered.
     * @param text
     * @return this
     */
    public TextBuilder showText(String text) {
        event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(text)}));
        return this;
    }

    /**
     * Show an item when the current text is hovered.
     * @param item
     * @return this
     */
    public TextBuilder showItem(ItemStack item) {
        event(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                new BaseComponent[] {new TextComponent(new GenericItem(item).getFullTag().toString())}));
        return this;
    }

    /**
     * Get the number of lines that will display to the user from this page.
     * @return count
     */
    public int getLineCount(int lineSize) {
        return TextUtils.getLinesUsed(getParts().stream().map(bc -> BaseComponent.toLegacyText(bc))
                .collect(Collectors.joining()), lineSize);
    }

    /**
     * Returns the current text being modified.
     * Uses reflection for current lack of a better method.
     *
     * @return text
     */
    public TextComponent getCurrent() {
        return (TextComponent) ReflectionUtil.getField(this, "current");
    }

    /**
     * Returns the list of components that comprise this message.
     * Uses reflection for current lack of a better method.
     *
     * @return parts
     */
    public List<BaseComponent> getParts() {
        return (List<BaseComponent>) ReflectionUtil.getField(this, ComponentBuilder.class, "parts");
    }

    /**
     * Return the components in an array.
     * @return components
     */
    public BaseComponent[] getComponents() {
        return getParts().toArray(new BaseComponent[0]);
    }
}
