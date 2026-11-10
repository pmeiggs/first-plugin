package io.github.pmeiggs.firstPlugin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.ExplosionResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import io.github.pmeiggs.config.PluginConfig;

import java.util.List;

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
    }
    //test if explisiojn is cancelled for the methiodsa
    private boolean isExplosionCancelled(Material explodedMaterial) {
        Player player = MockBukkit.getMock().addPlayer();
        World world = player.getWorld();
        Block block = world.getBlockAt(0, 64, 0);
        block.setType(explodedMaterial);
        BlockState explodedState = block.getState();
        block.setType(Material.AIR);
        //creatge block exploide event for anchorsw and bedsad
        BlockExplodeEvent event = new BlockExplodeEvent(
                block,
                explodedState,
                List.of(),
                0.0f,
                ExplosionResult.DESTROY
        );
        plugin.getServer().getPluginManager().callEvent(event);
        return event.isCancelled();
    }
    //tgest if damnage ius cancelled from blkiock expolotionsd
    private boolean isDamageCancelled(DamageType damageType) {
        Player player = MockBukkit.getMock().addPlayer();
        EntityDamageEvent event = new EntityDamageEvent( //create damagfe event foir blkoick exsplosions
                player,
                EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                DamageSource.builder(damageType).build(),
                1.0
        );
        plugin.getServer().getPluginManager().callEvent(event);
        return event.isCancelled();
    }
    @Test //tgest for anchor expoloisions disdabled
    void bedAndAnchorExplosionsAreBlockedWhenCpvpDisabled() {
        setCPvPEnabled(plugin, pluginConfig, false);

        assertTrue(isExplosionCancelled(Material.RED_BED));
        assertTrue(isExplosionCancelled(Material.RESPAWN_ANCHOR));
    }

    @Test//test for other exploisives disabled FALSe
    void otherBlockExplosionsAreNotBlockedWhenCpvpDisabled() {
        setCPvPEnabled(plugin, pluginConfig, false);

        assertFalse(isExplosionCancelled(Material.TNT));
    }

    @Test //test for explosions when cpvp enabled (shoiuld false)
    void bedAndAnchorExplosionsAreAllowedWhenCpvpEnabled() {
        setCPvPEnabled(plugin, pluginConfig, true);

        assertFalse(isExplosionCancelled(Material.RED_BED));
        assertFalse(isExplosionCancelled(Material.RESPAWN_ANCHOR));
    }

    @Test //test for explosion dmg types whjen cpvp disabled
    void bedAndAnchorDamageIsBlockedWithoutBlockingOtherExplosionDamage() {
        setCPvPEnabled(plugin, pluginConfig, false);

        assertTrue(isDamageCancelled(DamageType.BAD_RESPAWN_POINT));
        assertFalse(isDamageCancelled(DamageType.EXPLOSION));
    }

    @Test //test for dmg cancelled when cpvp enabled
    void bedAndAnchorDamageIsAllowedWhenCpvpEnabled() {
        setCPvPEnabled(plugin, pluginConfig, true);

        assertFalse(isDamageCancelled(DamageType.BAD_RESPAWN_POINT));
    }


}
