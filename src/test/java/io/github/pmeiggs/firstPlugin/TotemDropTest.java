package io.github.pmeiggs.firstPlugin;

import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TotemDropTest {
    private FirstPlugin plugin;

    @BeforeEach void setup() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
    }

    @AfterEach void cleanup() {
        MockBukkit.unmock();
    }

    @Test void evokersDropNoTotemsButKeepTheirOtherLoot() {
        var player = MockBukkit.getMock().addPlayer();
        LivingEntity evoker = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), EntityType.EVOKER);
        var emeralds = new ItemStack(Material.EMERALD, 3);
        var drops = new ArrayList<>(List.of(new ItemStack(Material.TOTEM_OF_UNDYING), emeralds,
                new ItemStack(Material.TOTEM_OF_UNDYING)));
        var event = new EntityDeathEvent(evoker, DamageSource.builder(DamageType.GENERIC).build(), drops);
        event.setDroppedExp(10);
        plugin.getServer().getPluginManager().callEvent(event);
        assertEquals(List.of(emeralds), event.getDrops());
        assertEquals(10, event.getDroppedExp());
    }

    @Test void playerDeathStillDropsTotems() {
        var player = MockBukkit.getMock().addPlayer();
        var totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        var drops = new ArrayList<>(List.of(totem));
        var event = new org.bukkit.event.entity.PlayerDeathEvent(player,
                DamageSource.builder(DamageType.GENERIC).build(), drops, 0, 0, null, true);
        // Isolate loot handling from the existing uninitialized death-message listener.
        new io.github.pmeiggs.firstPlugin.listeners.WorldNerfListener().onEntityDeath(event);
        assertEquals(List.of(totem), event.getDrops());
    }

    @Test void otherMobsKeepTotemsAndExistingBreezeRodRuleStillWorks() {
        var player = MockBukkit.getMock().addPlayer();
        LivingEntity breeze = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), EntityType.BREEZE);
        var totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        var rods = new ItemStack(Material.BREEZE_ROD, 2);
        var drops = new ArrayList<>(List.of(totem, rods));
        var event = new EntityDeathEvent(breeze, DamageSource.builder(DamageType.GENERIC).build(), drops);
        plugin.getServer().getPluginManager().callEvent(event);
        assertEquals(List.of(totem, rods), event.getDrops());
        assertEquals(4, rods.getAmount());
    }
}
