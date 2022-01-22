package me.gabrielsantos.projects.devroom.database.repository;

import com.google.gson.JsonElement;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.RequiredArgsConstructor;
import me.gabrielsantos.projects.devroom.database.repository.adapter.SepultureAdapter;
import me.gabrielsantos.projects.devroom.model.Sepulture;
import me.gabrielsantos.projects.devroom.util.InventoryAdapter;
import me.gabrielsantos.projects.devroom.util.LocationAdapter;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.Location;

import java.util.Set;

@RequiredArgsConstructor
public final class SepultureRepository {

    private final String TABLE = "nextspawnershop_data";

    private final SQLExecutor executor;

    public void createTable() {
        executor.updateQuery("CREATE TABLE IF NOT EXISTS `" + TABLE + "`(" +
            "`owner` CHAR(36) NOT NULL PRIMARY KEY," +
            "`location` TEXT NOT NULL," +
            "`items` TEXT NOT NULL," +
            "`opened` INTEGER(1) NOT NULL" +
            ");"
        );
    }

    public Sepulture selectOne(String query) {
        if (query == null) query = "";

        return executor.resultOneQuery(
            String.format("SELECT * FROM `%s` %s", TABLE, query),
            $ -> {
            },
            SepultureAdapter.class
        );
    }

    public Set<Sepulture> selectAll(String query) {
        if (query == null) query = "";

        return executor.resultManyQuery(
            String.format("SELECT * FROM `%s` %s", TABLE, query),
            $ -> {
            },
            SepultureAdapter.class
        );
    }

    public Set<Sepulture> selectAll() {
        return selectAll(null);
    }

    public void saveOne(Sepulture sepulture) {
        executor.updateQuery(
            String.format("REPLACE INTO `%s` VALUES(?,?,?,?)", TABLE),
            statement -> {
                final JsonElement location = LocationAdapter.INSTANCE.serialize(sepulture.getLocation(), Location.class, null);

                final String items = InventoryAdapter.itemStackArrayToBase64(sepulture.getItems().clone());

                statement.set(1, sepulture.getOwner().toString());
                statement.set(2, location);
                statement.set(3, items);
                statement.set(4, BooleanUtils.toInteger(sepulture.isOpened()));
            }
        );
    }

}
