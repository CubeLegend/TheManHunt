package me.CubeLegend.TheManHunt.TeamSystem;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.LanguageSystem.Language;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

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
            Inventory inventory = Bukkit.createInventory(null,9, "teamName");
            inventory.setItem(2, createGuiItem(Material.BLUE_WOOL, "&6Team &1Runners", "&6Click if you want to join Team &1Runners&6."));
            inventory.setItem(4, createGuiItem(Material.GRAY_WOOL, "&6Team &7Spectators", "&6Click if you want to join Team &7Spectators&6."));
            inventory.setItem(6, createGuiItem(Material.RED_WOOL , "&6Team &4Hunters", "&6Click if you want to join Team &4Hunters&6."));
            selectionInventories.put(language.name, inventory);
        }
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(Player player)
    {
        String language = LanguageManager.getInstance().getPlayerLanguage(player);
        player.openInventory(selectionInventories.get(language));
    }

    //open inventory on interaction with TeamSelector
    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.hasItem() && Objects.equals(event.getItem(), tsi.getTeamSelectionItem()) && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            openInventory(player);
        }
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e)
    {
        if (GameHandler.getInstance().getGameState() != GameState.IDLE) {
            return;
        }

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();
        if (clickedItem.getType() == Material.BLUE_WOOL) {

            //trying to add a runner
            TeamHandler.getInstance().getTeam("Runners").addMember(p);
            p.sendMessage("&6Du wurdest zum Team &1Runners &6hinzugefügt.");
            e.setCancelled(true);
            return;
        }
        if (clickedItem.getType() == Material.RED_WOOL) {

            //trying to add a hunter
            TeamHandler.getInstance().getTeam("Hunters").addMember(p);
            p.sendMessage("&6Du wurdest zum Team &4Hunters &6hinzugefügt.");
            e.setCancelled(true);
            return;
        }
        if (clickedItem.getType() == Material.GRAY_WOOL) {

            //trying to add a spectator
            TeamHandler.getInstance().getTeam("Spectators").addMember(p);
            p.sendMessage("&6Du wurdest zum Team &7Spectators &6hinzugefügt.");
            e.setCancelled(true);
        }
    }
}
