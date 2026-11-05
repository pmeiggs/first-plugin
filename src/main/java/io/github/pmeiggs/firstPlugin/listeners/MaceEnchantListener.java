package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class MaceEnchantListener implements Listener {

    //prevents enchanting the mace in etable
    @EventHandler
    public void maceEnchantDisable(PrepareItemEnchantEvent event) {

        ItemStack item = event.getItem();

        if (item.getType() == Material.MACE) {
            // cancel event if the item is a Mace
            event.setCancelled(true);
        }
    }

    @EventHandler // prevent combining/enchanting via the Anvil
    public void maceAnvilDisable(PrepareAnvilEvent event) {
        try {
            if (event == null || event.getInventory() == null) {
                return;
            }

            ItemStack targetItem = event.getInventory().getItem(0); // Left slot
            if (targetItem != null && targetItem.getType() == Material.MACE) {
                event.setResult(null);
            }
        } catch (NullPointerException ignored) {
            // Some mocked/invalid prepare-anvil events may not have a valid inventory view.
        }
    }
}