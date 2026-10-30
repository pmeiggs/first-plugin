package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        // Check protection level cap (Prot 3)
        checkAndFixProtection(cursor);
        checkAndFixProtection(current);

        // Check and cap Sharpness level on weapons (Sharpness <= 3)
        checkAndFixSharpness(cursor);
        checkAndFixSharpness(current);

        // Disable Netherite on Armor, Swords, and Axes
        if (isRestrictedNetheriteItem(cursor) || isRestrictedNetheriteItem(current)) {
            event.setCancelled(true);
            player.sendMessage("§cNetherite is disabled for armor, swords, and axes!");
        }

        // Totem Inventory Limit (Max 1 allowed)
        enforceTotemLimit(event, player);

        // Mace Unstorable Check (Cannot put Mace into chests/containers)
        if (isMace(cursor) || isMace(current)) {
            if (event.getClickedInventory() != null && event.getClickedInventory() != player.getInventory()) {
                event.setCancelled(true);
                player.sendMessage("§cThe Mace is unstorable and cannot be placed in containers!");
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (ItemStack item : event.getNewItems().values()) {
            if (isRestrictedNetheriteItem(item)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onDamageItemNerfs(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            //double check sharpness level is 3 and not higher during combat 
            ItemStack weapon = player.getInventory().getItemInMainHand();
            checkAndFixSharpness(weapon);

            // Limit Mace damage to 8 hearts (16 damage points) & make unbreakable
            if (weapon.getType() == Material.MACE) {
                if (event.getFinalDamage() > 16.0) {
                    event.setDamage(16.0);
                }
                var meta = weapon.getItemMeta();
                if (meta != null && !meta.isUnbreakable()) {
                    meta.setUnbreakable(true);
                    weapon.setItemMeta(meta);
                }
            }
        }

        // Limit Cart damage to 10 hearts (20 damage points)
        if (event.getDamager().getType().name().contains("MINECART") && event.getFinalDamage() > 20.0) {
            event.setDamage(20.0);
        }
    }

    private boolean isRestrictedNetheriteItem(ItemStack item) {
        if (item == null) return false;
        String name = item.getType().name();
        return name.startsWith("NETHERITE_") && 
              (name.contains("HELMET") || name.contains("CHESTPLATE") || 
               name.contains("LEGGINGS") || name.contains("BOOTS") || 
               name.contains("SWORD") || name.contains("AXE"));
    }

    private void checkAndFixProtection(ItemStack item) {
        if (item != null && item.containsEnchantment(Enchantment.PROTECTION)) {
            if (item.getEnchantmentLevel(Enchantment.PROTECTION) > 3) {
                item.removeEnchantment(Enchantment.PROTECTION);
                item.addEnchantment(Enchantment.PROTECTION, 3);
            }
        }
    }

    private void checkAndFixSharpness(ItemStack item) {
        if (item != null && item.containsEnchantment(Enchantment.SHARPNESS)) {
            if (item.getEnchantmentLevel(Enchantment.SHARPNESS) > 3) {
                item.removeEnchantment(Enchantment.SHARPNESS);
                item.addEnchantment(Enchantment.SHARPNESS, 3);
            }
        }
    }

    private boolean isMace(ItemStack item) {
        return item != null && item.getType() == Material.MACE;
    }

    //bugged code icl, will look at it later, but for now, this is the best way to enforce the totem limit
    private void enforceTotemLimit(InventoryClickEvent event, Player player) {
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        
        boolean addingTotem = (cursor != null && cursor.getType() == Material.TOTEM_OF_UNDYING) || 
                              (current != null && current.getType() == Material.TOTEM_OF_UNDYING && event.isShiftClick());

        if (addingTotem) {
            int totemCount = 0;
            for (ItemStack content : player.getInventory().getContents()) {
                if (content != null && content.getType() == Material.TOTEM_OF_UNDYING) {
                    totemCount += content.getAmount();
                }
            }
            if (totemCount >= 1) {
                event.setCancelled(true);
                player.sendMessage("§cYou can only have 1 Totem of Undying in your inventory at a time!");
            }
        }
    }
}
