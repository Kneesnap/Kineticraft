package net.kineticraft.lostcity.config;

import lombok.Getter;
import net.kineticraft.lostcity.utils.TextBuilder;
import net.kineticraft.lostcity.utils.TextUtils;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuration specifically for chat components.
 *
 * Created by Kneesnap on 6/28/2017.
 */
@Getter
public class TextConfig extends RawConfig {

    private TextBuilder text;

    @Override
    protected void load(List<String> lines) {
        text = TextUtils.fromMarkup(lines.stream().collect(Collectors.joining("\n")));
    }

    @Override
    public List<String> getLines() {
        return Arrays.asList(text.toMarkup().split("\n"));
    }
}
