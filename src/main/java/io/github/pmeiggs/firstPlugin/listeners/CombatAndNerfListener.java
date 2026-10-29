package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class CombatAndNerfListener implements Listener {

    // Tracks combat tags: Player UUID -> Epoch timestamp of last hit
    private final HashMap<UUID, Long> combatTags = new HashMap<>();
    private final long COMBAT_COOLDOWN_MS = 10000; // 10 seconds combat block

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        // 1. Combat tagging for players
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker) {
            long now = System.currentTimeMillis();
            combatTags.put(victim.getUniqueId(), now);
            combatTags.put(attacker.getUniqueId(), now);
        } else if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player attacker) {
            long now = System.currentTimeMillis();
            combatTags.put(victim.getUniqueId(), now);
            combatTags.put(attacker.getUniqueId(), now);

            // Disable Punch Enchantment in combat
            ItemStack bow = attacker.getInventory().getItemInMainHand();
            if (bow.containsEnchantment(Enchantment.PUNCH)) {
                arrow.setKnockbackStrength(0);
                attacker.sendMessage("§cPunch enchantment is disabled in combat!");
            }
        }

        // 2. Limit Sharpness to 3 and Protection to 3
        if (event.getDamager() instanceof Player player) {
            ItemStack weapon = player.getInventory().getItemInMainHand();
            if (weapon.containsEnchantment(Enchantment.SHARPNESS)) {
                if (weapon.getEnchantmentLevel(Enchantment.SHARPNESS) > 3) {
                    weapon.removeEnchantment(Enchantment.SHARPNESS);
                    weapon.addEnchantment(Enchantment.SHARPNESS, 3);
                }
            }

            // 3. Limit Mace damage to 8 hearts (16 damage points)
            if (weapon.getType() == Material.MACE) {
                if (event.getFinalDamage() > 16.0) {
                    event.setDamage(16.0);
                }
                
                // Make Mace unique & indestructible
                var meta = weapon.getItemMeta();
                if (meta != null && !meta.isUnbreakable()) {
                    meta.setUnbreakable(true);
                    weapon.setItemMeta(meta);
                }
            }
        }

        // 4. Limit Cart damage to 10 hearts (20 damage points)
        if (event.getDamager().getType().name().contains("MINECART") && event.getFinalDamage() > 20.0) {
            event.setDamage(20.0);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        boolean inCombat = combatTags.containsKey(player.getUniqueId()) &&
                (System.currentTimeMillis() - combatTags.get(player.getUniqueId()) < COMBAT_COOLDOWN_MS);

        // 5. Disable GApples in combat
        if (inCombat && (item.getType() == Material.GOLDEN_APPLE || item.getType() == Material.ENCHANTED_GOLDEN_APPLE)) {
            if (event.getAction().name().contains("RIGHT")) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot consume Golden Apples while in combat!");
            }
        }

        // 6. Disable Ender Pearls in combat
        if (inCombat && item.getType() == Material.ENDER_PEARL) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot use Ender Pearls while in combat!");
        }
    }

    // 7. Disable Elytra in combat
    @EventHandler
    public void onToggleFlight(org.bukkit.event.player.PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        boolean inCombat = combatTags.containsKey(player.getUniqueId()) &&
                (System.currentTimeMillis() - combatTags.get(player.getUniqueId()) < COMBAT_COOLDOWN_MS);

        if (inCombat && player.isGliding()) {
            event.setCancelled(true);
            player.sendMessage("§cElytra flight is disabled during combat!");
        }
    }

    // 8. Block restricted potion effects (Poison, Slowness, Weakness, etc.)
    @EventHandler
    public void onPotionDrink(org.bukkit.event.entity.EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player) {
            PotionEffectType type = event.getModifiedType();
            if (type.equals(PotionEffectType.POISON) ||
                    type.equals(PotionEffectType.WEAKNESS) ||
                    type.equals(PotionEffectType.SLOWNESS) ||
                    type.equals(PotionEffectType.SLOW_FALLING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRiptideUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.TRIDENT) return;

        boolean inCombat = combatTags.containsKey(player.getUniqueId()) &&
                (System.currentTimeMillis() - combatTags.get(player.getUniqueId()) < COMBAT_COOLDOWN_MS);

        if (inCombat && item.containsEnchantment(Enchantment.RIPTIDE)) {
            event.setCancelled(true);
            player.sendMessage("§cRiptide Tridents are disabled during combat!");
        }
    }
}
