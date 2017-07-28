package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.item.display.GUIItem;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Store primitives.
 * Created by Kneesnap on 7/3/2017.
 */
public class PrimitiveStore<T> extends MethodStore<T> {

    private Function<T, Number> convert;
    private Function<String, T> parse;

    public PrimitiveStore(Class<T> apply, String typeName, Function<T, Number> convert, Function<String, T> parse) {
        super(apply, "setNum", "get" + typeName);
        this.convert = convert;
        this.parse = parse;
    }

    @Override
    public void editItem(GUIItem item, Object value, Consumer<Object> setter) {
        item.leftClick(ce -> {
            ce.getPlayer().sendMessage(ChatColor.GREEN + "Please enter the new value.");
            Callbacks.listenForChat(ce.getPlayer(), m -> {
                try {
                    setter.accept(parse.apply(m));
                } catch (NumberFormatException e) {
                    ce.getPlayer().sendMessage(ChatColor.RED + "Invalid number.");
                }
            }, null);
        }).rightClick(ce -> setter.accept(0))
                .addLoreAction("Left", "Set Value").addLoreAction("Right", "Reset Value");
    }

    @Override
    protected Class<Number> getSaveArgument() {
        return Number.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveField(JsonData data, Object value, String key) throws Exception {
        super.saveField(data, convert.apply((T) value), key);
    }
}
