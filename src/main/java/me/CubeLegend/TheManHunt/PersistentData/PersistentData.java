package me.CubeLegend.TheManHunt.PersistentData;

import org.bukkit.Bukkit;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PersistentData implements Serializable {

    @Serial
    private static final long serialVersionUID = 3570970814172486251L;

    public String deleteWorldOnStartUp;

    public List<UUID> runners;
    public List<UUID> hunters;

    // Can be used for saving
    public PersistentData(
            String deleteWorldOnStartUp,
            List<UUID> runners,
            List<UUID> hunters
    ) {
        this.deleteWorldOnStartUp = deleteWorldOnStartUp;

        this.runners = runners;
        this.hunters = hunters;
    }

    public void saveData(String filePath) {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            Bukkit.getLogger().warning("Could not save data to file");
            e.printStackTrace();
        }
    }

    public static PersistentData loadData(String filePath) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            PersistentData persistentData = (PersistentData) in.readObject();
            in.close();
            return persistentData;
        } catch (ClassNotFoundException | IOException e) {
            Bukkit.getLogger().warning("Could not load data from file");
            e.printStackTrace();
            return null;
        }
    }
}