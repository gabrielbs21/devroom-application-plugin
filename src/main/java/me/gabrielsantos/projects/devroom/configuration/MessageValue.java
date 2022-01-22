package me.gabrielsantos.projects.devroom.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.gabrielsantos.projects.devroom.Sepultures;
import me.gabrielsantos.projects.devroom.util.ColorUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageValue {

    private static final MessageValue instance = new MessageValue();

    private final ConfigurationSection configuration = Sepultures.getInstance().getConfig().getConfigurationSection("messages");

    private final String graveLocationBroadcast = this.message("grave-location-broadcast");
    private final String wrongGrave = this.message("wrong-grave");
    private final String deathKeyUsed = this.message("death-key-used");
    private final String graveOpened = this.message("grave-opened");
    private final String graveGeneratedRepeatingMessage = this.message("grave-generated-repeating-message");

    private final List<String> graveSign = this.messageList("grave-sign");

    public static <T> T get(Function<MessageValue, T> supplier) {
        return supplier.apply(MessageValue.instance);
    }

    private String message(String key) {
        return ColorUtil.colored(configuration.getString(key));
    }

    private List<String> messageList(String key) {
        return configuration.getStringList(key)
            .stream()
            .map(ColorUtil::colored)
            .collect(Collectors.toList());
    }

}
