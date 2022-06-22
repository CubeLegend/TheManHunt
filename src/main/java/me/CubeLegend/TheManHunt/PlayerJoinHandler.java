package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.TeamSystem.TeamSelectionItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

public class PlayerJoinHandler implements Listener {

    private final TeamSelectionItem tsi = new TeamSelectionItem();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (GameHandler.getInstance().getGameState() == GameState.IDLE) {
            Inventory playerInventory = event.getPlayer().getInventory();
            if (!playerInventory.contains(tsi.getTeamSelectionItem())) {
                playerInventory.addItem(tsi.getTeamSelectionItem());
            }
        }
    }
}
