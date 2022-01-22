package me.gabrielsantos.projects.devroom;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.Getter;
import me.gabrielsantos.projects.devroom.database.SQLProvider;
import me.gabrielsantos.projects.devroom.database.repository.SepultureRepository;
import me.gabrielsantos.projects.devroom.listener.OpenSepultureListener;
import me.gabrielsantos.projects.devroom.listener.PlayerDeathListener;
import me.gabrielsantos.projects.devroom.manager.SepultureManager;
import me.gabrielsantos.projects.devroom.model.Sepulture;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Sepultures extends JavaPlugin {

    private SQLExecutor sqlExecutor;

    private SepultureRepository sepultureRepository;
    private SepultureManager sepultureManager;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        try {
            final SQLConnector sqlConnector = new SQLProvider(this).setup();
            this.sqlExecutor = new SQLExecutor(sqlConnector);

            this.sepultureRepository = new SepultureRepository(this.sqlExecutor);
            this.sepultureRepository.createTable();

            this.sepultureManager = new SepultureManager(this.sepultureRepository);
            this.sepultureManager.init();

            this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(
                sepultureManager
            ), this);

            this.getServer().getPluginManager().registerEvents(new OpenSepultureListener(
                this,
                sepultureManager
            ), this);

            this.registerDeathKeyRecipe();

            this.getLogger().info("Plugin successfully enabled.");
        } catch (Throwable t) {
            t.printStackTrace();
            this.getLogger().severe("An error occurred during the plugin initialization!");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        for (Sepulture sepulture : this.sepultureManager.getSepultures().values()) {
            this.sepultureRepository.saveOne(sepulture);
        }
    }

    private void registerDeathKeyRecipe() {
        final NamespacedKey key = new NamespacedKey(this, "death_key");

        final ShapelessRecipe deathKeyRecipe = new ShapelessRecipe(key, SepultureManager.DEATH_KEY_ITEMSTACK);

        deathKeyRecipe.addIngredient(Material.TOTEM_OF_UNDYING);
        deathKeyRecipe.addIngredient(Material.EMERALD);

        this.getServer().addRecipe(deathKeyRecipe);
    }

    public static Sepultures getInstance() {
        return getPlugin(Sepultures.class);
    }

}
