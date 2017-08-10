package net.kineticraft.lostcity.commands.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.guis.data.GUIJsonEditor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Allow editting of json data.
 * Created by Kneesnap on 7/20/2017.
 */
public class CommandEdit extends StaffCommand {

    private static List<JsonProvider> providers = new ArrayList<>();

    public CommandEdit() {
        super(EnumRank.MOD, "<player|config> <data>", "Edit json data.", "edit");
        providers.add(new JsonProvider<>("player", KCPlayer::getWrapper, KCPlayer::updateSave));
        providers.add(new JsonProvider<>("config", c -> (JsonConfig) Configs.getConfig(c), JsonConfig::saveToDisk));
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        JsonProvider<?> pv = providers.stream().filter(p -> p.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
        if (pv == null) { // Unknown provider.
            showUsage(sender);
            return;
        }

        Jsonable j = pv.getGetter().apply(args[1]);
        if (j == null) { // Data wasn't found.
            sender.sendMessage(ChatColor.RED + "Unknown target data '" + args[1] + "'.");
            return;
        }

        new GUIJsonEditor((Player) sender, j, pv::update);
    }

    @AllArgsConstructor
    private class JsonProvider<T extends Jsonable> {

        @Getter private String name;
        @Getter private final Function<String, T> getter;
        private final Consumer<T> updater;

        /**
         * Update the refresh after it's been editted.
         * @param player
         * @param data
         */
        @SuppressWarnings("unchecked")
        public void update(Player player, Jsonable data) {
            player.sendMessage(ChatColor.GREEN + "Updated " + getName());
            if (updater != null)
                updater.accept((T) data);
        }
    }
}
