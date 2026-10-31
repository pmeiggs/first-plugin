package io.github.pmeiggs.firstPlugin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import io.github.pmeiggs.config.PluginConfig;

public class AntiCrystalPvPTest {
    
    private FirstPlugin plugin;
    private PluginConfig pluginConfig;

    @BeforeEach 
    void setup(){
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
        pluginConfig = new PluginConfig(plugin);
    }

    @AfterEach 
    void tearDown(){
        MockBukkit.unmock();
    }


    @Test 
    void crystalDmgBlockedWhenCpvpDisabled(){
        setCPvPEnabled(plugin, pluginConfig, false);
        assertFalse(doesCrystalExplode());
    }

    @Test 
    void crystalDmgWhenCpvpEnabled(){
        setCPvPEnabled(plugin, pluginConfig, true);
        assertTrue(doesCrystalExplode());
    }

    private boolean doesCrystalExplode(){

        //test setup
        
        Player player = MockBukkit.getMock().addPlayer();
        World world = player.getWorld();
        EnderCrystal crystal = (EnderCrystal) world.spawnEntity(
                new Location(world, 0, 64, 0),
                EntityType.END_CRYSTAL
        );

        //player damages crystal
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                player,
                crystal,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                DamageSource.builder(DamageType.PLAYER_ATTACK)
                        .withCausingEntity(player)
                        .withDirectEntity(player)
                        .build(),
                1.0
        );

        plugin.getServer().getPluginManager().callEvent(event);

        return !event.isCancelled() && crystal.isValid();

    }

    private void setCPvPEnabled(FirstPlugin plugin, PluginConfig pluginConfig, boolean enabled){
        pluginConfig.setCrystalPvpEnabled(enabled);
        plugin.saveConfig();
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        plugin.getServer().getPluginManager().enablePlugin(plugin);
    }



}
