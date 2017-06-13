package net.kineticraft.lostcity.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_11_R1.ChatBaseComponent;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import org.bukkit.ChatColor;

import java.util.Arrays;

/**
 * Contains basic utils and aliases to methods that are not easy to find / documented.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class TextUtils {

    public static int CHAT_SIZE = 320;
    public static int BOOK_SIZE = 120;
    public static String COLOR_CODE = ChatColor.RESET.toString().substring(0, 1);

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
        return TextComponent.fromLegacyText(legacyText);
    }

    /**
     * Convert a chat component to mojangson.
     * @param components
     * @return mojangson
     */
    public static String toString(BaseComponent[] components) {
        return ComponentSerializer.toString(components);
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
    private static String centerText(String text, int lineSize) {
        if (text == null || text.length() == 0)
            return "";

        // Perform calculations.
        int centerPixel = (lineSize / 2) - 6;
        int pixelSize = getPixelWidth(text);
        int blankPixels = centerPixel - (pixelSize / 2);
        int addedPixels = 0;

        // Add spaces
        while(addedPixels < blankPixels) {
            text = " " + text;
            addedPixels += MinecraftFont.SPACE.getCharWidth();
        }

        return text;
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
            if (c.equals(COLOR_CODE)) { // Changing color character.
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
                (getPixelWidth(line) - MinecraftFont.DEFAULT.getCharWidth()) / lineSize).sum();
    }
}
