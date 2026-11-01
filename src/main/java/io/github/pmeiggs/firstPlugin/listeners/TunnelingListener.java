package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class TunnelingListener implements Listener {

    private final JavaPlugin plugin;
    private final PluginConfig pluginConfig;
    private final Enchantment tunneling;
    private final Set<UUID> playersBreakingTunnelBlocks = new HashSet<>();
    private static final Set<Material> TUNNELABLE_BLOCKS = EnumSet.of(
            Material.COBBLESTONE,
            Material.STONE,
            Material.COBBLED_DEEPSLATE,
            Material.DEEPSLATE,
            Material.ANDESITE,
            Material.GRANITE
    );

    public TunnelingListener(JavaPlugin plugin, PluginConfig pluginConfig, Enchantment tunneling) {
        this.plugin = plugin;
        this.pluginConfig = pluginConfig;
        this.tunneling = tunneling;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!pluginConfig.isTunnelingEnabled()
                || playersBreakingTunnelBlocks.contains(player.getUniqueId())) {
            return;
        }
        if(player.isSneaking()){
            return;
        }
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tunneling == null || tool.getEnchantmentLevel(tunneling) == 0) {
            return;
        }

        Block origin = event.getBlock();
        Vector direction = player.getEyeLocation().getDirection();
        int dominantAxis = dominantAxis(direction);

        Bukkit.getScheduler().runTask(plugin, () -> breakTunnel(player, origin, dominantAxis));
    }

    private void breakTunnel(Player player, Block origin, int dominantAxis) {
        if (!pluginConfig.isTunnelingEnabled() || !player.isOnline()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        playersBreakingTunnelBlocks.add(playerId);
        try {
            for (int first = -1; first <= 1; first++) {
                for (int second = -1; second <= 1; second++) {
                    if (first == 0 && second == 0) {
                        continue;
                    }

                    Block block = getTunnelBlock(origin, dominantAxis, first, second);
                    if (TUNNELABLE_BLOCKS.contains(block.getType())) {
                        player.breakBlock(block);
                    }
                }
            }
        } finally {
            playersBreakingTunnelBlocks.remove(playerId);
        }
    }

    private int dominantAxis(Vector direction) {
        double x = Math.abs(direction.getX());
        double y = Math.abs(direction.getY());
        double z = Math.abs(direction.getZ());

        if (x >= y && x >= z) {
            return 0;
        }
        if (y >= z) {
            return 1;
        }
        return 2;
    }

    private Block getTunnelBlock(Block origin, int dominantAxis, int first, int second) {
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();

        if (dominantAxis == 0) {
            y += first;
            z += second;
        } else if (dominantAxis == 1) {
            x += first;
            z += second;
        } else {
            x += first;
            y += second;
        }

        return origin.getWorld().getBlockAt(x, y, z);
    }

    public static NamespacedKey getEnchantmentKey() {
        return new NamespacedKey("fockysenchants", "tunneling");
    }
}
