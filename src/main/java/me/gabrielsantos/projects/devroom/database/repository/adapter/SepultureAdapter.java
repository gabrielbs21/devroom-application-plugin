package me.gabrielsantos.projects.devroom.database.repository.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import me.gabrielsantos.projects.devroom.model.Sepulture;
import me.gabrielsantos.projects.devroom.util.InventoryAdapter;
import me.gabrielsantos.projects.devroom.util.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.UUID;

public final class SepultureAdapter implements SQLResultAdapter<Sepulture> {

    @Override
    public Sepulture adaptResult(SimpleResultSet resultSet) {
        final JsonElement location = JsonParser.parseString(resultSet.get("location"));

        ItemStack[] items = null;

        try {
            items = InventoryAdapter.itemStackArrayFromBase64(String.valueOf(resultSet.get("owner")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Sepulture.builder()
            .owner(UUID.fromString(resultSet.get("owner")))
            .location(LocationAdapter.INSTANCE.deserialize(location, Location.class, null))
            .items(items)
            .opened(Boolean.parseBoolean(resultSet.get("opened")))
            .build();
    }

}
