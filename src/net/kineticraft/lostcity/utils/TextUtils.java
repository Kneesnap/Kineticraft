package net.kineticraft.lostcity.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_12_R1.ChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * Contains basic utils and aliases to methods that are not easy to find / documented.
 * Created by Kneesnap on 6/10/2017.
 */
public class TextUtils {

    public static int CHAT_SIZE = 320;
    public static int BOOK_SIZE = 120;

    /**
     * Convert base components to an NMS component.
     * @param cmp
     * @return nmsComponent
     */
    public static IChatBaseComponent toNMSComponent(BaseComponent... cmp) {
        return ChatBaseComponent.ChatSerializer.a(toString(cmp));
    }

    /**
     * Convert a string from mojangson to basecomponents.
     * @param mojangson
     * @return components
     */
    public static BaseComponent[] fromMojangson(String mojangson) {
        return ComponentSerializer.parse(mojangson);
    }

    /**
     * Convert a legacy message (&cHello) -> {"text":"Hello","color":"red"}
     * @param legacyText
     * @return components
     */
    public static BaseComponent[] fromLegacy(String legacyText) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', legacyText));
    }

    /**
     * Convert a chat component to mojangson.
     * @param components
     * @return mojangson
     */
    public static String toString(BaseComponent... components) {
        return ComponentSerializer.toString(components);
    }

    /**
     * Convert a chat component to mojangson.
     * @param components
     * @return mojangson
     */
    public static String toMojangson(BaseComponent... components) {
        return toString(components);
    }

    /**
     * Convert base components to legacy text.
     * @param bc
     * @return
     */
    public static String toLegacy(BaseComponent... bc) {
        return BaseComponent.toLegacyText(bc);
    }

    /**
     * Return a string that will pad text to center it.
     * @param text
     * @param lineSize
     * @return padding
     */
    private static String padCenter(String text, int lineSize) {
        if (text == null || text.length() == 0)
            return "";

        String fillWith = " ";
        // Perform calculations.
        int centerPixel = (lineSize / 2) - 6;
        int pixelSize = getPixelWidth(text);
        int blankPixels = centerPixel - (pixelSize / 2);
        int spaceSize = getPixelWidth(fillWith);

        String padding = "";
        for (int i = 0; i < blankPixels; i += spaceSize)
            padding += fillWith;

        return padding;
    }

    /**
     * Center a text component for display to a player.
     * @param tb
     * @param lineSize
     * @return centered
     */
    public static TextBuilder center(TextBuilder tb, int lineSize) {
        return tb.preceed(padCenter(tb.toLegacy(), lineSize));
    }

    /**
     * Center text for displaying in chat.
     * @param text
     * @return centered
     */
    public static String centerChat(String text) {
        return centerText(text, CHAT_SIZE);
    }

    /**
     * Center text, for displaying in a book.
     * @param text
     * @return centered
     */
    public static String centerBook(String text) {
        return centerText(text, BOOK_SIZE);
    }

    /**
     * Center text, for displaying on a given line size.
     * @param text
     * @param lineSize
     * @return centered
     */
    public static String centerText(String text, int lineSize) {
        return text != null || text.length() > 0 ? padCenter(text, lineSize) + text : "";
    }

    /**
     * Get the amount of pixels a message will take up in the client's display.
     * Only works if the client has the default width settings.
     *
     * @param text
     * @return width
     */
    public static int getPixelWidth(String text) {
        if (text == null || text.length() == 0)
            return 0;

        int pixelSize = 0;
        boolean bold = false;

        // Determine the pixel width of the string.
        for (int i = 0; i < text.length(); i++) {
            String c = text.substring(i, i + 1);
            if (c.equals(ChatColor.COLOR_CHAR)) { // Changing color character.
                bold = text.substring(i + 1, i + 2).equalsIgnoreCase("l");
            } else {
                pixelSize += MinecraftFont.getWidth(c, bold);
            }
        }

        return pixelSize;
    }

    /**
     * Gets the amount of lines this text will take up from the given line size.
     * @param text
     * @param lineSize
     * @return lines
     */
    public static int getLinesUsed(String text, int lineSize) {
        return Arrays.stream(text.split("\n")).mapToInt(line ->
                ((getPixelWidth(line) - MinecraftFont.DEFAULT.getCharWidth()) / lineSize) + 1).sum();
    }

    /**
     * Send a localized message to the given CommandSender
     * @param sender
     * @param message
     */
    public static void sendLocalized(CommandSender sender, String message, Object... args) {
        TranslatableComponent tc = new TranslatableComponent(message, args);
        sender.sendMessage(tc);
    }

    /**
     * Send a localized error message to the given CommandSender
     * @param sender
     * @param message
     */
    public static void sendLocalizedError(CommandSender sender, String message, Object... args) {
        TranslatableComponent tc = new TranslatableComponent(message, args);
        tc.setColor(net.md_5.bungee.api.ChatColor.RED);
        sender.sendMessage(tc);
    }

    /**
     * Create a string a certain length from the passed char.
     * @param c
     * @param times
     * @return created
     */
    public static String makeString(char c, int times) {
        char[] str = new char[times];
        return new String(str).replaceAll("\0", String.valueOf(c));
    }

    /**
     * Force a fixed decimal position.
     * If the decimal ends in 00, it will drop the decimal.
     *
     * @param value
     * @param decimals
     * @return fixed
     */
    public static double toFixed(double value, int decimals) {
        return (int) value != value ? Double.parseDouble(new DecimalFormat("##." + makeString('#', decimals))
                .format(value)) : (int) value;
    }

    /**
     * Converts a string of text formatted in a readable way into a ComponentBuilder.
     * This text is in a similar format to bbcode.
     *
     * @param input
     * @return textBuilder
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public static TextBuilder fromMarkup(String input) {
        input = ChatColor.translateAlternateColorCodes('&', input);
        String[] queue = input.split("");
        TextBuilder tb = new TextBuilder();

        boolean lastColor = false;
        String append = "";

        for (int i = 0; i < queue.length; i++) {
            int temp = i;
            String c = queue[i];

            if (c.equals("[")) {

                // Read tag identifier, ex: [url]
                String tagId = "";
                while (!queue[++i].equals("]"))
                    tagId += queue[i];
                i++; // Skip close bracket.

                TextTag tag;
                try {
                    tag = TextTag.valueOf(tagId.split("=")[0].toUpperCase());
                } catch (Exception e) {
                    // If it isn't a bbcode tag, treat it as text.
                    i = temp;
                    append += "[";
                    continue;
                }

                // Read the text in between the tags, [url]Click here![/url]
                String text = "";
                String endTag = "[/" + tag.name().toLowerCase() + "]";
                while (!input.substring(i).toLowerCase().startsWith(endTag))
                    text += queue[i++];
                i += endTag.length() - 1; // Continue reading after this tag ends.

                // Get the extra parameter in the tag. Defaults to the text if not preset. [url=http://google.com]
                String param = tagId.contains("=") ? tagId.substring(tagId.lastIndexOf("=") + 1) : null;

                // Apply the text.
                if (append.length() > 0) {
                    tb.append(append); // Apply the text queued.
                    append = "";
                }

                tag.apply(tb, text, param);
            } else if (c.equals(ChatColor.COLOR_CHAR)) {
                // We've encountered a color code.

                // Append new text, but only if the last element was not also a color code.
                if (!lastColor) {
                    tb.append(append);
                    append = "";
                }

                tb.format(ChatColor.getByChar(queue[++i])); // Add color code.
                lastColor = true;
            } else if (c.equals("\n")) {
                //Append as a new component on a new line.
                tb.append(append + "\n");
                append = "";
            } else {
                // This is normal text.
                append += c;
                lastColor = false;
            }
        }
        tb.append(append); // Add the last bit of text.

        return tb;
    }

    @AllArgsConstructor @Getter
    public enum TextTag {
        URL(TextBuilder::openURL, ClickEvent.Action.OPEN_URL),
        HOVER(TextBuilder::showText, HoverEvent.Action.SHOW_TEXT),
        COMMAND(TextBuilder::runCommand, ClickEvent.Action.RUN_COMMAND);

        private final BiConsumer<TextBuilder, String> tagApply;
        private final Object action;

        public void apply(TextBuilder tb, String text, String param) {
            applyText(tb, this == HOVER ? param : text);
            getTagApply().accept(tb, this == HOVER ? text : param);
        }

        private void applyText(TextBuilder textBuilder, String text) {
            if (text != null && text.length() > 0)
                textBuilder.append(text); // Apply the text to the builder.
        }
    }

    /**
     * Send a message formatted in markup to the given sender.
     * @param sender
     * @param markup
     */
    public static void sendMarkup(CommandSender sender, String markup, Object... args) {
        sender.sendMessage(fromMarkup(markup).format(args));
    }

    /**
     * Color a value on a numeric scale.
     *
     * @param value Value on scale
     * @param max - Max value on scale.
     * @return colored
     */
    public static String colorValue(double value, double max) {
        return colorValue(value, max, false);
    }

    /**
     * Color a value on a numeric scale.
     *
     * @param value Value on scale
     * @param max - Max value on scale.
     * @param reverse Should the colors reverse?
     * @return colored
     */
    public static String colorValue(double value, double max, boolean reverse) {
        String val = String.valueOf(toFixed(value, 2));
        String[] options = new String[ColorTable.values().length];
        for (int i = 0; i < options.length; i++)
            options[i] = val;
        return colorString(value, max, reverse, options);
    }

    /**
     * Colorize a string on a numeric scale.
     *
     * @param value - Value on scale
     * @param max - Max scale value
     * @param display - Display options
     * @return colored
     */
    public static String colorString(double value, double max, String... display) {
        return colorString(value, max, false, display);
    }

    /**
     * Colorize a string on a numeric scale.
     *
     * @param value - Value on scale
     * @param max - Max scale value
     * @param reverse - Should the colors reverse?
     * @param display - Display options
     * @return colored
     */
    public static String colorString(double value, double max, boolean reverse, String... display) {
        ColorTable ct = ColorTable.getFromPercent((int) (value / max * 100));
        return (reverse ? ColorTable.values()[ColorTable.values().length - ct.ordinal() - 1] : ct).getColor()
                + display[Math.min(ct.ordinal(), display.length - 1)];
    }

    @AllArgsConstructor @Getter
    private enum ColorTable {
        VERY_LOW(ChatColor.DARK_RED, 0),
        LOW(ChatColor.RED, 35),
        LOW_MID(ChatColor.GOLD, 65),
        HIGH_MID(ChatColor.YELLOW, 75),
        HIGH(ChatColor.GREEN, 85),
        VERY_HIGH(ChatColor.DARK_GREEN, 95);

        private final ChatColor color;
        private final int percentMin;

        public static ColorTable getFromPercent(int percent) {
            ColorTable table = values()[0];
            for (ColorTable ct : values())
                if (ct.getPercentMin() <= percent)
                    table = ct;
            return table;
        }
    }
}
