package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.CustomItem;
import me.CubeLegend.TheManHunt.GameModeSystem.GameModeManager;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.LanguageSystem.Message;
import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.StateSystem.GameStateChangeEvent;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.List;
import java.util.Objects;

public class FortressTracker extends CustomItem {

    private static FortressTracker instance;

    public static FortressTracker getInstance() {
        if (instance == null) {
            instance = new FortressTracker();
        }
        return instance;
    }

    public FortressTracker() {
        super("Fortress Tracker", Material.COMPASS);
    }

    private int TaskId = 0;

    private final GameModeManager gmm = GameModeManager.getInstance();

    public void startFortressTrackingRoutine(int period) {
        TaskId = Bukkit.getServer().getScheduler().runTaskTimer(TheManHunt.getInstance(), () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getInventory().contains(this.getItem())) {
                    if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                        Location fortressLocation = player.getWorld().locateNearestStructure(
                                player.getLocation(),
                                StructureType.NETHER_FORTRESS,
                                100,
                                false);
                        if (fortressLocation != null) {
                            setLodestone(player, fortressLocation);
                        }
                    }
                }
            }
        }, 0, period).getTaskId();
    }

    public void stopFortressTrackingRoutine() {
        if (Bukkit.getScheduler().isCurrentlyRunning(TaskId)) {
            Bukkit.getScheduler().cancelTask(TaskId);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.hasItem()
                && Objects.requireNonNull(event.getItem()).getType() == Material.COMPASS
                && Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName().equals(Objects.requireNonNull(this.getItem().getItemMeta()).getDisplayName())
                && event.getItem().getItemMeta().isUnbreakable() == this.getItem().getItemMeta().isUnbreakable()
                && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {

            if (event.getClickedBlock() != null) {
                if (Objects.requireNonNull(event.getClickedBlock()).getType() == Material.LODESTONE) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (player.getWorld().getEnvironment() != World.Environment.NETHER) {
                LanguageManager.getInstance().sendMessage(player, Message.ERROR_USE_ONLY_IN_NETHER, new String[0]);
            } else {
                Location fortressLocation = player.getWorld().locateNearestStructure(
                        player.getLocation(),
                        StructureType.NETHER_FORTRESS,
                        100,
                        false);
                assert fortressLocation != null;
                fortressLocation.setY(player.getLocation().getY());
                fortressLocation.add(0.5, 0, 0.5);
                double fortressDistance = player.getLocation().distance(fortressLocation);
                int intFortressDistance = (int) Math.round(fortressDistance);
                LanguageManager.getInstance().sendMessage(player, Message.NEXT_Fortress_X_BLOCKS_AWAY, new String[] {String.valueOf(intFortressDistance)});
            }
        }
    }

    void setLodestone(Player player, Location target) {
        ItemStack fortressTracker = null;
        for (ItemStack is : player.getInventory()) {
            if (this.getItem().equals(is)) {
                fortressTracker =  is;
                break;
            }
        }
        if (fortressTracker != null) {
            CompassMeta cm = (CompassMeta) fortressTracker.getItemMeta();
            assert cm != null;
            cm.setLodestone(target);
            fortressTracker.setItemMeta(cm);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return;
        if (!player.getInventory().contains(VillageTracker.getInstance().getItem())) return;
        int trackerSlot = player.getInventory().first(VillageTracker.getInstance().getItem());
        player.getInventory().setItem(trackerSlot, this.getItem());
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getChangeFrom() != GameState.IDLE) return;
        if (event.getChangeTo() == GameState.RUNAWAYTIME || event.getChangeTo() == GameState.PLAYING) {
            if (gmm.getBoolean("Runner.FortressTracker") && !gmm.getBoolean("Runner.VillageTracker")) {
                List<Player> runners = TeamHandler.getInstance().getTeam("Runners").getMembers();
                for (Player runner : runners) {
                    VillageTracker.getInstance().giveToPlayer(runner);
                }
            }
        }
    }
}
