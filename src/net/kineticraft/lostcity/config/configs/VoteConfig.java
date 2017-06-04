package net.kineticraft.lostcity.config.configs;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.config.JsonConfig;
import net.kineticraft.lostcity.data.JsonData;

import java.util.UUID;

/**
 * Holds on to vote data.
 * Created by Kneesnap on 6/3/2017.
 */
@Setter @Getter
public class VoteConfig extends JsonConfig {

    private int votesPerParty;
    private int votesUntilParty;
    private int totalVotes;
    private UUID topVoter;

    public VoteConfig() {
        super(Configs.ConfigType.VOTES);
    }

    @Override
    public void load(JsonData data) {
        setVotesPerParty(data.getInt("votesPerParty", 50));
        setVotesUntilParty(data.getInt("votesToParty", getVotesPerParty()));
        setTotalVotes(data.getInt("totalVotes"));
        setTopVoter(data.getUUID("topVoter"));
    }

    @Override
    public JsonData save() {
        JsonData data = new JsonData();
        data.setNum("votesPerParty", getVotesPerParty());
        data.setNum("votesToParty", getVotesUntilParty());
        data.setNum("totalVotes", getTotalVotes());
        data.setUUID("topVoter", getTopVoter());

        return data;
    }
}
