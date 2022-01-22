package me.gabrielsantos.projects.devroom.listener;

import me.gabrielsantos.projects.devroom.manager.SepultureManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record PlayerDeathListener(SepultureManager sepultureManager) implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PlayerDeathEvent event) {
        final Player player = event.getPlayer();

        final List<ItemStack> drops = event.getDrops();

        this.sepultureManager.createSepulture(
            player.getLocation(),
            player,
            drops.toArray(new ItemStack[]{})
        );

        drops.clear();
    }

}
