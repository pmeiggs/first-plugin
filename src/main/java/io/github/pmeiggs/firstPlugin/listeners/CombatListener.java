package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.firstPlugin.commands.PvpToggle;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
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
    private final long COMBAT_COOLDOWN_MS = 15000; // 15 seconds combat block, increased 10 -> 15s
    private final HashMap<UUID, TextColor> previousUsernameColour = new HashMap<>();
    private final HashMap<UUID, Component> previousPlayerListName = new HashMap<>();
    private final JavaPlugin plugin;
    private final PvpToggle pvpToggle;

    public CombatListener(JavaPlugin plugin, PvpToggle pvpToggle) {
        this.plugin = plugin;
        this.pvpToggle = pvpToggle;
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

        Player player = event.getPlayer();
        if (isCombatTagged(player)) { //added functionality for combat logging
            player.setHealth(0);
        }
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
        }
    }
    @EventHandler
    private void disablePunch(EntityShootBowEvent bowShoot) {
        ItemStack bow = bowShoot.getBow();

        if(!(bowShoot.getEntity() instanceof Player player)) {//if entity isnt a player
            return;
        }

        if(pvpToggle.isPvpDisabled(player)){ //if player has pvp disabled
            return;
        }

        if(!(bow.containsEnchantment(Enchantment.PUNCH))) {//if bow doesnt have punch
            return;
        }

        bowShoot.setCancelled(true);
        player.sendMessage("§cYour bow has §lPunch! §r§cYou won't be able to use this one as your combat is toggled on.");
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (!(event.getAction().name().contains("RIGHT"))) {
            return;
        }
        // Disable Enchanted GApples while having combat on
        if (!(pvpToggle.isPvpDisabled(player)) && (item.getType() == Material.ENCHANTED_GOLDEN_APPLE)) { //now only enchanted gapples disabled
            //disabled if combat is on, since players can eat before being tagged
            event.setCancelled(true);
            player.sendMessage("§cYou cannot consume §lEnchanted Golden Apples §r§cwhile having combat on!");
        }

        // Disable Ender Pearls in combat
        if (isCombatTagged(player) && item.getType() == Material.ENDER_PEARL) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot use §lEnder Pearls §r§cwhile in combat!");
        }

        // Disable Riptide Tridents while having combat on
        if (item.getType() == Material.TRIDENT) {
            if (!(pvpToggle.isPvpDisabled(player)) && item.containsEnchantment(Enchantment.RIPTIDE)) {
                event.setCancelled(true);//made it the same as gapples since people can just trident away
                player.sendMessage("§c§lRiptide Tridents §r§care disabled while combat is disabled!");
            }
        }
    }

    // Disable Elytra in combat
    @EventHandler
    public void onToggleFlight(EntityToggleGlideEvent event) {

        if(!(event.getEntity() instanceof Player player)) {
            return;
        }
        //fidxed, playertoggleflight doesnt consider elytra
        if (isCombatTagged(player)) {
            event.setCancelled(true);
            player.sendMessage("§c§lElytra flight §r§cis disabled during combat!");
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

    //new method to verify if combat tagged
    public boolean isCombatTagged(Player player) {
        return combatTags.containsKey(player.getUniqueId()) &&
                (System.currentTimeMillis() - combatTags.get(player.getUniqueId()) < COMBAT_COOLDOWN_MS);
    }
}