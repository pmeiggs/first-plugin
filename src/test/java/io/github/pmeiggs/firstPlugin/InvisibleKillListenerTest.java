package io.github.pmeiggs.firstPlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import io.github.pmeiggs.config.PluginConfig;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InvisibleKillListenerTest {

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
    void invisiblePlayerCanBeKilledWhenEnabled(){
        Player killed = MockBukkit.getMock().addPlayer();
        Player invisiblePlayer = MockBukkit.getMock().addPlayer();

        invisiblePlayer.setInvisible(true);
        killed.setKiller(invisiblePlayer);

        PlayerDeathEvent deathEvent = new PlayerDeathEvent(
                killed,
                DamageSource.builder(DamageType.PLAYER_ATTACK)
                        .withCausingEntity(invisiblePlayer)
                        .build(),
                new ArrayList<>(),
                0,
                0,
                Component.text(killed.getName() + " was killed by §kinvisible§r."),
                true
        );

        assertSame(killed.getKiller(), invisiblePlayer);
        assertFalse(deathEvent.deathMessage().toString().contains(invisiblePlayer.getName()));
    }



}
