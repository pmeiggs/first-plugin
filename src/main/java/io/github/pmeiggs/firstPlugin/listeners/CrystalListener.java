package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.config.PluginConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.damage.DamageType;

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

    @EventHandler //cancel the bed or anchort explosion
    public void onBedOrAnchorExplosion(BlockExplodeEvent event) {
        if (pluginConfig.isCrystalPvpEnabled()) {
            return;
        }

        if (!(isBedOrAnchor(event.getExplodedBlockState().getType()))) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler //cancel the damaghe from explosion ONLY fromn achors and beds
    public void onBlockExplosionDamage(EntityDamageEvent event) {
        if (pluginConfig.isCrystalPvpEnabled()) {
            return;
        }

        if (!(event.getDamageSource().getDamageType() == DamageType.BAD_RESPAWN_POINT)) {
            return;
        }

        event.setCancelled(true);
    }
    //chech if itgs a bed or anchor for reusabilityu in event handlers.
    private boolean isBedOrAnchor(Material material) {
        return Tag.BEDS.isTagged(material) || material == Material.RESPAWN_ANCHOR;
    }
}
