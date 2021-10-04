package me.CubeLegend.TheManHunt.NMSUtils;

import net.minecraft.server.v1_16_R3.StructureGenerator;
import net.minecraft.server.v1_16_R3.WorldGenFeatureVillageConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MinecraftStructures {

	public static double getDistanceToVillage(World world, Location from) {
		return from.distance(getStructureLocation(world, from, "Village"));
	}
	
	//Locate ---------------------------------------------------------------------
	public static Location getStructureLocation(World world, Location from, String structure) {
		String stringVillageLocation = null;
		try {
			stringVillageLocation = getStructure(from, structure);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | InstantiationException e) {
			e.printStackTrace();
		}
		if (stringVillageLocation == null)
			return null;

		String[] stringVillageLocationArray = stringVillageLocation.split(",");
		
		String stringVillageLocationX = stringVillageLocationArray[0];
		String stringVillageLocationZ = stringVillageLocationArray[2];
		
		stringVillageLocationX = stringVillageLocationX.replaceAll("[^0-9-]", "");
		stringVillageLocationX = stringVillageLocationX.trim();
		stringVillageLocationX = stringVillageLocationX.replaceAll(" ", "");
		
		stringVillageLocationZ = stringVillageLocationZ.replaceAll("[^0-9-]", "");
		stringVillageLocationZ = stringVillageLocationZ.trim();
		stringVillageLocationZ = stringVillageLocationZ.replaceAll(" ", "");
		
		int VillageLocationX = Integer.parseInt(stringVillageLocationX);
		int VillageLocationZ = Integer.parseInt(stringVillageLocationZ);
		Location VillageLocation = new Location(world, VillageLocationX, 50, VillageLocationZ);

		return VillageLocation;
	}
	
    private static String getStructure(Location l, String structure) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException {
		String firstVersionNumber = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[0].replace("v", "");
		String secondVersionNumber = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[1];
		String thirdVersionNumber = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[2].replace("R", "");
		int fullVersionNumber = Integer.parseInt(firstVersionNumber + secondVersionNumber + thirdVersionNumber);
		if (fullVersionNumber < 1160) {
			Method getHandle = l.getWorld().getClass().getMethod("getHandle");
			Object nmsWorld = getHandle.invoke(l.getWorld());
			// method: public BlockPosition a(StructureGenerator<?> structuregenerator, BlockPosition blockposition, int i, boolean flag) WorldSever.class line 1502
			Object blockPositionString = nmsWorld.getClass().getMethod("a", new Class[] { String.class, getNMSClass("BlockPosition"), int.class, boolean.class }).invoke(nmsWorld, structure, getBlockPosition(l), 100, false);
			return blockPositionString.toString();
		}
		if (fullVersionNumber >= 1160) {
			Method getHandle = l.getWorld().getClass().getMethod("getHandle");
			Object nmsWorld = getHandle.invoke(l.getWorld());
			StructureGenerator<WorldGenFeatureVillageConfiguration> StructureGenerator = net.minecraft.server.v1_16_R3.StructureGenerator.VILLAGE;
			Object blockPositionString = nmsWorld.getClass().getMethod("a", new Class[] { getNMSClass("StructureGenerator"), getNMSClass("BlockPosition"), int.class, boolean.class }).invoke(nmsWorld, StructureGenerator, getBlockPosition(l), 100, false);
			return blockPositionString.toString();
		}
		System.out.println("Invalid Version");
		return null;
	}

    private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
         String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
         String name = "net.minecraft.server." + version + nmsClassString;
		 return Class.forName(name);
	}

    private static Object getBlockPosition(Location loc) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
         Class<?> nmsBlockPosition = getNMSClass("BlockPosition");
         Object nmsBlockPositionInstance = nmsBlockPosition
                 .getConstructor(new Class[] { Double.TYPE, Double.TYPE, Double.TYPE })
                 .newInstance(new Object[] { loc.getX(), loc.getY(), loc.getZ() });
         return nmsBlockPositionInstance;
	}
    //----------------------------------------------------------------------------
}
