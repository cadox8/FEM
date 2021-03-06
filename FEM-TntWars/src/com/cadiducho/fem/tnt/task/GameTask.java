package com.cadiducho.fem.tnt.task;

import com.cadiducho.fem.core.api.FEMServer.GameID;
import com.cadiducho.fem.core.util.Title;
import com.cadiducho.fem.tnt.TntIsland;
import com.cadiducho.fem.tnt.TntPlayer;
import com.cadiducho.fem.tnt.TntWars;
import com.cadiducho.fem.tnt.manager.GameState;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private final TntWars plugin;
    public static GameTask instance;
    
    public GameTask(TntWars instance) {
        plugin = instance;
    }

    private static int count = 0;

    @Override
    public void run() {
        instance = this;
        checkWinner();
        noPlayers();
        plugin.getGm().getPlayersInGame().stream()
                .filter(p -> !TntWars.getPlayer(p).isRespawning())
                .forEach(p -> plugin.getMsg().sendActionBar(p, "&a&lTiempo jugado: " + count)
        );

        switch (count){
            case 0:
                plugin.getGm().getPlayersInGame().forEach(p -> {
                    TntIsland isla = TntIsland.getIsland(p.getUniqueId());
                    Title.sendTitle(p, 1, 5, 1, "", isla.getColor() + "¡Destruye el resto de islas!");
                    p.playSound(p.getLocation(), Sound.EXPLODE, 1F, 1F);
                    p.setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());

                    final TntPlayer tp = TntWars.getPlayer(p);
                    tp.setCleanPlayer(GameMode.SURVIVAL);
                    tp.setGameScoreboard();
                    tp.getUserData().addPlay(GameID.TNTWARS);
                    tp.save();
                });
                plugin.getAm().getIslas().forEach(i -> i.destroyCapsule());
                break;
            case 5:
                plugin.getGm().setDañoEnCaida(true);
                plugin.getAm().getGeneradores().forEach(gen -> gen.init());
                break;
        }
        ++count;
    }
    
    public void checkWinner() {
        if (plugin.getGm().getPlayersInGame().size() <= 1) {
            Player winner = plugin.getGm().getPlayersInGame().get(0);
            for (Player p : plugin.getGm().getPlayersInGame()) {
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                Title.sendTitle(winner, 1, 7, 1,"&b" + p.getName(), "&aha ganado la partida!");
            }
            plugin.getMsg().sendBroadcast("&b" + winner.getDisplayName() + " &aha ganado la partida!");
            TntPlayer tp = TntWars.getPlayer(winner);
            tp.getUserData().setCoins(TntWars.getPlayer(winner).getUserData().getCoins() + 5);
            tp.getUserData().addWin(GameID.TNTWARS);
            tp.save();
            
            end();
            cancel();
        }
    }
    
    public void end() {
        GameState.state = GameState.ENDING;

        //Cuenta atrás para envio a los lobbies y cierre del server
        //Iniciar hilo del juego
        new ShutdownTask(plugin).runTaskTimer(plugin, 20l, 20l);
    }

    public static int getTimeLeft() {
        return count;
    }

    private void noPlayers(){
        if (plugin.getGm().getPlayersInGame().isEmpty()){
            plugin.getServer().shutdown();
        }
    }
}
