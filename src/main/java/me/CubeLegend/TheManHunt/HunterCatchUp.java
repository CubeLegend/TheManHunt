package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;

public class HunterCatchUp implements Listener {

    private int TaskId;

    public void startHunterCatchUpRoutine(int period) {
        TaskId = Bukkit.getServer().getScheduler().runTaskTimer(TheManHunt.getInstance(), () -> {

            List<Player> hunters = TeamHandler.getInstance().getTeam("Hunters").getMembers();
            List<Player> runners = TeamHandler.getInstance().getTeam("Runners").getMembers();
            for (Player hunter : hunters) {
                boolean distanceLarger = true;
                for (Player runner : runners) {
                    if (!Objects.equals(hunter.getLocation().getWorld(), runner.getWorld())) return;
                    if (hunter.getLocation().distance(runner.getLocation()) <= 500) distanceLarger = false;
                }
                if (distanceLarger) {
                    hunter.addPotionEffect(PotionEffectType.SPEED.createEffect(20, 7));
                    hunter.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20, 0));
                    hunter.addPotionEffect(PotionEffectType.JUMP.createEffect(20, 7));
                    hunter.addPotionEffect(PotionEffectType.DOLPHINS_GRACE.createEffect(20, 10));
                }
            }
        }, 0, period).getTaskId();
    }

    public void stopHunterCatchUpRoutine() {
        if (Bukkit.getScheduler().isCurrentlyRunning(TaskId)) {
            Bukkit.getScheduler().cancelTask(TaskId);
        }
    }
}
