package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.config.PluginConfig;
import io.github.pmeiggs.firstPlugin.combat.CombatTagTracker;
import io.github.pmeiggs.firstPlugin.commands.PvpToggle;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.command.CommandSourceStackMock;

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

    @Test
    void pvpToggleCannotBeUsedAgainBeforeFiveDays() {
        PvpToggle pvpToggle = new PvpToggle(plugin, new CombatTagTracker());
        Player player = MockBukkit.getMock().addPlayer();
        CommandSourceStack source = CommandSourceStackMock.from(player);

        pvpToggle.execute(source, new String[0]);
        assertTrue(pvpToggle.isPvpDisabled(player));

        pvpToggle.execute(source, new String[0]);
        assertTrue(pvpToggle.isPvpDisabled(player));
    }

    @Test
    void pvpToggleCanBeUsedAgainAfterFiveDays() {
        PvpToggle pvpToggle = new PvpToggle(plugin, new CombatTagTracker());
        Player player = MockBukkit.getMock().addPlayer();
        CommandSourceStack source = CommandSourceStackMock.from(player);

        pvpToggle.execute(source, new String[0]);
        NamespacedKey lastToggleKey = new NamespacedKey(plugin, "pvp_toggle_last_used");
        player.getPersistentDataContainer().set(
                lastToggleKey,
                PersistentDataType.LONG,
                System.currentTimeMillis() - 5L * 24 * 60 * 60 * 1000 - 1
        );

        pvpToggle.execute(source, new String[0]);
        assertFalse(pvpToggle.isPvpDisabled(player));
    }

    @Test
    void adminCanResetAnotherPlayersCooldown() {
        PvpToggle pvpToggle = new PvpToggle(plugin, new CombatTagTracker());
        Player target = MockBukkit.getMock().addPlayer("target");
        Player admin = MockBukkit.getMock().addPlayer("admin");
        admin.addAttachment(plugin, "firstplugin.togglepvp.reset", true);
        CommandSourceStack targetSource = CommandSourceStackMock.from(target);
        CommandSourceStack adminSource = CommandSourceStackMock.from(admin);
        NamespacedKey lastToggleKey = new NamespacedKey(plugin, "pvp_toggle_last_used");

        pvpToggle.execute(targetSource, new String[0]);
        assertTrue(pvpToggle.isPvpDisabled(target));
        assertTrue(target.getPersistentDataContainer().has(lastToggleKey, PersistentDataType.LONG));

        pvpToggle.execute(adminSource, new String[]{"reset", target.getName()});

        assertFalse(target.getPersistentDataContainer().has(lastToggleKey, PersistentDataType.LONG));
        pvpToggle.execute(targetSource, new String[0]);
        assertFalse(pvpToggle.isPvpDisabled(target));
    }

    @Test
    void playerWithoutResetPermissionCannotResetCooldown() {
        PvpToggle pvpToggle = new PvpToggle(plugin, new CombatTagTracker());
        Player target = MockBukkit.getMock().addPlayer("target");
        Player player = MockBukkit.getMock().addPlayer("player");
        CommandSourceStack targetSource = CommandSourceStackMock.from(target);
        CommandSourceStack playerSource = CommandSourceStackMock.from(player);
        NamespacedKey lastToggleKey = new NamespacedKey(plugin, "pvp_toggle_last_used");

        pvpToggle.execute(targetSource, new String[0]);
        assertTrue(target.getPersistentDataContainer().has(lastToggleKey, PersistentDataType.LONG));

        pvpToggle.execute(playerSource, new String[]{"reset", target.getName()});

        assertTrue(target.getPersistentDataContainer().has(lastToggleKey, PersistentDataType.LONG));
        pvpToggle.execute(targetSource, new String[0]);
        assertTrue(pvpToggle.isPvpDisabled(target));
    }
}