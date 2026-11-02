package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.firstPlugin.listeners.EnchantmentCapListener;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantedBookMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnchantmentCapListenerTest {

    private FirstPlugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void enchantingTableOffersAreCappedAtLevel3() {
        Player player = MockBukkit.getMock().addPlayer();
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        EnchantingInventory inventory = (EnchantingInventory) MockBukkit.getMock().createInventory(null, InventoryType.ENCHANTING);

        // Mock enchanting offers simulating rolls for Sharpness 5 or Protection 4
        PrepareItemEnchantEvent event = new PrepareItemEnchantEvent(player, null, inventory, item, new org.bukkit.enchantments.EnchantmentOffer[]{
                new org.bukkit.enchantments.EnchantmentOffer(Enchantment.SHARPNESS, 5, 30),
                new org.bukkit.enchantments.EnchantmentOffer(Enchantment.PROTECTION, 4, 30),
                new org.bukkit.enchantments.EnchantmentOffer(Enchantment.UNBREAKING, 3, 10)
        }, new int[]{1, 2, 3}, 0);

        plugin.getServer().getPluginManager().callEvent(event);

        assertEquals(3, event.getOffers()[0].getEnchantmentLevel());
        assertEquals(3, event.getOffers()[1].getEnchantmentLevel());
        assertEquals(3, event.getOffers()[2].getEnchantmentLevel()); 
        //unbreaking 3 should remain untouched
    }

    @Test
    void enchantingItemEventClampsEnchantsToAdd() {
        Player player = MockBukkit.getMock().addPlayer();
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        EnchantingInventory inventory = (EnchantingInventory) MockBukkit.getMock().createInventory(null, InventoryType.ENCHANTING);

        Map<Enchantment, Integer> enchantsToAdd = new HashMap<>();
        enchantsToAdd.put(Enchantment.SHARPNESS, 5);
        enchantsToAdd.put(Enchantment.PROTECTION, 4);

        EnchantItemEvent event = new EnchantItemEvent(player, inventory, item, 30, enchantsToAdd, new HashMap<>(), 0);
        plugin.getServer().getPluginManager().callEvent(event);

        assertEquals(3, event.getEnchantsToAdd().get(Enchantment.SHARPNESS));
        assertEquals(3, event.getEnchantsToAdd().get(Enchantment.PROTECTION));
    }

    @Test
    void anvilCombinationsAreCappedAtLevel3() {
        Player player = MockBukkit.getMock().addPlayer();
        AnvilInventory anvilInventory = (AnvilInventory) MockBukkit.getMock().createInventory(null, InventoryType.ANVIL);
        
        ItemStack result = new ItemStack(Material.DIAMOND_SWORD);
        result.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);

        PrepareAnvilEvent event = new PrepareAnvilEvent(anvilInventory, result);
        plugin.getServer().getPluginManager().callEvent(event);

        assertEquals(3, event.getResult().getEnchantmentLevel(Enchantment.SHARPNESS));
    }

    @Test
    void villagerLibrarianTradesForHighTierEnchantsAreCapped() {
        Villager villager = (Villager) MockBukkit.getMock().addPlayer().getWorld().spawnEntity(
                MockBukkit.getMock().addPlayer().getLocation(), EntityType.VILLAGER);

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantedBookMeta meta = (EnchantedBookMeta) book.getItemMeta();
        meta.addStoredEnchant(Enchantment.PROTECTION, 4, true);
        book.setItemMeta(meta);

        MerchantRecipe recipe = new MerchantRecipe(book, 0, 10, true, 1, 0.05f);
        recipe.setIngredients(List.of(new ItemStack(Material.EMERALD, 20)));

        VillagerAcquireTradeEvent event = new VillagerAcquireTradeEvent(villager, recipe);
        plugin.getServer().getPluginManager().callEvent(event);

        EnchantedBookMeta resultMeta = (EnchantedBookMeta) event.getRecipe().getResult().getItemMeta();
        assertEquals(3, resultMeta.getStoredEnchantLevel(Enchantment.PROTECTION));
    }

    @Test
    void inventorySafeguardClampsIllegalEnchantments() {
        Player player = MockBukkit.getMock().addPlayer();
        ItemStack illegalItem = new ItemStack(Material.DIAMOND_SWORD);
        illegalItem.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);

        player.getInventory().setItemInMainHand(illegalItem);

        //should trigger an inventory click event involving the item
        InventoryClickEvent event = new InventoryClickEvent(
                player.getOpenInventory(),
                InventoryType.SlotType.CONTAINER,
                0,
                org.bukkit.event.inventory.ClickType.LEFT,
                org.bukkit.event.inventory.InventoryAction.PICKUP_ALL
        );

        plugin.getServer().getPluginManager().callEvent(event);

        assertEquals(3, player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SHARPNESS));
    }
}
