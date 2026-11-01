package io.github.pmeiggs.firstPlugin.combat;

import io.github.pmeiggs.config.PluginConfig;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTagTracker {

    private final long combatCooldownMs;
    private final Map<UUID, Long> combatTags = new HashMap<>();

    public CombatTagTracker() {
        this(PluginConfig.DEFAULT_COMBAT_COOLDOWN_MS);
    }

    public CombatTagTracker(PluginConfig pluginConfig) {
        this(pluginConfig.getCombatCooldownMs());
    }

    private CombatTagTracker(long combatCooldownMs) {
        this.combatCooldownMs = combatCooldownMs;
    }

    public void tag(Player player, long now) {
        combatTags.put(player.getUniqueId(), now);
    }

    public long getCombatCooldownMs() {
        return combatCooldownMs;
    }

    public void remove(UUID playerId) {
        combatTags.remove(playerId);
    }

    public boolean isCurrentTag(UUID playerId, long tagTime) {
        return Long.valueOf(tagTime).equals(combatTags.get(playerId));
    }

    public boolean isCombatTagged(Player player) {
        Long tagTime = combatTags.get(player.getUniqueId());
        return tagTime != null && System.currentTimeMillis() - tagTime < combatCooldownMs;
    }
}