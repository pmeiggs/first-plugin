package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.config.PluginConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;



//checks that disabling and reenabling of custom enchantments through the config works correctly.
class CustomEnchantmentConfigTest {

    private FirstPlugin plugin;
    private PluginConfig pluginConfig;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
        pluginConfig = new PluginConfig(plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void tunnelingIsEnabledByDefaultAndCanBeDisabled() {
        assertTrue(pluginConfig.isTunnelingEnabled());

        plugin.getConfig().set("custom-enchantments.tunneling.enabled", false);

        assertFalse(pluginConfig.isTunnelingEnabled());
    }
}
