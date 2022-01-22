package me.gabrielsantos.projects.devroom.database;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.SQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Objects;

public record SQLProvider(Plugin plugin) {

    public SQLConnector setup() {
        final ConfigurationSection configuration = plugin.getConfig().getConfigurationSection("database.sql");

        if (configuration == null) return null;

        final String sqlType = configuration.getString("type");

        SQLConnector sqlConnector = null;

        if (Objects.requireNonNull(sqlType).equalsIgnoreCase("mysql")) {

            ConfigurationSection mysqlSection = configuration.getConfigurationSection("mysql");
            if (mysqlSection != null) {
                sqlConnector = mysqlDatabaseType(mysqlSection).connect();
            }

        } else if (sqlType.equalsIgnoreCase("sqlite")) {

            ConfigurationSection sqliteSection = configuration.getConfigurationSection("sqlite");
            if (sqliteSection != null) {
                sqlConnector = sqliteDatabaseType(sqliteSection).connect();
            }

        }

        return sqlConnector;
    }

    private SQLDatabaseType sqliteDatabaseType(ConfigurationSection section) {
        return SQLiteDatabaseType.builder()
            .file(new File(plugin.getDataFolder(), Objects.requireNonNull(section.getString("file"))))
            .build();
    }

    private SQLDatabaseType mysqlDatabaseType(ConfigurationSection section) {
        return MySQLDatabaseType.builder()
            .address(section.getString("address"))
            .username(section.getString("username"))
            .password(section.getString("password"))
            .database(section.getString("database"))
            .build();
    }

}
