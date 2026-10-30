package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetheriteListenerTest {

    private FirstPlugin plugin;
    private PluginConfig pluginConfig;
    

    //init plugin
    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
        pluginConfig = new PluginConfig(plugin);
    }

    //unload plugin
    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }


    //tests if netherite gear can be smithed when netheriteSmithing is allowed and disallowed within the config
    @Test
    void netheriteSmithingEventsAreBlockedWhenDisabled() {
        
        //test setup
        Player player = MockBukkit.getMock().addPlayer();
        Inventory smithingInventory = Bukkit.createInventory(player, InventoryType.SMITHING);
        player.openInventory(smithingInventory);
        InventoryView view = player.getOpenInventory();
        ItemStack netheriteSword = new ItemStack(Material.NETHERITE_SWORD);

        //Test with netheriteSmithing disabled
        pluginConfig.setNetheriteSmithingEnabled(false);
        smithingInventory.setItem(3, netheriteSword);
        assertFalse(canPrepareSmithingNetherite(view, netheriteSword));
        assertFalse(canSmithNetherite(view));

        //Test with netheriteSmithing enabled
        setNetheriteSmithingEnabled(plugin, pluginConfig, true);
        assertTrue(canPrepareSmithingNetherite(view, netheriteSword));
        assertTrue(canSmithNetherite(view));
    }

    //check if can prepare smithing event with netherite tools
    private boolean canPrepareSmithingNetherite(InventoryView view, ItemStack netheriteSword){
        PrepareSmithingEvent prepare = new PrepareSmithingEvent(view, netheriteSword);
        plugin.getServer().getPluginManager().callEvent(prepare);
        return prepare.getResult() != null;
    }
    
    private boolean canSmithNetherite(InventoryView view){
        SmithItemEvent smith = new SmithItemEvent(view, SlotType.RESULT, 3, ClickType.LEFT, InventoryAction.PICKUP_ALL);
        plugin.getServer().getPluginManager().callEvent(smith);
        return !smith.isCancelled();
    }



    private void setNetheriteSmithingEnabled(FirstPlugin plugin, PluginConfig pluginConfig, boolean enabled) {
        pluginConfig.setNetheriteSmithingEnabled(enabled);
        plugin.saveConfig();
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        plugin.getServer().getPluginManager().enablePlugin(plugin);
    }

}
