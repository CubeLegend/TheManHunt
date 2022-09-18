package me.CubeLegend.TheManHunt.TeamSystem;

import me.CubeLegend.TheManHunt.StateSystem.GameHandler;
import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.LanguageSystem.Language;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.LanguageSystem.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;

public class SelectionInventories implements Listener {

    private static SelectionInventories instance;

    public static SelectionInventories getInstance() {
        if (instance == null) {
            instance = new SelectionInventories();
        }
        return instance;
    }

    private final HashMap<String, Inventory> selectionInventories = new HashMap<>();

    private final TeamSelectionItem tsi = new TeamSelectionItem();

    SelectionInventories() {
        for (Language language : LanguageManager.getInstance().getLanguages()) {

            //insert the Strings from the language file
            Inventory inventory = Bukkit.createInventory(
                    null,
                    9,
                    language.getMessage(Message.TEAM_SELECTION_TITLE, new String[0])
            );

            String runnersName = language.getMessage(Message.RUNNERS_DISPLAY_NAME, new String[0]);
            inventory.setItem(2, createGuiItem(
                    Material.BLUE_WOOL,
                    runnersName,
                    language.getMessage(Message.JOIN_TEAM_BUTTON, new String[] {runnersName})
            ));

            String spectatorsName = language.getMessage(Message.SPECTATORS_DISPLAY_NAME, new String[0]);
            inventory.setItem(4, createGuiItem(
                    Material.GRAY_WOOL,
                    spectatorsName,
                    language.getMessage(Message.JOIN_TEAM_BUTTON, new String[] {spectatorsName})
            ));

            String huntersName = language.getMessage(Message.HUNTERS_DISPLAY_NAME, new String[0]);
            inventory.setItem(6, createGuiItem(
                    Material.RED_WOOL ,
                    huntersName,
                    language.getMessage(Message.JOIN_TEAM_BUTTON, new String[] {huntersName})
            ));

            selectionInventories.put(language.name, inventory);
        }
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        assert meta != null;

        // Set the name of the item
        meta.setDisplayName(name);
        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public void openInventory(Player player) {
        String language = LanguageManager.getInstance().getPlayerLanguage(player);
        player.openInventory(selectionInventories.get(language));
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory selection = selectionInventories.get(LanguageManager.getInstance().getPlayerLanguage(player));
        if (event.getInventory().equals(selection)) event.setCancelled(true);

        if (GameHandler.getInstance().getGameState() != GameState.IDLE) return;

        ItemStack clickedItem = event.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        switch (clickedItem.getType()) {
            case BLUE_WOOL -> {
                //trying to add a runner
                Team runners = TeamHandler.getInstance().getTeam("Runners");
                runners.addMember(player);
                LanguageManager.getInstance().sendMessage(player, Message.YOU_JOINED_TEAM, new String[] {runners.getTeamName()});
            }
            case RED_WOOL -> {
                //trying to add a hunter
                Team hunters = TeamHandler.getInstance().getTeam("Hunters");
                hunters.addMember(player);
                LanguageManager.getInstance().sendMessage(player, Message.YOU_JOINED_TEAM, new String[] {hunters.getTeamName()});
            }
            case  GRAY_WOOL -> {
                //trying to add a spectator
                Team spectators = TeamHandler.getInstance().getTeam("Spectators");
                spectators.addMember(player);
                LanguageManager.getInstance().sendMessage(player, Message.YOU_JOINED_TEAM, new String[] {spectators.getTeamName()});
            }
        }
    }
}
