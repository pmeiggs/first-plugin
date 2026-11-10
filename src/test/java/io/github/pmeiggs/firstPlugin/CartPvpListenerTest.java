package io.github.pmeiggs.firstPlugin;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
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

    @Test
    void CartDamageIsCappedAtTwentyHealthPoints() {
        Player victim = MockBukkit.getMock().addPlayer();

        Entity tntMinecart = victim.getWorld().spawnEntity(
                victim.getLocation(),
                EntityType.TNT_MINECART
        );

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                tntMinecart,
                victim,
                EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
                24.0
        );

        plugin.getServer().getPluginManager().callEvent(event);

        assertEquals(20.0, event.getDamage(), 0.001);
    }
}
