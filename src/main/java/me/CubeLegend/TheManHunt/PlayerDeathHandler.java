package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.PersistentData.PersistentDataHandler;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerDeathHandler implements Listener {

    private final ArrayList<UUID> deadRunners = new ArrayList<>();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Bukkit.getScheduler().runTaskLater(TheManHunt.getInstance(), () -> player.spigot().respawn(), 2);

        if (TeamHandler.getInstance().getTeam("Runners").checkForMember(player)) {
            deadRunners.add(player.getUniqueId());
            if (deadRunners.size() == TeamHandler.getInstance().getTeam("Runners").getMemberCount()) {
                PersistentDataHandler pdh = PersistentDataHandler.getInstance();
                pdh.setAllHunterWins(pdh.getAllHunterWins() + 1);
                pdh.saveData();

                TeamHandler.getInstance().getTeam("Hunters").win();
                TeamHandler.getInstance().getTeam("Runners").lose();
            }
            Bukkit.getScheduler().runTaskLater(TheManHunt.getInstance(), () -> player.setGameMode(GameMode.SPECTATOR),1);
        }
    }
}
