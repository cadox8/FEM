package com.cadiducho.fem.gem.task;

import com.cadiducho.fem.core.api.FEMServer.GameID;
import com.cadiducho.fem.core.util.Title;
import com.cadiducho.fem.gem.GemHunters;
import com.cadiducho.fem.gem.GemPlayer;
import com.cadiducho.fem.gem.manager.GameState;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTask extends BukkitRunnable {

    private final GemHunters plugin;

    public CountdownTask(GemHunters instance) {
        plugin = instance;
    }

    private int count = 10;

    @Override
    public void run() {
        plugin.getGm().getPlayersInGame().stream().forEach(pl -> pl.setLevel(count));
        switch (count){
            case 10:
                //Colocar jugadores
                plugin.getTm().cleanTeams();
                plugin.getTm().drawTeams(plugin.getGm().getPlayersInGame());
                plugin.getGm().getPlayersInGame().stream().forEach(p -> GemHunters.getPlayer(p).spawn());
                break;
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                plugin.getMsg().sendBroadcast("&7El juego empezará en " + count);
                plugin.getGm().getPlayersInGame().stream().forEach(p -> p.playSound(p.getLocation(), Sound.NOTE_PLING, 1F, 1F));
                break;
            case 0:
                GameState.state = GameState.HIDDING;
                plugin.getGm().getPlayersInGame().stream().forEach(p -> {
                    p.playSound(p.getLocation(), Sound.EXPLODE, 1F, 1F);
                    Title.sendTitle(p, 1, 3, 1, "&b&l¡Esconde tu gema!", "");
                    p.setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());

                    final GemPlayer gp = GemHunters.getPlayer(p);
                    gp.setCleanPlayer(GameMode.SURVIVAL);
                    gp.dressPlayer();
                    gp.setHiddingScoreboard();
                    gp.getUserData().addPlay(GameID.GEMHUNTERS);
                    gp.save();
                });

                //Iniciar hilo de la fase de esconder
                new HiddingTask(plugin).runTaskTimer(plugin, 1l, 20l);
                cancel();
                break;
        }
        --count;
    }
}
