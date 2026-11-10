package io.github.pmeiggs.firstPlugin;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartPvpListenerTest {

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

//    @Test
//    void CartDamageIsCappedAtTwentyHealthPoints() {
//        Assertions.assertNotNull(MockBukkit.getMock());
//        Entity cart = MockBukkit.getMock().entitybuilder().type(EntityType.MINECART).build();
//        Player victim = MockBukkit.getMock().addPlayer();
//        attacker.getInventory().setItemInMainHand(new ItemStack(Material.MACE));
//        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
//                attacker, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 24);
//
//        plugin.getServer().getPluginManager().callEvent(event);
//
//        assertEquals(16, event.getFinalDamage(), 0.001);
//    }
}
