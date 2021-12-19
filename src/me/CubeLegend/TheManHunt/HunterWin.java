package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityStatus;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public class HunterWin implements Listener {

    private final ArrayList<UUID> deadRunners = new ArrayList<>();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        System.out.println("PlayerDeathEvent");
        Player player = event.getEntity();
        if (TeamHandler.getInstance().getTeam("Runners").checkForMember(player)) {
            deadRunners.add(player.getUniqueId());
            if (deadRunners.size() == TeamHandler.getInstance().getTeam("Runners").getMemberCount()) {
                hunterWinGame();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (!(event.getDamager() instanceof Player)) {
            event.setCancelled(true);

            PacketPlayOutEntityStatus DeathAnim = new PacketPlayOutEntityStatus();
            byte myByte = 3; // 2 means hurt animation, 3 means death animation
            setValue(DeathAnim, "a", player.getEntityId());
            setValue(DeathAnim, "b", myByte);
            for (Player p : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer)p).getHandle().playerConnection.sendPacket(DeathAnim);
            }
            Bukkit.getLogger().info("Write>> " + DeathAnim.toString() + ": " + getValue(DeathAnim, "a") + ", " + getValue(DeathAnim, "b"));

            player.setGameMode(GameMode.SPECTATOR);
            if (deadRunners.size() == TeamHandler.getInstance().getTeam("Runners").getMemberCount()) {
                hunterWinGame();
            }
            return;
        }
        //Player damager = (Player) event.getDamager();

        if (GameHandler.getInstance().getGameState() == GameState.IDLE) {
            event.setCancelled(true);
            return;
        }

        /*if (Freeze.getInstance().containsFreezedPlayers((damager))) {
            event.setCancelled(true);
        } else {*/
            if (TeamHandler.getInstance().getTeam("Runners").checkForMember(player)) {
                if ((player.getHealth() - event.getFinalDamage()) <= 0) {
                    deadRunners.add(player.getUniqueId());
                    event.setCancelled(true);

                    PacketPlayOutEntityStatus DeathAnim = new PacketPlayOutEntityStatus();
                    byte myByte = 2;
                    setValue(DeathAnim, "a", player.getEntityId());
                    setValue(DeathAnim, "b", myByte);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(DeathAnim);
                    }
                    Bukkit.getLogger().info("Write>> " + DeathAnim.toString() + ": " + getValue(DeathAnim, "a") + ", " + getValue(DeathAnim, "b"));

                    player.setGameMode(GameMode.SPECTATOR);
                    if (deadRunners.size() == TeamHandler.getInstance().getTeam("Runners").getMemberCount()) {
                        hunterWinGame();
                    }
                }
            }
        //}
    }

    public void hunterWinGame() {
        GameHandler.getInstance().setGameState(GameState.END);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("§6Die §4Hunter §6haben Gewonnen!!!", null, 10, 70, 20);
        }
        GameHandler.getInstance().connectPlayersToLobby();
    }

    private void setValue(Object obj,String name,Object value){
        try{
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        }catch(Exception ignored){}
    }

    private Object getValue(Object obj,String name){
        try{
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        }catch(Exception ignored){}
        return null;
    }
}
