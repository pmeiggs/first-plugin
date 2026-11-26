package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.config.PluginConfig;
import io.github.pmeiggs.firstPlugin.commands.PvpToggle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener implements Listener {
    private final PluginConfig pluginConfig;
    private final PvpToggle pvpToggle;

    public ConnectionListener(PluginConfig pluginConfig, PvpToggle pvpToggle) {
        this.pluginConfig = pluginConfig;
        this.pvpToggle = pvpToggle;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.text(pluginConfig.getJoinMessagePrefix())
                .color(NamedTextColor.GRAY)
                .append(Component.text(pluginConfig.getJoinMessageSuffix())
                        .decorate(TextDecoration.BOLD)));
        pvpToggle.updatePlayerListName(event.getPlayer());
    }
}
