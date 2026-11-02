package io.github.pmeiggs.firstPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VillagerTradeLockListenerTest {

    private FirstPlugin plugin;
    private World world;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
        world = MockBukkit.getMock().addSimpleWorld("world");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void careerChangeIsAllowedBeforeVillagerAcquiresTrades() {
        Villager villager = createVillager();

        assertFalse(careerChange(villager).isCancelled());
    }

    @Test
    void careerChangeIsBlockedAfterVillagerAcquiresFirstTrade() {
        Villager villager = createVillager();
        VillagerAcquireTradeEvent acquireTradeEvent = new VillagerAcquireTradeEvent(
                villager,
                new MerchantRecipe(new ItemStack(Material.EMERALD), 10)
        );
        Bukkit.getPluginManager().callEvent(acquireTradeEvent);

        assertFalse(acquireTradeEvent.isCancelled());
        assertTrue(careerChange(villager).isCancelled());
    }

    @Test
    void careerChangeIsBlockedForExistingVillagerWithTrades() {
        Villager villager = createVillager();
        villager.setRecipes(List.of(new MerchantRecipe(new ItemStack(Material.EMERALD), 10)));

        assertTrue(careerChange(villager).isCancelled());
    }

    private Villager createVillager() {
        return world.spawn(new Location(world, 0, 64, 0), Villager.class);
    }

    private VillagerCareerChangeEvent careerChange(Villager villager) {
        VillagerCareerChangeEvent event = new VillagerCareerChangeEvent(
                villager,
                Villager.Profession.LIBRARIAN,
                VillagerCareerChangeEvent.ChangeReason.LOSING_JOB
        );
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
}
