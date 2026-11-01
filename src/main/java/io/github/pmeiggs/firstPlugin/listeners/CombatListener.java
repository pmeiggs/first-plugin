package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class CombatListener implements Listener {

    private final HashMap<UUID, Long> combatTags = new HashMap<>();
    private final long COMBAT_COOLDOWN_MS = 10000; // 10 seconds combat block
    private final HashMap<UUID, TextColor> previousUsernameColour = new HashMap<>();
    private final HashMap<UUID, Component> previousPlayerListName = new HashMap<>();
    private final JavaPlugin plugin;

    public CombatListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private void tagPlayer(Player player, long now) {
        UUID playerId = player.getUniqueId();
        if (!previousUsernameColour.containsKey(playerId)) {
            previousUsernameColour.put(playerId, player.displayName().color());
            previousPlayerListName.put(playerId, player.playerListName());
        }

        player.displayName(player.displayName().color(NamedTextColor.RED));
        Component tabName = player.playerListName();
        player.playerListName((tabName == null
                ? Component.text(player.getName())
                : tabName).color(NamedTextColor.RED));
        combatTags.put(playerId, now);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!Long.valueOf(now).equals(combatTags.get(playerId))) {
                return;
            }

            combatTags.remove(playerId);
            restoreUsernameColour(playerId);
        }, COMBAT_COOLDOWN_MS / 50);
    }

    private void restoreUsernameColour(UUID playerId) {
        if (!previousUsernameColour.containsKey(playerId)) {
            return;
        }

        TextColor previousColour = previousUsernameColour.remove(playerId);
        Component originalTabName = previousPlayerListName.remove(playerId);
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            player.displayName(player.displayName().color(previousColour));
            player.playerListName(originalTabName);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        combatTags.remove(playerId);
        restoreUsernameColour(playerId);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        // Combat tagging for players
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker) {
            long now = System.currentTimeMillis();
            tagPlayer(victim, now);
            tagPlayer(attacker, now);
        } else if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player attacker) {
            long now = System.currentTimeMillis();
            tagPlayer(victim, now);
            tagPlayer(attacker, now);

            // Disable Punch Enchantment in combat
            ItemStack bow = attacker.getInventory().getItemInMainHand();
            if (bow.containsEnchantment(Enchantment.PUNCH)) {
                arrow.setKnockbackStrength(0);
                attacker.sendMessage("§cPunch enchantment is disabled in combat!");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        boolean inCombat = combatTags.containsKey(player.getUniqueId()) &&
                (System.currentTimeMillis() - combatTags.get(player.getUniqueId()) < COMBAT_COOLDOWN_MS);

        // Disable GApples in combat
        if (inCombat && (item.getType() == Material.GOLDEN_APPLE || item.getType() == Material.ENCHANTED_GOLDEN_APPLE)) {
            if (event.getAction().name().contains("RIGHT")) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot consume Golden Apples while in combat!");
            }
        }

        // Disable Ender Pearls in combat
        if (inCombat && item.getType() == Material.ENDER_PEARL) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot use Ender Pearls while in combat!");
        }

        // Disable Riptide Tridents in combat
        if (item.getType() == Material.TRIDENT) {
            if (inCombat && item.containsEnchantment(Enchantment.RIPTIDE)) {
                event.setCancelled(true);
                player.sendMessage("§cRiptide Tridents are disabled during combat!");
            }
        }
    }

    // Disable Elytra in combat
    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        boolean inCombat = combatTags.containsKey(player.getUniqueId()) &&
                (System.currentTimeMillis() - combatTags.get(player.getUniqueId()) < COMBAT_COOLDOWN_MS);

        if (inCombat && player.isGliding()) {
            event.setCancelled(true);
            player.sendMessage("§cElytra flight is disabled during combat!");
        }
    }

    // Block restricted potion effects during combat/gameplay
    @EventHandler
    public void onPotionDrink(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player) {
            PotionEffectType type = event.getModifiedType();
            if (type != null && (type.equals(PotionEffectType.POISON) ||
                    type.equals(PotionEffectType.WEAKNESS) ||
                    type.equals(PotionEffectType.SLOWNESS) ||
                    type.equals(PotionEffectType.SLOW_FALLING))) {
                event.setCancelled(true);
            }
        }
    }
}