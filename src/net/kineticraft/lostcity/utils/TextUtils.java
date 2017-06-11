package net.kineticraft.lostcity.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;

/**
 * Contains basic utils and aliases to methods that are not easy to find / documented.
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class TextUtils {
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
}
