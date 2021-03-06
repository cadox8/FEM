package com.cadiducho.fem.gem.manager;

import com.cadiducho.fem.core.util.Metodos;
import com.cadiducho.fem.gem.GemHunters;
import com.cadiducho.fem.gem.GemPlayer;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamManager {

    private final GemHunters plugin;

    public TeamManager(GemHunters instance) {
        plugin = instance;
    }

    public Scoreboard board;
    public Team rojo;
    public Team azul;
    
    @Getter private final HashMap<Team,  ArrayList<Player>> jugadores = new HashMap<>();
    @Getter private final HashMap<Team, Location> teams = new HashMap<>();

    public void initTeams() {
        board = plugin.getGameBoard().getScoreboard();
        cleanTeams();
        
        rojo = board.getTeam("1byd_rojo") == null ? board.registerNewTeam("1byd_rojo") : board.getTeam("1byd_rojo");
        azul = board.getTeam("2byd_azul") == null ? board.registerNewTeam("2byd_azul") : board.getTeam("2byd_azul");

        rojo.setPrefix("§c");
        azul.setPrefix("§3");

        rojo.setDisplayName("Rojo");
        azul.setDisplayName("Azul");
        
        rojo.setAllowFriendlyFire(false);
        azul.setAllowFriendlyFire(false);
        
        //Evitar nulls
        plugin.getGm().getGemas().put(rojo, new ArrayList<>()); 
        plugin.getGm().getGemas().put(azul, new ArrayList<>());
        
        jugadores.put(rojo, new ArrayList<>());
        jugadores.put(azul, new ArrayList<>());
        
        teams.put(rojo, Metodos.stringToLocation(plugin.getConfig().getString("GemHunters.Arena.Spawn.morado")));
        teams.put(azul, Metodos.stringToLocation(plugin.getConfig().getString("GemHunters.Arena.Spawn.amarillo")));
    }

    public int getPuntos(Team t) {
        return plugin.getGm().getGemas().get(t).size();
    }
    
    public void addPunto(Team t, Location loc) {
        HashMap<Team, ArrayList<Location>> hGemas = plugin.getGm().getGemas();
        ArrayList<Location> array = hGemas.get(t);
        if (array == null) {
            System.out.println("Array null, intentamos fixear...");
            array = new ArrayList<>();
        }
        array.add(loc);
        hGemas.remove(t);
        hGemas.put(t, array);
        //plugin.getMsg().sendBroadcast("El equipo " + t.getDisplayName() + " ha conseguido un punto");
    }
    
    public boolean tryRemoveGema(Player p, Location loc) {
        HashMap<Team, ArrayList<Location>> hGemas = plugin.getGm().getGemas();
        Team oppositeTeam = getOpositeTeam(getTeam(p.getPlayer()));
        ArrayList<Location> array = hGemas.get(oppositeTeam);
        if (array == null) {
            array = new ArrayList<>();
        }
        if (array.contains(loc)) {
            array.remove(loc);
            hGemas.remove(oppositeTeam);
            hGemas.put(oppositeTeam, array);
            plugin.getMsg().sendBroadcast(plugin.getTm().getTeam(p.getPlayer()).getPrefix() + p.getName() + "&e ha roto una gema del equipo " + oppositeTeam.getPrefix() +  oppositeTeam.getDisplayName());
            jugadores.get(getTeam(p.getPlayer())).forEach(ally -> ally.playSound(ally.getLocation(), Sound.ORB_PICKUP, 1F, 1F));
            jugadores.get(oppositeTeam).forEach(ally -> ally.playSound(ally.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 1F));
            
            final GemPlayer gp = GemHunters.getPlayer(p);
            gp.getUserData().setGemDestroyed(gp.getUserData().getGemDestroyed() + 1);
            gp.save();
            
            plugin.getGm().checkWinner();
            return true;
        }
        return false;
    }
    
    public Team getTeam(Player pl) {
        return board.getEntryTeam(pl.getName());
    }
    
    public Team getOpositeTeam(Team t) {
        if (t.getDisplayName().equals("Rojo")) return azul;
        if (t.getDisplayName().equals("Azul")) return rojo;
        else return null;
    }
    
    public Color getColor(Player pl) {
        switch (getTeam(pl).getPrefix()) {
            case "§c":
                return Color.RED;
            case "§3":
            case "§1":
                return Color.BLUE;
        }
        return Color.MAROON;
    }
    
    public void cleanTeams() {
        teams.keySet().forEach(t -> t.getEntries().forEach(str -> t.removeEntry(str)));  
    }
    
    public void drawTeams(ArrayList<Player> pls) {
        ArrayList<Player> clon = (ArrayList<Player>) pls.clone();
        
        while (clon.size() > 0) {
            for (Team t : teams.keySet()) {
                if (clon.size() <= 0) break;
                Player pl = clon.get(clon.size() - 1);
                clon.remove(pl);
                t.addEntry(pl.getName());
                GemHunters.getPlayer(pl).sendMessage( "Has sido asignado al equipo " + t.getPrefix() + t.getDisplayName());
  
                ArrayList<Player> lista = jugadores.get(t);
                lista.add(pl);
                jugadores.replace(t, lista);
                pl.setScoreboard(board);
            }
        }
    }
}
