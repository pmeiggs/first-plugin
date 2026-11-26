package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.firstPlugin.FirstPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityBreedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VillagerBreedingListenerTest {

    private World world;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        MockBukkit.load(FirstPlugin.class);
        world = MockBukkit.getMock().addSimpleWorld("world");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test //check for event cancelled once villagers have bred once
    void villagersCanBreedOnceButCannotBreedAgain() {
        Villager mother = createVillager();
        Villager father = createVillager();

        assertFalse(breed(mother, father).isCancelled());
        assertTrue(breed(mother, father).isCancelled());
    }

    @Test //check for villagers who want to breed with partners
    void villagerCannotBreedAgainWithANewPartner() {
        Villager mother = createVillager();

        assertFalse(breed(mother, createVillager()).isCancelled());
        assertTrue(breed(mother, createVillager()).isCancelled());
    }

    @Test//check for cancel not uisng breed chance
    void cancelledBreedDoesNotUseUpEitherVillagersBreed() {
        Villager mother = createVillager();
        Villager father = createVillager();
        EntityBreedEvent cancelledEvent = newBreedEvent(mother, father);
        cancelledEvent.setCancelled(true);
        Bukkit.getPluginManager().callEvent(cancelledEvent);

        assertTrue(cancelledEvent.isCancelled());
        assertFalse(breed(mother, father).isCancelled());
    }
//villager
    private Villager createVillager() {
        return world.spawn(new Location(world, 0, 64, 0), Villager.class);
    }
//breed event
    private EntityBreedEvent breed(Villager mother, Villager father) {
        EntityBreedEvent event = newBreedEvent(mother, father);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
//new breed event for reusage in tests
    private EntityBreedEvent newBreedEvent(Villager mother, Villager father) {
        return new EntityBreedEvent(createVillager(), mother, father, null, null, 0);
    }
}
