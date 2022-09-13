package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.TeamSystem.TeamSelectionItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

public class PlayerJoinHandler implements Listener {

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
}
