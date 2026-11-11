package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.config.PluginConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBedOrAnchorUse(PlayerInteractEvent event) {
        if (pluginConfig.isCrystalPvpEnabled()
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.useInteractedBlock() == Event.Result.DENY) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Player player = event.getPlayer();
        // Sneaking with an item bypasses block use, allowing placement against it.
        if (player.isSneaking() && (!player.getInventory().getItemInMainHand().isEmpty()
                || !player.getInventory().getItemInOffHand().isEmpty())) {
            return;
        }

        boolean wouldExplode = false;
        if (Tag.BEDS.isTagged(block.getType())) {
            wouldExplode = !block.getWorld().isBedWorks();
        } else if (block.getBlockData() instanceof RespawnAnchor anchor
                && !block.getWorld().isRespawnAnchorWorks() && anchor.getCharges() > 0) {
            boolean roomForCharge = anchor.getCharges() < anchor.getMaximumCharges();
            boolean chargingThisHand = event.getMaterial() == Material.GLOWSTONE && roomForCharge;
            // Vanilla lets the off hand charge when the main hand has no glowstone.
            boolean chargingOffHand = event.getHand() == EquipmentSlot.HAND && roomForCharge
                    && player.getInventory().getItemInOffHand().getType() == Material.GLOWSTONE;
            wouldExplode = !chargingThisHand && !chargingOffHand;
        }

        if (wouldExplode) {
            // Stop before vanilla removes the block. Deny item use too so the
            // blocked click cannot accidentally place or consume a held item.
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
            event.getPlayer().sendMessage(Component.text(
                            "Bed and Respawn Anchor explosions are disabled. You can still use them as intended.")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD));
        }
    }

    @EventHandler // Keep explosion cancellation as a fallback for other triggers.
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
