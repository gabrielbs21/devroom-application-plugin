package me.gabrielsantos.projects.devroom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data(staticConstructor = "of")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public final class Sepulture {

    private final UUID owner;

    private final Location location;

    private final ItemStack[] items;

    @Builder.Default private boolean opened = false;

}
