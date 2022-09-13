package me.CubeLegend.TheManHunt.TeamSystem;

import me.CubeLegend.TheManHunt.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class TeamSelectionItem extends CustomItem {

	private static TeamSelectionItem instance;

	public static TeamSelectionItem getInstance() {
		if (instance == null) {
			instance = new TeamSelectionItem();
		}
		return instance;
	}

	public TeamSelectionItem() {
		super("Team Selector", Material.PAPER);
	}

	//open inventory on interaction with TeamSelector
	@Override
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (!event.hasItem()) return;
		if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;
		if (!Objects.equals(event.getItem(), this.getItem())) return;

		Player player = event.getPlayer();
		SelectionInventories.getInstance().openInventory(player);
	}
}
