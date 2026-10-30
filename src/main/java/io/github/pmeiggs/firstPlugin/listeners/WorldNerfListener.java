package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class WorldNerfListener implements Listener {


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