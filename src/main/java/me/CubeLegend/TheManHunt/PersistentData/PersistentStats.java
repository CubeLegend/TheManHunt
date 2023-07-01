package me.CubeLegend.TheManHunt.PersistentData;

import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PersistentStats implements Serializable {

    @Serial
    private static final long serialVersionUID = -839975967040811882L;

    public int allRunnerWins;
    public int allHunterWins;

    // Can be used for saving
    public PersistentStats(
            int allRunnerWins,
            int allHunterWins
    ) {
        this.allRunnerWins = allRunnerWins;
        this.allHunterWins = allHunterWins;
    }

    public void saveData(String filePath) {
        try {
            // convert fields and values to csv format
            StringBuilder stringBuilder = new StringBuilder();
            for (Field field : this.getClass().getFields()) {
                stringBuilder.append(field.getName())
                        .append(",")
                        .append(field.get(this).toString())
                        .append("\n");
            }

            byte[] data = stringBuilder.toString().getBytes();
            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(filePath));
            out.write(data);
            out.close();
        } catch (IOException e) {
            Bukkit.getLogger().warning("Could not save data to file");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static PersistentStats loadData(String filePath) {
        try {
            GZIPInputStream in = new GZIPInputStream(new FileInputStream(filePath));
            String[] data = new String(in.readAllBytes()).split("\n");
            in.close();

            HashMap<String, String> mappedData = new HashMap<>();
            for (String s : data) {
                String[] keyValuePair = s.split(",");
                if (keyValuePair.length != 2) throw new IOException("Malformed data for stats");
                mappedData.put(keyValuePair[0], keyValuePair[1]);
            }

            // get constructor arguments
            int allRunnerWins = 0;
            int allHunterWins = 0;
            for (String key : mappedData.keySet()) {
                switch (key) {
                    case "allRunnerWins" -> allRunnerWins = Integer.parseInt(mappedData.get(key));
                    case "allHunterWins" -> allHunterWins = Integer.parseInt(mappedData.get(key));
                }
            }

            return new PersistentStats(allRunnerWins, allHunterWins);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Could not load data to file");
            e.printStackTrace();
            return null;
        }
    }
}