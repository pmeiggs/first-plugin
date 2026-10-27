package io.github.pmeiggs.firstPlugin.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener implements Listener{

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.text(
                "Hi we're making a plugin for events and stuff that you'd like to see on the server.")
                .color(NamedTextColor.GRAY)
                .append(Component.text(" Please vote for what you'd prefer on the polls on Discord, many thanks!")
                        .decorate(TextDecoration.BOLD)));
    }
}
