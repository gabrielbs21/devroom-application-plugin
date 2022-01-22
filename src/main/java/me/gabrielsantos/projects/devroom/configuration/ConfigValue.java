package me.gabrielsantos.projects.devroom.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.gabrielsantos.projects.devroom.Sepultures;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Function;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigValue {

    private static final ConfigValue instance = new ConfigValue();

    private final ConfigurationSection configuration = Sepultures.getInstance().getConfig();

    private final int broadcastGraveLocationAfter = (int) this.field("broadcast-grave-location-after");
    private final int deleteGraveAfter = (int) this.field("delete-grave-after");
    private final int sendLocationMessage = (int) this.field("send-location-message");

    public static <T> T get(Function<ConfigValue, T> supplier) {
        return supplier.apply(ConfigValue.instance);
    }

    private Object field(String key) {
        return configuration.get(key);
    }

}
