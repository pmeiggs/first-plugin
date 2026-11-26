package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.firstPlugin.commands.PvpToggle;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class DroppedItemsListener implements Listener {

    private final PvpToggle pvpToggle;
    private final NamespacedKey droppedWithPvpOffKey;

    public DroppedItemsListener(JavaPlugin plugin, PvpToggle pvpToggle) {
        this.pvpToggle = pvpToggle;
        this.droppedWithPvpOffKey = new NamespacedKey(plugin, "dropped_with_pvp_off");
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (pvpToggle.isPvpDisabled(event.getPlayer())) {
            event.getItemDrop().getPersistentDataContainer()
                    .set(droppedWithPvpOffKey, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || pvpToggle.isPvpDisabled(player)) {
            return;
        }

        byte droppedWithPvpOff = event.getItem().getPersistentDataContainer()
                .getOrDefault(droppedWithPvpOffKey, PersistentDataType.BYTE, (byte) 0);

        if (droppedWithPvpOff == 1) {
            event.setCancelled(true);
        }
    }
}
