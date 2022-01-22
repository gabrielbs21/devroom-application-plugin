package me.gabrielsantos.projects.devroom.listener;

import me.gabrielsantos.projects.devroom.Sepultures;
import me.gabrielsantos.projects.devroom.configuration.MessageValue;
import me.gabrielsantos.projects.devroom.manager.SepultureManager;
import me.gabrielsantos.projects.devroom.model.Sepulture;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public record OpenSepultureListener(Sepultures sepultures, SepultureManager sepultureManager) implements Listener {

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        final Sepulture localSepulture = this.sepultureManager.getLocalSepulture(clickedBlock.getLocation());

        if (localSepulture == null) return;

        if (localSepulture.isOpened()) return;

        event.setCancelled(true);

        if (event.getAction().isRightClick()) {
            final boolean usingDeathKey = this.sepultureManager.isUsingDeathKey(player);

            if (!localSepulture.getOwner().equals(player.getUniqueId()) && !usingDeathKey) {
                player.sendMessage(MessageValue.get(MessageValue::wrongGrave));
                return;
            }

            this.sepultureManager.openSepulture(localSepulture, player, usingDeathKey, true);
            if (usingDeathKey) {
                final OfflinePlayer sepultureOwner = Bukkit.getOfflinePlayer(localSepulture.getOwner());

                player.sendMessage(MessageValue.get(MessageValue::deathKeyUsed)
                    .replace("{player}", Objects.requireNonNull(sepultureOwner.getName()))
                );
            } else {
                player.sendMessage(MessageValue.get(MessageValue::graveOpened));
            }
        }
    }

}
