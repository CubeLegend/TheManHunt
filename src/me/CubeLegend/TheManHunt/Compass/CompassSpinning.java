package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class CompassSpinning implements Listener {
	
	private static CompassSpinning instance;

	public static CompassSpinning getInstance() {
		if (instance == null) {
			instance = new CompassSpinning();
		}
		return instance;
	}

	public ArrayList<UUID> spinPlayerCompass = new ArrayList<UUID>();

	public boolean containsSpinPlayerCompass(Player player) {
		return spinPlayerCompass.contains(player.getUniqueId());
	}

	public void addSpinPlayerCompass(Player player) {
		if (!spinPlayerCompass.contains(player.getUniqueId())) {
		spinPlayerCompass.add(player.getUniqueId());
		}
	}
	
	public void removeSpinPlayerCompass(Player player) {
		if (spinPlayerCompass.contains(player.getUniqueId())) {
			spinPlayerCompass.remove(player.getUniqueId());
		}
	}
	
	//Removing player from ArrayList when player leaves ---
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (spinPlayerCompass.contains(event.getPlayer().getUniqueId())) {
			spinPlayerCompass.remove(event.getPlayer().getUniqueId());
		}
	}
	//-----------------------------------------------------
	
	private static int id4 = 4;
	
	private static int counter = 1;
	
	private static final Location loc1 = new Location(null, 1000, 50, 0);
	private static final Location loc2 = new Location(null, 0, 50, 1000);
	private static final Location loc3 = new Location(null, -1000, 50, 0);
	private static final Location loc4 = new Location(null, 0, 50, -1000);
	
	public void startSpinningCompasses() {
		
		id4 = Bukkit.getScheduler().runTaskTimer(TheManHunt.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				for (UUID uuid : spinPlayerCompass) {
					System.out.println(spinPlayerCompass);
					if (Bukkit.getPlayer(uuid) != null) {
						Player player = Bukkit.getPlayer(uuid);
						
						loc1.setWorld(player.getWorld());
						loc2.setWorld(player.getWorld());
						loc3.setWorld(player.getWorld());
						loc4.setWorld(player.getWorld());
						
						if (counter == 1) player.setCompassTarget(player.getLocation().add(loc1));			
						if (counter == 2) player.setCompassTarget(player.getLocation().add(loc2));
						if (counter == 3) player.setCompassTarget(player.getLocation().add(loc3));
						if (counter == 4) player.setCompassTarget(player.getLocation().add(loc4));
					}
				}
				if (counter >= 4) {
					counter = 1;
				} else {
					counter++;
				}
			}
		}, 0, 10).getTaskId();
	}
	
	public void cancelSpinningCompasses() {
		Bukkit.getScheduler().cancelTask(id4);
	}
}
