package net.kineticraft.lostcity.config;

import lombok.Getter;
import net.kineticraft.lostcity.utils.TextBuilder;
import net.kineticraft.lostcity.utils.TextUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuration specifically for chat components.
 * Created by Kneesnap on 6/28/2017.
 */
@Getter
public class TextConfig extends RawConfig {

    private List<String> lines;

    @Override
    protected void load(List<String> lines) {
        this.lines = lines;
    }

    public TextBuilder getText() {
        return TextUtils.fromMarkup(getLines().stream().collect(Collectors.joining("\n")));
    }
}
