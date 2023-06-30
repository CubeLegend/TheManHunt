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

    Tracker(String name, Material material, Team team, String ability) {
        super(name, material);
        owners = team;
        this.ability = ability;
    }

    private final Configuration config = Configuration.getInstance();
    private final Team owners;
    private final String ability;

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getChangeFrom() != GameState.IDLE) return;
        if (event.getChangeTo() == GameState.RUNAWAYTIME || event.getChangeTo() == GameState.PLAYING) {
            if (config.getBoolean(ability)) {
                List<Player> members = owners.getMembers();
                members.stream().filter(Objects::nonNull).forEach(this::giveToPlayer);
            }
        }
    }
}
