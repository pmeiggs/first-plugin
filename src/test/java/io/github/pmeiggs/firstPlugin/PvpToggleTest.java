package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.config.PluginConfig;
import io.github.pmeiggs.firstPlugin.combat.CombatTagTracker;
import io.github.pmeiggs.firstPlugin.commands.PvpToggle;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvpToggleTest {

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
    void playerIsInPvpOnlyWhileCombatTagged() {
        CombatTagTracker tracker = new CombatTagTracker();
        PvpToggle pvpToggle = new PvpToggle(plugin, tracker);
        Player player = MockBukkit.getMock().addPlayer();

        tracker.tag(player, System.currentTimeMillis());
        assertTrue(pvpToggle.playerIsInPvp(player));

        tracker.tag(player, System.currentTimeMillis() - 15000);
        assertFalse(pvpToggle.playerIsInPvp(player));
    }

    @Test
    void combatCooldownComesFromPluginConfig() {
        plugin.getConfig().set("combatCooldownMs", 5000);

        CombatTagTracker tracker = new CombatTagTracker(new PluginConfig(plugin));

        assertEquals(5000, tracker.getCombatCooldownMs());
    }
}