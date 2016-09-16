package com.cadiducho.fem.tnt.manager;

import com.cadiducho.fem.tnt.TntWars;
import com.cadiducho.fem.tnt.task.CountdownTask;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class GameManager {

    private final TntWars plugin;

    public GameManager(TntWars instance) {
        plugin = instance;
    }

    @Getter private final ArrayList<Player> playersInGame = new ArrayList<>();
    @Getter private final ArrayList<Player> spectators = new ArrayList<>();
    @Getter private final HashMap<Team, ArrayList<Location>> gemas = new HashMap<>();

    private boolean checkStart = false;
    @Getter @Setter private boolean dañoEnCaida = true;

    public void checkStart() {
        if (checkStart == false && playersInGame.size() >= plugin.getAm().getMinPlayers()) {
            checkStart = true;
            new CountdownTask(plugin).runTaskTimer(plugin, 20l, 20l);
        }
    }

    public void addPlayerToGame(Player player) {
        if (playersInGame.contains(player)) {
            playersInGame.remove(player);
            playersInGame.add(player);
        } else {
            playersInGame.add(player);
        }
    }

    public void removePlayerFromGame(Player player) {
        playersInGame.remove(player);
    }

    public boolean isEnding() {
        return GameState.state == GameState.ENDING;
    }

    public boolean isInLobby() {
        return GameState.state == GameState.LOBBY;
    }
    
    public boolean isInCountdown() {
        return GameState.state == GameState.COUNTDOWN;
    }
    
    public boolean isInGame() {
        return GameState.state == GameState.GAME;
    }
}
