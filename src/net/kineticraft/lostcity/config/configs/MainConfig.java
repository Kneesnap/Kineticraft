package net.kineticraft.lostcity.config.configs;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.data.maps.JsonMap;
import net.kineticraft.lostcity.mechanics.system.BuildType;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.lists.StringList;
import net.kineticraft.lostcity.party.Party;
import org.bukkit.inventory.ItemStack;

/**
 * Main plugin config.
 * Created by Kneesnap on 6/3/2017.
 */
@Getter @Setter
public class MainConfig extends JsonConfig {
    private String voteURL = "http://google.com/";
    private int afkLimit = 30;
    private JsonMap<String> filter = new JsonMap<>();
    private String discordToken;
    private long serverId;
    private BuildType buildType = BuildType.PRODUCTION;
    private int build;
    private int lastNotesSize;
    private Party party;
    private StringList swearWords = new StringList();
    private JsonMap<ItemStack> dungeonRewards = new JsonMap<>();
}
