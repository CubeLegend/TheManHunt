package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.StateSystem.GameHandler;
import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.StateSystem.GameStateChangeEvent;
import me.CubeLegend.TheManHunt.TeamSystem.TeamSelectionItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

public class LobbyHandler implements Listener {

    private final TeamSelectionItem tsi = new TeamSelectionItem();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (GameHandler.getInstance().getGameState() == GameState.IDLE) {
            Inventory playerInventory = player.getInventory();
            if (!playerInventory.contains(tsi.getItem())) {
                playerInventory.addItem(tsi.getItem());
            }
        }

        for (String permission : Settings.getInstance().GiveEveryonePermissions) {
            player.addAttachment(TheManHunt.getInstance(), "TheManHunt." + permission, true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (GameHandler.getInstance().getGameState() == GameState.IDLE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getChangeFrom() != GameState.IDLE) return;
        if (event.getChangeTo() == GameState.RUNAWAYTIME || event.getChangeTo() == GameState.PLAYING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                tsi.removeFromPlayer(player);
            }
        }
    }
}
