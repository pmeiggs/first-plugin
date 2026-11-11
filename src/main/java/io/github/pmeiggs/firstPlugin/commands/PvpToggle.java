package io.github.pmeiggs.firstPlugin.commands;

import io.github.pmeiggs.firstPlugin.combat.CombatTagTracker;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class PvpToggle implements BasicCommand {
    private static final long TOGGLE_COOLDOWN_MS = 5L * 24 * 60 * 60 * 1000;

    private final NamespacedKey pvpDisabledKey;
    private final NamespacedKey lastToggleKey;
    private final CombatTagTracker combatTagTracker;

    public PvpToggle(JavaPlugin plugin) {
        this(plugin, new CombatTagTracker());
    }

    public PvpToggle(JavaPlugin plugin, CombatTagTracker combatTagTracker) {
        this.pvpDisabledKey = new NamespacedKey(plugin, "pvp_disabled");
        this.lastToggleKey = new NamespacedKey(plugin, "pvp_toggle_last_used");
        this.combatTagTracker = combatTagTracker;
    }

    public boolean isPvpDisabled(Player player) {
        return player.getPersistentDataContainer().getOrDefault(pvpDisabledKey, PersistentDataType.BYTE, (byte) 0) == 1;
    }

    public boolean playerIsInPvp(Player player) {
        return combatTagTracker.isCombatTagged(player);
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reset")) {
                resetCooldown(source, args);
            } else { //fallback if wrong usaghe
                source.getSender().sendMessage(Component.text("Usage: /togglepvp [reset <player>]"));
            }
            return;
        }

        if (!(source.getExecutor() instanceof Player player)) {
            return;
        }
        if (playerIsInPvp(player)) {
            player.sendMessage(Component.text("You cannot toggle PvP while combat tagged.")
                    .color(NamedTextColor.RED));
            return;
        }

        long now = System.currentTimeMillis();
        long lastToggle = player.getPersistentDataContainer()
                .getOrDefault(lastToggleKey, PersistentDataType.LONG, 0L);
        long remaining = lastToggle + TOGGLE_COOLDOWN_MS - now;
        if (remaining > 0) { //set cooldown for command
            player.sendMessage(Component.text("You can toggle PvP again in " + formatRemaining(remaining) + ".")
                    .color(NamedTextColor.RED));
            return;
        }

        boolean nowDisabled = !isPvpDisabled(player); //basically check fi the command was run before by keeping count of it kinda
        if (nowDisabled) { //set to 1 if pvp off, remove otherwise
            player.getPersistentDataContainer().set(pvpDisabledKey, PersistentDataType.BYTE, (byte) 1);
        } else {
            player.getPersistentDataContainer().remove(pvpDisabledKey);
        }
        player.getPersistentDataContainer().set(lastToggleKey, PersistentDataType.LONG, now);
        //send msg as feedback for when pvp is on or off
        player.sendMessage(Component.text(nowDisabled ? "PvP is now off." : "PvP is now on.")
                .color(NamedTextColor.GRAY)
                .decorate(TextDecoration.BOLD));
    }
    //fortmat tikme remainiong on cd to send in playuer msg
    private String formatRemaining(long remainingMs) {
        long minutes = (remainingMs + 59_999) / 60_000;
        long days = minutes / (24 * 60);
        long hours = (minutes % (24 * 60)) / 60;
        long remainingMinutes = minutes % 60;
        if (days > 0) {
            return days + "d " + hours + "h";
        }
        if (hours > 0) {
            return hours + "h " + remainingMinutes + "m";
        }
        return remainingMinutes + "m";
    }
    //reset the cooldown for someone, moslty for admins testing or fixing stuff
    private void resetCooldown(CommandSourceStack source, String[] args) {
        if (!source.getSender().hasPermission("firstplugin.togglepvp.reset")) {//gonna use luckperms so
            source.getSender().sendMessage(Component.text("You don't have permission to reset PvP cooldowns.")
                    .color(NamedTextColor.RED));
            return;
        }

        if (args.length != 2) { //fallback forr correctr usage if args not 2
            source.getSender().sendMessage(Component.text("Usage: /togglepvp reset <player>"));
            return;
        }
        //fallbvack for arg 2
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            source.getSender().sendMessage(Component.text("That player must be online."));
            return;
        }

        target.getPersistentDataContainer().remove(lastToggleKey);
        source.getSender().sendMessage(Component.text("Reset the PvP toggle cooldown for " + target.getName() + "."));
    }

}
