package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LibrarianBookPriceListenerTest {
    private World world;
    private PluginConfig pluginConfig;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        FirstPlugin plugin = MockBukkit.load(FirstPlugin.class);
        pluginConfig = new PluginConfig(plugin);
        world = MockBukkit.getMock().addSimpleWorld("world");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void increasesLibrarianEnchantedBookPriceByOneAndAHalf() {
        MerchantRecipe recipe = acquireTrade(Villager.Profession.LIBRARIAN, Material.ENCHANTED_BOOK, 20);

        assertEquals(30, recipe.getIngredients().get(0).getAmount());
        assertEquals(Material.BOOK, recipe.getIngredients().get(1).getType());
    }

    @Test
    void roundsUpAndCapsEmeraldPriceAtOneStack() {
        MerchantRecipe roundedUp = acquireTrade(Villager.Profession.LIBRARIAN, Material.ENCHANTED_BOOK, 3);
        MerchantRecipe capped = acquireTrade(Villager.Profession.LIBRARIAN, Material.ENCHANTED_BOOK, 50);

        assertEquals(5, roundedUp.getIngredients().get(0).getAmount());
        assertEquals(64, capped.getIngredients().get(0).getAmount());
    }

    @Test
    void usesConfiguredPriceMultiplier() {
        pluginConfig.setEnchantedBookPriceMultiplier(2.0);

        MerchantRecipe recipe = acquireTrade(Villager.Profession.LIBRARIAN, Material.ENCHANTED_BOOK, 20);

        assertEquals(40, recipe.getIngredients().get(0).getAmount());
    }

    @Test
    void doesNotChangeOtherOffersOrOtherProfessions() {
        MerchantRecipe nonBookOffer = acquireTrade(Villager.Profession.LIBRARIAN, Material.BOOK, 20);
        MerchantRecipe otherProfession = acquireTrade(Villager.Profession.FARMER, Material.ENCHANTED_BOOK, 20);

        assertEquals(20, nonBookOffer.getIngredients().get(0).getAmount());
        assertEquals(20, otherProfession.getIngredients().get(0).getAmount());
    }

    private MerchantRecipe acquireTrade(Villager.Profession profession, Material result, int emeralds) {
        Villager villager = world.spawn(new Location(world, 0, 64, 0), Villager.class);
        villager.setProfession(profession);

        MerchantRecipe recipe = new MerchantRecipe(new ItemStack(result), 12);
        recipe.setIngredients(List.of(
                new ItemStack(Material.EMERALD, emeralds),
                new ItemStack(Material.BOOK)
        ));
        VillagerAcquireTradeEvent event = new VillagerAcquireTradeEvent(villager, recipe);
        Bukkit.getPluginManager().callEvent(event);
        return event.getRecipe();
    }

}
