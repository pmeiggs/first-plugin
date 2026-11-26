package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class VillagerBreedingListener implements Listener {
    private final NamespacedKey hasBredKey; //key to limnit breeding to 1 per villager

    public VillagerBreedingListener(JavaPlugin plugin) {
        this.hasBredKey = new NamespacedKey(plugin, "villager_has_bred");
    }
//highest prio to make it work
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void preventRepeatBreeding(EntityBreedEvent event) {
        if (!(event.getMother() instanceof Villager mother)//define 2 villagers as mother and father cuz why not
                || !(event.getFather() instanceof Villager father)) {
            return;
        }
        //check for key and set cancelled if either true
        if (hasBred(mother) || hasBred(father)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void markSuccessfulBreeding(EntityBreedEvent event) { //check for successful breeding then mark as bred
        if (event.getMother() instanceof Villager mother
                && event.getFather() instanceof Villager father) {
            markAsBred(mother);
            markAsBred(father);
        }
    }
//check for breeding
    private boolean hasBred(Villager villager) {
        return villager.getPersistentDataContainer()
                .getOrDefault(hasBredKey, PersistentDataType.BYTE, (byte) 0) == 1;
    }
//mark as bred
    private void markAsBred(Villager villager) {
        villager.getPersistentDataContainer().set(hasBredKey, PersistentDataType.BYTE, (byte) 1);
    }
}
