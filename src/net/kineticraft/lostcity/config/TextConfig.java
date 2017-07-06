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

    private List<TextBuilder> components = new ArrayList<>();

    @Override
    protected void load(List<String> lines) {
        components = lines.stream().map(TextUtils::fromMarkup).collect(Collectors.toList());
    }

    @Override
    public List<String> getLines() {
        return getComponents().stream().map(TextBuilder::toMarkup).collect(Collectors.toList());
    }

    public BaseComponent[] toArray() {
        List<BaseComponent> all = new ArrayList<>();
        getComponents().stream().map(TextBuilder::create).map(Arrays::stream).forEach(s -> s.forEach(all::add));
        return all.toArray(new BaseComponent[0]);
    }
}
