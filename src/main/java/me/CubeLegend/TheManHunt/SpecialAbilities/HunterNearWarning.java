package me.CubeLegend.TheManHunt.SpecialAbilities;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import me.CubeLegend.TheManHunt.TheManHunt;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class HunterNearWarning {

    private static HunterNearWarning instance;

    public static HunterNearWarning getInstance() {
        if (instance == null) {
            instance = new HunterNearWarning();
        }
        return instance;
    }

    private int taskID;

    private double warningRadius;

    public void startRoutine(int period, int radius) {

        warningRadius = radius;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(TheManHunt.getInstance(), () -> {
            if (!((GameHandler.getInstance().getGameState() == GameState.RUNAWAYTIME) || (GameHandler.getInstance().getGameState() == GameState.PLAYING))) {
                return;
            }
            for (Player runner : TeamHandler.getInstance().getTeam("Runners").getMembers()) {
                if (!runner.isOnline()) continue;
                double closestHunterDis = warningRadius + 1;
                for (Entity entity : runner.getNearbyEntities(warningRadius, warningRadius, warningRadius)) {
                    if (!(entity instanceof Player)) {
                        continue;
                    }
                    if (TeamHandler.getInstance().getTeam("Hunters").checkForMember((Player) entity)) {
                        double currentHunterDis = runner.getLocation().distance(entity.getLocation());
                        if (closestHunterDis > currentHunterDis) {
                            closestHunterDis = currentHunterDis;
                        }
                    }
                }
                if (closestHunterDis <= warningRadius) {
                    runner.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cA Hunter is near"));
                }
            }
        },0, period);
    }

    public void stopRoutine() {
        if (Bukkit.getScheduler().isCurrentlyRunning(taskID)) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }
}
