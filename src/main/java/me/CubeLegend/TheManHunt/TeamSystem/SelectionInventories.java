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

    private final Material runnersIcon = Material.valueOf(TeamHandler.getInstance().getTeam("Runners").getTeamIcon());
    private final Material spectatorIcon = Material.valueOf(TeamHandler.getInstance().getTeam("Spectators").getTeamIcon());
    private final Material huntersIcon = Material.valueOf(TeamHandler.getInstance().getTeam("Hunters").getTeamIcon());

    SelectionInventories() {
        for (Language language : LanguageManager.getInstance().getLanguages()) {

            //insert the Strings from the language file
            Inventory inventory = Bukkit.createInventory(
                    null,
                    9,
                    language.getMessage(Message.TEAM_SELECTION_TITLE, new String[0])
            );

            String runnersName = language.getMessage(Message.RUNNERS_DISPLAY_NAME, new String[0]);
            inventory.setItem(TeamHandler.getInstance().getTeam("Runners").getTeamSelectionSlot(), createGuiItem(
                    runnersIcon,
                    runnersName,
                    language.getMessage(Message.JOIN_TEAM_BUTTON, new String[] {runnersName})
            ));

            String spectatorsName = language.getMessage(Message.SPECTATORS_DISPLAY_NAME, new String[0]);
            inventory.setItem(TeamHandler.getInstance().getTeam("Spectators").getTeamSelectionSlot(), createGuiItem(
                    spectatorIcon,
                    spectatorsName,
                    language.getMessage(Message.JOIN_TEAM_BUTTON, new String[] {spectatorsName})
            ));

            String huntersName = language.getMessage(Message.HUNTERS_DISPLAY_NAME, new String[0]);
            inventory.setItem(TeamHandler.getInstance().getTeam("Hunters").getTeamSelectionSlot(), createGuiItem(
                    huntersIcon ,
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

        Material clickedType = clickedItem.getType();
        if (clickedType.equals(runnersIcon)) {
            //trying to add a runner
            Team runners = TeamHandler.getInstance().getTeam("Runners");
            runners.addMember(player);
            LanguageManager.getInstance().sendMessage(player, Message.YOU_JOINED_TEAM, new String[] {runners.getTeamName()});
        }
        else if (clickedType.equals(huntersIcon)) {
            //trying to add a hunter
            Team hunters = TeamHandler.getInstance().getTeam("Hunters");
            hunters.addMember(player);
            LanguageManager.getInstance().sendMessage(player, Message.YOU_JOINED_TEAM, new String[] {hunters.getTeamName()});
        }
        else if (clickedType.equals(spectatorIcon)) {
            //trying to add a spectator
            Team spectators = TeamHandler.getInstance().getTeam("Spectators");
            spectators.addMember(player);
            LanguageManager.getInstance().sendMessage(player, Message.YOU_JOINED_TEAM, new String[] {spectators.getTeamName()});
        }
    }
}
