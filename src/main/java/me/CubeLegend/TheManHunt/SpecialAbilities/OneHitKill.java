package me.CubeLegend.TheManHunt.SpecialAbilities;

import me.CubeLegend.TheManHunt.GameModeSystem.GameModeManager;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OneHitKill implements Listener {

    private static OneHitKill instance;

    public static OneHitKill getInstance() {
        if (instance == null) {
            instance = new OneHitKill();
        }
        return instance;
    }

    private final GameModeManager gmm = GameModeManager.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!gmm.getBoolean("Abilities.Hunter.OneHitKill")) return;
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!(damager instanceof Player damagerPlayer)) return;
        if (!(entity instanceof Player player)) return;
        if (!TeamHandler.getInstance().getTeam("Hunters").checkForMember(damagerPlayer)) return;
        if (!TeamHandler.getInstance().getTeam("Runners").checkForMember(player)) return;
        event.setDamage(999999);
    }
}
