package me.gabrielsantos.projects.devroom.util;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class ItemBuilder implements Cloneable {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int amount, int data) {
        this.itemStack = new ItemStack(material, amount, (byte) data);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack otherItem) {
        this.itemStack = otherItem;
        this.itemMeta = otherItem.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack, ItemMeta itemMeta) {
        this.itemStack = itemStack;
        this.itemMeta = itemMeta;
    }

    public ItemBuilder itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public ItemBuilder itemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        return this;
    }

    public ItemBuilder material(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder name(String name) {
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        itemMeta.setLore(Arrays.stream(lore)
            .map(s -> ChatColor.translateAlternateColorCodes('&', s))
            .collect(Collectors.toList())
        );
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        itemMeta.setLore(lore.stream()
            .map(s -> ChatColor.translateAlternateColorCodes('&', s))
            .collect(Collectors.toList())
        );
        return this;
    }

    public ItemBuilder loreLine(String... line) {
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.addAll(Arrays.stream(line)
            .map(s -> ChatColor.translateAlternateColorCodes('&', s))
            .toList()
        );
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder loreLineIf(boolean condition, String... line) {
        if (condition) {
            loreLine(line);
        }
        return this;
    }

    public ItemBuilder durability(short durability) {
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder data(MaterialData materialData) {
        itemStack.setData(materialData);
        return this;
    }

    public ItemBuilder acceptItemStack(Consumer<ItemStack> consumer) {
        consumer.accept(itemStack);
        return this;
    }

    public ItemBuilder acceptItemMeta(Consumer<ItemMeta> consumer) {
        consumer.accept(itemMeta);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder glow(boolean b) {
        if (b) {
            enchantment(Enchantment.ARROW_DAMAGE, 1);
            hideEnchantments();
        }
        return this;
    }

    public ItemBuilder glow() {
        enchantment(Enchantment.ARROW_DAMAGE, 1);
        hideEnchantments();
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder hideEnchantments() {
        addFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder skullOwner(String owner) {
        SkullMeta skull = (SkullMeta) itemMeta;
        itemStack.setDurability((short) 3);
        skull.setOwner(owner);
        return this;
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(itemStack.clone(), itemMeta.clone());
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
