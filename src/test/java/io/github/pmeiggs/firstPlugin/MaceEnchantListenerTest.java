package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.firstPlugin.listeners.MaceEnchantListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaceEnchantListenerTest {

    private FirstPlugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
        plugin.getServer().getPluginManager().registerEvents(new MaceEnchantListener(), plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void maceEncahntingCancelled() {
        Player player = MockBukkit.getMock().addPlayer();
        ItemStack mace = new ItemStack(Material.MACE);

        PrepareItemEnchantEvent event = new PrepareItemEnchantEvent(
                player,
                null,
                null,
                mace,
                new org.bukkit.enchantments.EnchantmentOffer[0],
                30
        );

        plugin.getServer().getPluginManager().callEvent(event);

        assertTrue(event.isCancelled());
    }

    @Test
    void maceResultCanelled() {
        Player player = MockBukkit.getMock().addPlayer();

        // open anvil inventory for the player
        AnvilInventory anvilInventory = (AnvilInventory) player.openInventory(Bukkit.createInventory(player, InventoryType.ANVIL)).getTopInventory();

        //put mace and enchanted book in the anvil inventory
        anvilInventory.setItem(0, new ItemStack(Material.MACE));
        anvilInventory.setItem(1, new ItemStack(Material.ENCHANTED_BOOK));
        //we dont really care about namechanges, so just using enchanted book is fine since only that can be used with a mace
        assertNull(anvilInventory.getResult());
    }
}
