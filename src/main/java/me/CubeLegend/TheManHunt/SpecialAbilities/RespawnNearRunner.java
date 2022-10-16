package me.CubeLegend.TheManHunt.SpecialAbilities;

import me.CubeLegend.TheManHunt.Settings;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RespawnNearRunner implements Listener {

    @EventHandler
    public void onPlayerRespawn (PlayerRespawnEvent event) {
        if (!TeamHandler.getInstance().getTeam("Hunters").checkForMember(event.getPlayer())) {
            return;
        }
        List<Player> runners = TeamHandler.getInstance().getTeam("Runners").getMembers();

        int distance = Settings.getInstance().RespawnDistanceNearRunner;
        if (distance <= 0) return;
        int resolution = Settings.getInstance().RespawnNearRunnerResolution;

        List<Location> locations = getSpawnLocations(runners, distance, resolution);
        Random random = new Random();
        event.setRespawnLocation(Bukkit.getWorlds().get(0).getHighestBlockAt(locations.get(random.nextInt(locations.size()))).getLocation());
    }

    private List<Location> getSpawnLocations(List<Player> runners, int distance, int resolution) {
        HashMap<Player, List<Vector>> pointsAroundRunners = new HashMap<>();
        HashMap<Player, Vector> runnerLocations = new HashMap<>();
        for (Player runner : runners) {
            Vector playerLoc = runner.getLocation().toVector();
            playerLoc.setY(1);
            if (runner.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                playerLoc.multiply(new Vector(8, 1, 8));
            }
            runnerLocations.put(runner, playerLoc);
            List<Vector> pointsAroundRunner = getPointsAroundVector(playerLoc.toLocation(runner.getWorld()), distance, resolution);

            if (!pointsAroundRunner.isEmpty()) pointsAroundRunners.put(runner, pointsAroundRunner);
        }

        HashMap<Player, List<Vector>> pointsAroundOthers = new HashMap<>(pointsAroundRunners);
        HashMap<Player, Vector> othersLocations = new HashMap<>(runnerLocations);

        for (Player runner : pointsAroundRunners.keySet()) {
            List<Vector> originalVectors = pointsAroundOthers.get(runner);
            Vector originalLocation = othersLocations.get(runner);

            pointsAroundOthers.remove(runner);
            othersLocations.remove(runner);

            List<Vector> pointsAroundRunner = pointsAroundRunners.get(runner);
            List<Vector> result = removeOverlappingPoints(
                    runnerLocations.get(runner),
                    pointsAroundRunner,
                    othersLocations,
                    pointsAroundOthers,
                    distance
            );
            pointsAroundRunners.put(runner, result);

            othersLocations.put(runner,originalLocation);
            pointsAroundOthers.put(runner, originalVectors);
        }

        // convert vectors to locations
        List<Location> locations = new ArrayList<>();
        for (List<Vector> listVector : pointsAroundRunners.values()) {
            for (Vector v : listVector) {
                locations.add(v.toLocation(Bukkit.getWorlds().get(0)));
            }
        }
        return locations;
    }

    private List<Vector> getPointsAroundVector(Location loc, int distance, int resolution) {
        List<Vector> pointsAroundPlayer = new ArrayList<>();
        for (int i = 1; i <= resolution; i++) {
            double angle = Math.PI * 2 / resolution * i;
            Vector vecToCircle = new Vector(distance * Math.cos(angle), 0, distance * Math.sin(angle));

            pointsAroundPlayer.add(loc.clone().add(vecToCircle).toVector());
        }
        return pointsAroundPlayer;
    }

    private List<Vector> removeOverlappingPoints(
            Vector playerOrigin,
            List<Vector> playerPoints,
            HashMap<Player, Vector> othersOrigin,
            HashMap<Player, List<Vector>> othersPoints,
            int distance
    ) {
        for (Player other : othersPoints.keySet()) {
            Vector otherOrigin = othersOrigin.get(other);
            if (playerOrigin.distance(otherOrigin) >= distance) continue;

            List<Vector> currentOPPoints = othersPoints.get(other);
            List<Vector> overlappingPPoints = new ArrayList<>();
            for (Vector pPoint : playerPoints) {
                for (Vector oPoint : currentOPPoints) {
                    if (playerOrigin.distanceSquared(pPoint) < playerOrigin.distanceSquared(oPoint) &&
                            otherOrigin.distanceSquared(pPoint) < otherOrigin.distanceSquared(oPoint)) {
                        overlappingPPoints.add(pPoint);
                    }
                }
            }
            playerPoints.removeAll(overlappingPPoints);
        }
        return playerPoints;
    }
}
