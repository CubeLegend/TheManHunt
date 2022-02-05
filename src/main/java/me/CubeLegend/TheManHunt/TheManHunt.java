package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.Commands.CommandJoin;
import me.CubeLegend.TheManHunt.Commands.CommandStart;
import me.CubeLegend.TheManHunt.Compass.RunnerTracker;
import me.CubeLegend.TheManHunt.Compass.VillageTracker;
import me.CubeLegend.TheManHunt.SpecialAbilities.FreezeVision;
import me.CubeLegend.TheManHunt.SpecialAbilities.HunterNearWarning;
import me.CubeLegend.TheManHunt.TeamSystem.Team;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class TheManHunt extends JavaPlugin {

    private static TheManHunt instance;

    public static TheManHunt getInstance() {
        return instance;
    }

    /*@Override
    public void onLoad() {
//        List<File> worldFiles = new ArrayList<>();
//        for (World world : Bukkit.getWorlds()) {
//            worldFiles.add(world.getWorldFolder().getAbsoluteFile());
//            System.out.println(Bukkit.getServer().unloadWorld(world, false));
//        }
        FileInputStream in = null;
        try {
            // Initially empty.
            Properties properties = new Properties();

            // You can read files using FileInputStream or FileReader.
            in = new FileInputStream(filename);

            // This line reads the properties file.
            properties.load(in);

            // Your code goes here.
        } catch (FileNotFoundException ex) {
            // Handle missing properties file errors.
        } catch (IOException ex) {
            // Handle IO errors.
        } finally {
            // Need to do some work to close the stream.
            try {
                if (in != null) in.close();
            } catch (IOException ex) {
                // Handle IO errors or log a warning.
            }
        }
        List<File> worldFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(Bukkit.getWorldContainer().getAbsoluteFile().listFiles())));
        for (File file : worldFiles) {
            System.out.println(file);
            System.out.println(file.delete());
        }
    }*/

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        Settings.getInstance().loadSettingsFromConfig();
        //FileConfiguration config = this.getConfig();
        registerCommands();
        registerListeners();
        registerPluginMessagingChannels();
        //config.options().copyDefaults(true);
        //this.saveConfig();

        createTeams();
        startRoutines();
        GameHandler.getInstance().setGameState(GameState.IDLE);
    }

    @Override
    public void onDisable() {
        unregisterPluginMessagingChannels();
        FreezeVision.getInstance().stopFreezeVisionRoutine();
        VillageTracker.getInstance().stopVillageTrackingRoutine();
        RunnerTracker.getInstance().stopRunnerTrackerRoutine();
        HunterNearWarning.getInstance().stopRoutine();
    }

    private void createTeams() {
        TeamHandler.getInstance().createTeam("Runners", "diamond_shovel", 3, "1");
        TeamHandler.getInstance().createTeam("Hunters", "diamond_sword", 6, "4");
    }

    private void startRoutines() {
        if (Settings.getInstance().FreezeVision) {
            FreezeVision.getInstance().startFreezeVisionRoutine(Settings.getInstance().FreezeVisionUpdatePeriod);
        }
        if (Settings.getInstance().VillageTracker) {
            VillageTracker.getInstance().startVillageTrackingRoutine(Settings.getInstance().VillageTrackerUpdatePeriod);
        }
        if (Settings.getInstance().RunnerTracker) {
            RunnerTracker.getInstance().startRunnerTrackerRoutine(Settings.getInstance().RunnerTrackerUpdatePeriod);
        }
        if (Settings.getInstance().HunterNearWarning) {
            HunterNearWarning.getInstance().startRoutine(Settings.getInstance().HunterNearWarningUpdatePeriod, Settings.getInstance().HunterNearWarningRadius);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("membersof")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Team team = TeamHandler.getInstance().getTeam(args[0]);
                if (team != null) {
                    player.sendMessage("§6Members of Team " + team.getTeamName() + ":");
                    for (Player current : Bukkit.getOnlinePlayers()) {
                        if (TeamHandler.getInstance().getTeam(args[0]).checkForMember(current)) {
                            player.sendMessage(current.getDisplayName());
                        }
                    }
                    return true;
                }
            }
        }

        if (label.equalsIgnoreCase("dev")) {
            if (args[0].equalsIgnoreCase("giveVillageTracker")) {
                if (sender instanceof Player) {
                    VillageTracker.getInstance().givePlayerVillageTracker(((Player) sender));
                }
            }
            if (args[0].equalsIgnoreCase("giveRunnerTracker")) {
                if (sender instanceof Player) {
                    RunnerTracker.getInstance().givePlayerRunnerTracker(((Player) sender));
                }
            }
            if (args[0].equalsIgnoreCase("removeMember")) {
                TeamHandler.getInstance().getTeam(args[1]).removeMember(Objects.requireNonNull(Bukkit.getPlayer(args[2])));
            }
            if (args[0].equalsIgnoreCase("printFields")) {
                try {
                    for (Field f : getNMSClass("StructureGenerator").getDeclaredFields()) {
                        System.out.println(f.getName());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (args[0].equalsIgnoreCase("printField")) {
                try {
                    Class<?> StructureGenerator = getNMSClass("StructureGenerator");
                    Field village = StructureGenerator.getField("VILLAGE");
                    System.out.println(village.getName());
                    System.out.println(village.getType());
                    Constructor<?> newSG = StructureGenerator.getConstructor(StructureGenerator, village.getType());
                } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            if (args[0].equalsIgnoreCase("getMeta")) {
                if (sender instanceof Player) {
                    ((Player) sender).sendMessage(Objects.requireNonNull(((Player) sender).getInventory().getItemInMainHand().getItemMeta()).toString());
                }
            }
        }
        /*
        if (label.equalsIgnoreCase("vectorof")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                return true;
            }
        }
        if (label.equalsIgnoreCase("crossproduct")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Entity nearestEntity = null;
                for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
                    if (nearestEntity == null) {
                        nearestEntity = entity;
                    } else if (entity.getLocation().distance(player.getLocation()) < nearestEntity.getLocation().distance(player.getLocation())) {
                        nearestEntity = entity;
                    }
                }
                assert nearestEntity != null;
                //rc.getCrossProduct(player, nearestEntity);
                System.out.println(player.getLocation().add(new Location(player.getWorld(), 0, player.getEyeHeight(), 0)));
                System.out.println(nearestEntity.getLocation().add(new Vector(0, 1, 0)).toVector().subtract(player.getLocation().add(new Vector(0, player.getEyeHeight(), 0)).toVector()));
                player.sendMessage(player.getLocation().add(new Location(player.getWorld(), 0, player.getEyeHeight(), 0)).getDirection().crossProduct(nearestEntity.getLocation().add(new Vector(0, 1, 0)).toVector().subtract(player.getLocation().add(new Vector(0, player.getEyeHeight(), 0)).toVector())).lengthSquared() + " < threshold: 1");
                return true;
            }
        }
        if (label.equalsIgnoreCase("dotproduct")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Entity nearestEntity = null;
                for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
                    if (nearestEntity == null) {
                        nearestEntity = entity;
                    } else if (entity.getLocation().distance(player.getLocation()) < nearestEntity.getLocation().distance(player.getLocation())) {
                        nearestEntity = entity;
                    }
                }
                assert nearestEntity != null;
                //rc.getDotProduct(player, nearestEntity);
                player.sendMessage(String.valueOf(nearestEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().dot(player.getLocation().getDirection().normalize())) + " >= 0");
                return true;
            }
        }
        if (label.equalsIgnoreCase("showLine")) {
            Freeze.getInstance().addPlayersToVision("Runners");

            /*
            if (sender instanceof Player) {
                Player player = (Player) sender;
                RayCast rc = new RayCast();
                Entity nearestEntity = null;
                for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
                    if (nearestEntity == null) {
                        nearestEntity = entity;
                    } else if (entity.getLocation().distance(player.getLocation()) < nearestEntity.getLocation().distance(player.getLocation())) {
                        nearestEntity = entity;
                    }
                }
                assert nearestEntity != null;
                rc.showLineToTarget(player, nearestEntity);
                return true;
            }
             //*
        }*/
        return false;
    }

    private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = "net.minecraft.server." + version + nmsClassString;
        return Class.forName(name);
    }

    private void registerCommands() {
        this.getCommand("Start").setExecutor(new CommandStart());
        this.getCommand("Join").setExecutor(new CommandJoin());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(VillageTracker.getInstance(), this);
        pm.registerEvents(RunnerTracker.getInstance(), this);
        pm.registerEvents(Freeze.getInstance(), this);
        pm.registerEvents(new RunnerWin(), this);
        pm.registerEvents(new PlayerDeathHandler(), this);
    }

    private void registerPluginMessagingChannels() {
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, Objects.requireNonNull(this.getConfig().getString("PluginMessagingChannelOfMiniGame")), new MessageListener());
    }

    private void unregisterPluginMessagingChannels() {
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, Objects.requireNonNull(this.getConfig().getString("PluginMessagingChannelOfMiniGame")));
    }

}