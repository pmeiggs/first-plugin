package io.github.pmeiggs.firstPlugin.commands;

import io.github.pmeiggs.firstPlugin.combat.CombatTagTracker;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class PvpToggle implements BasicCommand {
    //create key in pdc
    private final NamespacedKey pvpDisabledKey;
    private final CombatTagTracker combatTagTracker;

    public PvpToggle(JavaPlugin plugin) {
        this(plugin, new CombatTagTracker());
    }

    public PvpToggle(JavaPlugin plugin, CombatTagTracker combatTagTracker) {
        this.pvpDisabledKey = new NamespacedKey(plugin, "pvp_disabled");
        this.combatTagTracker = combatTagTracker;
    }
    //get the byte of the key
    public boolean isPvpDisabled(Player player) {
        return player.getPersistentDataContainer().getOrDefault(pvpDisabledKey, PersistentDataType.BYTE, (byte) 0) == 1;
    }

    public boolean playerIsInPvp(Player player) {
        return combatTagTracker.isCombatTagged(player);
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            return;
        }
        if (playerIsInPvp(player)) {
            player.sendMessage(Component.text("You cannot toggle PvP while combat tagged.")
                    .color(NamedTextColor.RED));
            return;
        }
        boolean nowDisabled = !isPvpDisabled(player); //basically check fi the command was run before by keeping count of it kinda
        if (nowDisabled) { //set to 1 if pvp off, remove otherwise
            player.getPersistentDataContainer().set(pvpDisabledKey, PersistentDataType.BYTE, (byte) 1);
        } else {
            player.getPersistentDataContainer().remove(pvpDisabledKey);
        }
        //send msg as feedback for when pvp is on or off
        player.sendMessage(Component.text(nowDisabled ? "PvP is now off." : "PvP is now on.")
                .color(NamedTextColor.GRAY)
                .decorate(TextDecoration.BOLD));
    }
}
