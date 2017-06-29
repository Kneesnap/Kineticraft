package net.kineticraft.lostcity.config.configs;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.BuildType;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.maps.JsonStringMap;

/**
 * Main plugin config.
 *
 * Created by Kneesnap on 6/3/2017.
 */
@Getter @Setter
public class MainConfig extends JsonConfig {

    private String voteURL;
    private int afkLimit;
    private JsonStringMap filter = new JsonStringMap();
    private String discordToken;
    private long serverId;
    private BuildType buildType;

    @Override
    public void load(JsonData data) {
        setVoteURL(data.getString("voteURL", "http://google.com/"));
        setFilter(data.getStringMap("filter"));
        setAfkLimit(data.getInt("afkLimit", 30));
        setDiscordToken(data.getString("token", "MzI5ODI5MzA4NjczNjg3NTYz.DDYJJg.V7_gvpQbxnhdOfXr9rTJj1G1LsY"));
        setBuildType(data.getEnum("buildType", BuildType.DEV));
        setServerId(data.getLong("serverId", 199240565785624576L));
    }

    @Override
    public JsonData save() {
        return new JsonData().setString("voteURL", getVoteURL()).setElement("filter", getFilter())
                .setNum("afkLimit", getAfkLimit()).setString("token", getDiscordToken()).setEnum("buildType", getBuildType())
                .setNum("serverId", getServerId());
    }
}
