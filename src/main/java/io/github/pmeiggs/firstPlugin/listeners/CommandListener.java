package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.firstPlugin.commands.PvpToggle;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class CommandListener implements Listener {

    private final PvpToggle pvpToggle;

    public CommandListener(PvpToggle pvpToggle) {
        this.pvpToggle = pvpToggle;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (pvpToggle.isPvpDisabled(player)) {
            return;
        }

        String raw = event.getMessage().toLowerCase(Locale.ROOT).trim();

        String baseCmd = raw.split("\\s+")[0];

        if (baseCmd.equals("/claim") || baseCmd.endsWith(":claim")) {
            event.setCancelled(true);
            player.sendMessage("You cannot use /claim while PvP is enabled.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void stopClaim(PlayerInteractEvent event) {
        if (pvpToggle.isPvpDisabled(event.getPlayer())) {
            return;
        }
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.GOLDEN_HOE) {
            return;
        }

        event.setCancelled(true);
    }
}
