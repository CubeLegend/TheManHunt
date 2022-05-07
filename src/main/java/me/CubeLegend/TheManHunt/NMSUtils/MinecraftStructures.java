package me.CubeLegend.TheManHunt.NMSUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class MinecraftStructures {

	private static int versionNumber;

	//Locate ---------------------------------------------------------------------
	public static Location getStructureLocation(Location from, String structure) {
		String firstVersionNumber = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[0].replace("v", "");
		String secondVersionNumber = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[1];
		String thirdVersionNumber = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[2].replace("R", "");
		versionNumber = Integer.parseInt(firstVersionNumber + secondVersionNumber + thirdVersionNumber);

		if (versionNumber >= 1131) {
			try {
				return getStructureFromApi(from, structure);
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		String stringVillageLocation = null;
		try {
			stringVillageLocation = getStructure(from, structure);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		if (stringVillageLocation == null)
			return null;

		String[] stringVillageLocationArray = stringVillageLocation.split(",");
		
		String stringVillageLocationX = stringVillageLocationArray[0];
		String stringVillageLocationY = stringVillageLocationArray[1];
		String stringVillageLocationZ = stringVillageLocationArray[2];

		stringVillageLocationX = stringVillageLocationX.replaceAll("[^0-9-]", "");
		stringVillageLocationX = stringVillageLocationX.trim();
		stringVillageLocationX = stringVillageLocationX.replaceAll(" ", "");

		stringVillageLocationY = stringVillageLocationY.replaceAll("[^0-9-]", "");
		stringVillageLocationY = stringVillageLocationY.trim();
		stringVillageLocationY = stringVillageLocationY.replaceAll(" ", "");
		
		stringVillageLocationZ = stringVillageLocationZ.replaceAll("[^0-9-]", "");
		stringVillageLocationZ = stringVillageLocationZ.trim();
		stringVillageLocationZ = stringVillageLocationZ.replaceAll(" ", "");
		
		int VillageLocationX = Integer.parseInt(stringVillageLocationX);
		int VillageLocationY = Integer.parseInt(stringVillageLocationY);
		int VillageLocationZ = Integer.parseInt(stringVillageLocationZ);
		Location VillageLocation = new Location(from.getWorld(), VillageLocationX, VillageLocationY, VillageLocationZ);

		return VillageLocation;
	}

	private static Location getStructureFromApi(Location l, String structure) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		World world = l.getWorld();

		Class<?> structureTypeClass = Class.forName("org.bukkit.StructureType");
		Map<String, Object> map = (Map<String, Object>) structureTypeClass.getMethod("getStructureTypes").invoke(structureTypeClass);
		Object structureType = map.get(structure.toLowerCase());

		Method locateNearestStructure = world.getClass().getMethod( "locateNearestStructure",
				Location.class,
				structureTypeClass,
				int.class,
				boolean.class);
		return (Location) locateNearestStructure.invoke(world, l, structureType, 100, false);
	}
	
    private static String getStructure(Location l, String structure) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, NoSuchFieldException {
		String firstVersionNumber = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[0].replace("v", "");
		String secondVersionNumber = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[1];
		String thirdVersionNumber = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[2].replace("R", "");
		int fullVersionNumber = Integer.parseInt(firstVersionNumber + secondVersionNumber + thirdVersionNumber);

		if (versionNumber <= 1130) {
			ChunkGenerator chunkGenerator = l.getWorld().getGenerator();
			System.out.println(chunkGenerator.getClass().getMethods());
			//TODO add nms for 1130 version
		}

		if (fullVersionNumber < 1160) {
			Method getHandle = l.getWorld().getClass().getMethod("getHandle");
			Object nmsWorld = getHandle.invoke(l.getWorld());
			// method: public BlockPosition a(StructureGenerator<?> structuregenerator, BlockPosition blockposition, int i, boolean flag) WorldSever.class line 1502
			Object blockPositionString = nmsWorld.getClass().getMethod("a", new Class[] { String.class, getNMSClass("BlockPosition"), int.class, boolean.class }).invoke(nmsWorld, structure, getBlockPosition(l), 100, false);
			return blockPositionString.toString();
		}
		if (fullVersionNumber < 1170) {
			Method getHandle = Objects.requireNonNull(l.getWorld()).getClass().getMethod("getHandle");
			Object nmsWorld = getHandle.invoke(l.getWorld());
			Class<?> structureGeneratorClass = getNMSClass("StructureGenerator");
			Field field = structureGeneratorClass.getField(structure.toUpperCase());
			Object WorldGen = field.get(null);
			Object blockPositionString = nmsWorld.getClass().getMethod("a", new Class[] { getNMSClass("StructureGenerator"), getNMSClass("BlockPosition"), int.class, boolean.class })
					.invoke(nmsWorld, WorldGen, getBlockPosition(l), 100, false);
			return blockPositionString.toString();
		}
		if (fullVersionNumber >= 1180) {
			Method getHandle = Objects.requireNonNull(l.getWorld()).getClass().getMethod("getHandle");
			Object nmsWorld = getHandle.invoke(l.getWorld());
			Class<?> structureGeneratorClass = Class.forName("net.minecraft.world.level.levelgen.feature.StructureGenerator");
			Field field = null;
			if (structure.equalsIgnoreCase("VILLAGE")) {
				field = structureGeneratorClass.getField("r");
			} 
			assert field != null;
			Object WorldGen = field.get(null);
			Object blockPositionString = nmsWorld.getClass().getMethod("a", Class.forName("net.minecraft.world.level.levelgen.feature.StructureGenerator"), Class.forName("net.minecraft.core.BlockPosition"), int.class, boolean.class)
					.invoke(nmsWorld, WorldGen, getBlockPositionNew(l), 100, false);
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

    private static Object getBlockPosition(Location loc) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
         Class<?> nmsBlockPosition = getNMSClass("BlockPosition");
         Object nmsBlockPositionInstance = nmsBlockPosition
                 .getConstructor(new Class[] { Double.TYPE, Double.TYPE, Double.TYPE })
                 .newInstance(new Object[] { loc.getX(), loc.getY(), loc.getZ() });
         return nmsBlockPositionInstance;
	}

	private static Object getBlockPositionNew(Location loc) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Class<?> nmsBlockPosition = Class.forName("net.minecraft.core.BlockPosition");
		return nmsBlockPosition.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE).newInstance(loc.getX(), loc.getY(), loc.getZ());
	}
    //----------------------------------------------------------------------------
}
