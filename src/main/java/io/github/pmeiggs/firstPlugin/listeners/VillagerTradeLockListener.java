package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class VillagerTradeLockListener implements Listener {
    private final NamespacedKey lockedTradesKey;

    public VillagerTradeLockListener(JavaPlugin plugin) {
        this.lockedTradesKey = new NamespacedKey(plugin, "villager_trades_locked");
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            lockTrades(villager);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCareerChange(VillagerCareerChangeEvent event) {
        Villager villager = event.getEntity();
        if (hasLockedTrades(villager) || !villager.getRecipes().isEmpty()) {
            lockTrades(villager);
            event.setCancelled(true);
        }
    }

    private boolean hasLockedTrades(Villager villager) {
        return villager.getPersistentDataContainer()
                .getOrDefault(lockedTradesKey, PersistentDataType.BYTE, (byte) 0) == 1;
    }

    private void lockTrades(Villager villager) {
        villager.getPersistentDataContainer().set(lockedTradesKey, PersistentDataType.BYTE, (byte) 1);
    }
}
