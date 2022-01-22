package me.gabrielsantos.projects.devroom.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public final class ColorUtil {

    public static String colored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
