package me.CubeLegend.TheManHunt.TeamSystem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamSelectionItem {
	
	public ItemStack getTeamSelectionItem() {
		ItemStack paper = new ItemStack(Material.PAPER, 1);
		
	    ItemMeta meta = paper.getItemMeta();
		assert meta != null;
		meta.setDisplayName("Team Selector");
	    meta.setUnbreakable(true);
	    paper.setItemMeta(meta);
	    
	    return paper;
	}
}
