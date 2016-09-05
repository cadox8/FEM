package com.cadiducho.fem.core.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Wool;

/**
 *
 * @author Jake, Cadiducho
 */
public class ItemUtil {
    public static ItemStack createItem(Material material, String displayname, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Metodos.colorizar(displayname));
        ArrayList<String> Lore = new ArrayList<>();
        Lore.add(Metodos.colorizar(lore));
        meta.setLore(Lore);

        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createItem(Material material, String displayname, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayname);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, int amount, String displayname, String lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayname);
        ArrayList<String> Lore = new ArrayList<>();
        Lore.add(lore);
        meta.setLore(Lore);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String displayname) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayname);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createWool(String displayname, DyeColor dye) {
        ItemStack item = new Wool(dye).toItemStack();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayname);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createGlass(String displayname, String lore, DyeColor dye) {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, dye.getData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayname);
        ArrayList<String> Lore = new ArrayList<>();
        Lore.add(lore);
        meta.setLore(Lore);

        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createHeadPlayer(String name, List<String> lore) {
        ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta sm = (SkullMeta)playerHead.getItemMeta();
        sm.setOwner(name);
        sm.setLore(lore);
        sm.setDisplayName(Metodos.colorizar("&e"+name));
        playerHead.setItemMeta(sm);
        return playerHead;
    }
}
