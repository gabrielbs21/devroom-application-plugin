package me.gabrielsantos.projects.devroom.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.gabrielsantos.projects.devroom.Sepultures;
import me.gabrielsantos.projects.devroom.configuration.ConfigValue;
import me.gabrielsantos.projects.devroom.configuration.MessageValue;
import me.gabrielsantos.projects.devroom.database.repository.SepultureRepository;
import me.gabrielsantos.projects.devroom.model.Sepulture;
import me.gabrielsantos.projects.devroom.util.ColorUtil;
import me.gabrielsantos.projects.devroom.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public final class SepultureManager {

    @Getter private final Map<Location, Sepulture> sepultures = new HashMap<>();

    private final SepultureRepository repository;

    final BukkitScheduler scheduler = Bukkit.getScheduler();
    final Sepultures plugin = Sepultures.getInstance();

    public void init() {
        final Set<Sepulture> notOpenedSepultures = this.repository.selectAll("WHERE `opened` = 0");

        for (Sepulture sepulture : notOpenedSepultures) {
            this.sepultures.put(sepulture.getLocation(), sepulture);
        }
    }

    public static final ItemStack DEATH_KEY_ITEMSTACK = new ItemBuilder(Material.STICK)
        .name("&cDeath Key")
        .lore(
            "&7Can be used to open other",
            "&7players' grave"
        )
        .glow()
        .build();

    public void createSepulture(Location location, Player player, ItemStack[] itemStacks) {
        final Block chest = this.spawnChest(location, player);
        final Location chestLocation = chest.getLocation();

        final Sepulture sepulture = new Sepulture(
            player.getUniqueId(),
            chestLocation,
            itemStacks
        );

        this.sepultures.put(chestLocation, sepulture);

        this.scheduler.runTaskAsynchronously(this.plugin, () -> this.repository.saveOne(sepulture));

        this.scheduler.runTaskLaterAsynchronously(this.plugin, () -> {
            if (!sepulture.isOpened()) {
                Bukkit.broadcastMessage(
                    MessageValue.get(MessageValue::graveLocationBroadcast)
                        .replace("{player}", player.getName())
                        .replace("{x}", String.valueOf(chestLocation.getBlockX()))
                        .replace("{y}", String.valueOf(chestLocation.getBlockY()))
                        .replace("{z}", String.valueOf(chestLocation.getBlockZ()))
                );
            }
        }, ConfigValue.get(ConfigValue::broadcastGraveLocationAfter) * 20L);

        this.scheduler.runTaskLater(this.plugin, () -> {
            if (!sepulture.isOpened()) {
                this.openSepulture(sepulture, player, false, false);
            }
        }, ConfigValue.get(ConfigValue::deleteGraveAfter) * 20L);

        this.scheduler.runTaskTimerAsynchronously(Sepultures.getInstance(), () -> {
            if (sepulture.isOpened()) {
                Thread.currentThread().interrupt();
                return;
            }

            player.sendMessage(MessageValue.get(MessageValue::graveGeneratedRepeatingMessage)
                .replace("{x}", String.valueOf(chestLocation.getBlockX()))
                .replace("{x}", String.valueOf(chestLocation.getBlockY()))
                .replace("{x}", String.valueOf(chestLocation.getBlockZ()))
            );
        }, 0L, ConfigValue.get(ConfigValue::sendLocationMessage) * 20L);
    }

    private Block spawnChest(Location location, Player player) {
        final Block block = location.getBlock();

        block.setType(Material.CHEST);

        final BlockData blockData = block.getBlockData();

        if (blockData instanceof Directional directional) {
            final BlockFace oppositeFace = player.getFacing().getOppositeFace();
            directional.setFacing(oppositeFace);

            final Block relative = block.getRelative(oppositeFace);

            relative.setType(Material.OAK_WALL_SIGN);

            if (relative.getBlockData() instanceof Directional signDirectional) {
                signDirectional.setFacing(oppositeFace);

                relative.setBlockData(signDirectional);
            }

            final Sign sign = (Sign) relative.getState();

            final List<String> signLines = MessageValue.get(MessageValue::graveSign);

            for (int i = 0; i < signLines.size(); i++) {
                final String line = signLines.get(i);

                sign.line(i, Component.text(ColorUtil.colored(
                    line.replace("{player}", player.getName())
                )));
            }

            sign.update();

            block.setBlockData(directional);
        }

        return block;
    }

    public Sepulture getLocalSepulture(@NotNull Location location) {
        return sepultures.getOrDefault(location, null);
    }

    public void openSepulture(Sepulture sepulture, Player player, boolean useDeathKey, boolean dropItems) {
        final Location location = sepulture.getLocation();

        final Block block = location.getBlock();

        for (BlockFace value : BlockFace.values()) {
            final Block relative = block.getRelative(value);

            if (relative.getState() instanceof Sign) {
                relative.setType(Material.AIR);
                break;
            }
        }

        block.setType(Material.AIR);

        if (dropItems) {
            for (ItemStack item : sepulture.getItems()) {
                location.getWorld().dropItemNaturally(location, item);
            }
        }

        if (useDeathKey) {
            final ItemStack item = player.getInventory().getItemInMainHand();
            item.setAmount(item.getAmount() - 1);
            player.getInventory().setItemInMainHand(item);
        }

        sepulture.setOpened(true);
    }

    public boolean isUsingDeathKey(Player player) {
        final ItemStack itemInHand = player.getInventory().getItemInMainHand();

        return itemInHand.isSimilar(DEATH_KEY_ITEMSTACK);
    }

}
