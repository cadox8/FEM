package com.cadiducho.fem.color;

import com.cadiducho.fem.core.api.FEMServer;
import com.cadiducho.fem.core.api.FEMUser;
import com.cadiducho.fem.color.listener.PlayerListener;
import com.cadiducho.fem.color.listener.WorldListener;
import com.cadiducho.fem.color.manager.ArenaManager;
import com.cadiducho.fem.color.manager.GameManager;
import com.cadiducho.fem.color.manager.GameState;
import com.cadiducho.fem.color.util.Messages;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DyeOrDie extends JavaPlugin {

    private static DyeOrDie instance;
    
    public static ArrayList<DyePlayer> players = new ArrayList<>();
    private final static ChatColor[] colors = { ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE, ChatColor.DARK_GRAY, 
            ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GRAY, ChatColor.GREEN };

    @Getter private ArenaManager am;
    @Getter private GameManager gm;
    @Getter private Messages msg;

    @Override
    public void onEnable() {
        instance = this;
        
        File fConf = new File(getDataFolder(), "config.yml");
        if (!fConf.exists()) {
            try {
                getConfig().options().copyDefaults(true);
                saveConfig();
                //log("Generando archivo config.yml correctamente");
            } catch (Exception e) {
                /*log("Fallo al generar el config.yml!");
                debugLog("Causa: " + e.toString());*/
            }
        }

        gm = new GameManager(instance);
        am = new ArenaManager(instance);
        am.prepareWorld(getServer().getWorld(getConfig().getString("Color.Arena.mundo")));
        msg = new Messages(instance);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(instance), instance);
        pm.registerEvents(new WorldListener(instance), instance);

        GameState.state = GameState.LOBBY;
        getLogger().log(Level.INFO, "Color: Activado correctamente");
        
        //Evento para la caida al vacio
        getServer().getScheduler().runTaskTimer(instance, () -> {
            if (getGm().isInGame()) {
                getGm().getPlayersInGame().stream()
                        .filter(player -> (player.getLocation().getBlockY() < 0))
                        .forEach(player -> getPlayer(player).endGame());
            }
        }, 20, 20);
        getServer().getScheduler().runTaskTimer(this, () -> FEMServer.sendStatus(getConfig().getString("id"), GameState.getParsedStatus(), getGm().getPlayersInGame().size() + "/" + getAm().getMaxPlayers()), 20L, 20L);
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Color: Desativado correctamente");
    }

    public static DyeOrDie getInstance() {
        return instance;
    }
    
    public static DyePlayer getPlayer(OfflinePlayer p) {
        FEMUser u = FEMServer.getUser(p);
        for (DyePlayer pl : players) {
            if (pl.getBase().getUuid() == null) {
                continue;
            }
            if (pl.getBase().getUuid().equals(p.getUniqueId())) {
                return pl;
            }
        }
        DyePlayer us = new DyePlayer(u);
        if (us.getBase().getBase().isOnline()) {
            players.add(us);
        }
        return us;
    }
    
    public static String colorize(String message) {
        char[] csarray = message.toCharArray();
        StringBuilder colorized = new StringBuilder();
        char[] arrayOfChar1;
        int j = (arrayOfChar1 = csarray).length;
        for (int i = 0; i < j; i++) {
            char c = arrayOfChar1[i];
            colorized.append(colors[new Random().nextInt(colors.length)]);
            colorized.append(c);
        }
        return colorized.toString();
    }

}
