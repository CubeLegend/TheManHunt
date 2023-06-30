package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.Configuration;
import me.CubeLegend.TheManHunt.CustomItem;
import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.StateSystem.GameStateChangeEvent;
import me.CubeLegend.TheManHunt.TeamSystem.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public class Tracker extends CustomItem implements Listener {

    Tracker(String name, Material material, Team team, String abilityName) {
        super(name, material);
        owners = team;
        this.abilityName = abilityName;
        this.disabledAbilityName = null;
    }

    Tracker(String name, Material material, Team team, String abilityName, String disabledAbilityName) {
        super(name, material);
        owners = team;
        this.abilityName = abilityName;
        this.disabledAbilityName = disabledAbilityName;
    }

    private final Configuration config = Configuration.getInstance();
    private final Team owners;
    private final String abilityName;
    private final String disabledAbilityName;

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getChangeFrom() != GameState.IDLE) return;
        if (event.getChangeTo() != GameState.RUNAWAYTIME && event.getChangeTo() != GameState.PLAYING) return;
        if (!config.getBoolean(abilityName)) return;
        if (disabledAbilityName != null && config.getBoolean(disabledAbilityName)) return;
        List<Player> members = owners.getMembers();
        members.stream().filter(Objects::nonNull).forEach(this::giveToPlayer);
    }
}
