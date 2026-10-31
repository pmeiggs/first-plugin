package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.config.PluginConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CrystalListener implements Listener {

    private final PluginConfig pluginConfig;

    public CrystalListener(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @EventHandler
    public void onCrystalHit(EntityDamageByEntityEvent event) {
        if (pluginConfig.isCrystalPvpEnabled()) {
            return;
        }

        if (!(event.getEntity() instanceof EnderCrystal)) {
            return;
        }

        event.setCancelled(true);
        event.getEntity().remove();

        if (event.getDamager() instanceof Player player) {
            player.sendMessage(Component.text(
                    "End Crystal damage and explosion has been disabled, but you can still break them.")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD)
            );
        }

        if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                shooter.sendMessage(Component.text(
                        "End Crystal damage and explosion has been disabled, but you can still break them.")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                );
            }
        }
    }
}
