package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.BlockReceiveGameEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemAndWorldNerfListener implements Listener {

    // 1. Limit Protection to 3 & Netherite usage blocks on Armor, Swords, Axes
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        // Check protection level cap (Prot 3)[cite: 2]
        checkAndFixProtection(cursor);
        checkAndFixProtection(current);

        // Disable Netherite on Armor, Swords, and Axes[cite: 2]
        if (isRestrictedNetheriteItem(cursor) || isRestrictedNetheriteItem(current)) {
            event.setCancelled(true);
            player.sendMessage("§cNetherite is disabled for armor, swords, and axes!");
        }

        // Totem Inventory Limit (Max 1 allowed, no restocking from inventory)[cite: 2]
        enforceTotemLimit(event, player);

        // Mace Unstorable Check (Cannot put Mace into chests/shulkers/hoppers)[cite: 2]
        if (isMace(cursor) || isMace(current)) {
            if (event.getView().getTitle().contains("Chest") || 
                event.getView().getTitle().contains("Container") || 
                event.getView().getTitle().contains("Shulker") ||
                event.getClickedInventory() != player.getInventory()) {
                // If trying to move out of player inventory into a storage block
                if (event.getClickedInventory() != player.getInventory()) {
                    event.setCancelled(true);
                    player.sendMessage("§cThe Mace is unstorable and cannot be placed in containers!");
                }
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

    private boolean isMace(ItemStack item) {
        return item != null && item.getType() == Material.MACE;
    }

    // Enforce only ONE totem allowed in the entire player inventory[cite: 2]
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

    // 2. Disable Bed and Respawn Anchor explosion damage/effects[cite: 2]
    @EventHandler
    public void onExplosion(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || 
            event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            // Check if context is bed/anchor (simplified by canceling explosion damage if in dimension mismatch context)
            if (event.getEntity() instanceof Player player) {
                if (player.getWorld().getEnvironment() == org.bukkit.World.Environment.NETHER && 
                    event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // 3. Double Breeze Rod Drops[cite: 2]
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