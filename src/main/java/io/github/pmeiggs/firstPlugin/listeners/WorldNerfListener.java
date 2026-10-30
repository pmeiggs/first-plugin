package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class WorldNerfListener implements Listener {

    // Disable Nether bed explosion damage
    @EventHandler
    public void onExplosion(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || 
            event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            if (event.getEntity() instanceof Player player) {
                if (player.getWorld().getEnvironment() == World.Environment.NETHER && 
                    event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // Double Breeze Rod Drops
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType().name().equals("BREEZE")) {
            for (ItemStack drop : event.getDrops()) {
                if (drop.getType() == Material.BREEZE_ROD) {
                    drop.setAmount(drop.getAmount() * 2);
                }
            }
        }
    }
}