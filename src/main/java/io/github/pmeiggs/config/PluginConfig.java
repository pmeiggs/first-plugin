package io.github.pmeiggs.config;

import org.bukkit.plugin.java.JavaPlugin;


//handles the config

public class PluginConfig{
    public static final long DEFAULT_COMBAT_COOLDOWN_MS = 15000;

    private JavaPlugin plugin;

    public PluginConfig(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void load(){
        plugin.saveDefaultConfig();
    }

    public boolean isPvpEnabled(){
        return plugin.getConfig().getBoolean("pvpEnabled");
    }

    public long getCombatCooldownMs() {
        return plugin.getConfig().getLong("combatCooldownMs", DEFAULT_COMBAT_COOLDOWN_MS);
    }

    public boolean isCrystalPvpEnabled(){
        return plugin.getConfig().getBoolean("crystalPvpEnabled");
    }

    public void setCrystalPvpEnabled(boolean enabled){
        plugin.getConfig().set("crystalPvpEnabled", enabled);
    }

    public boolean isNetheriteSmithingEnabled(){
        return plugin.getConfig().getBoolean("netheriteSmithingEnabled");
    }

    public void setNetheriteSmithingEnabled(boolean enabled){
        plugin.getConfig().set("netheriteSmithingEnabled", enabled);
    }

    public boolean isTunnelingEnabled() {
        return plugin.getConfig().getBoolean("custom-enchantments.tunneling.enabled", true);
    }

    public String getJoinMessagePrefix() {
        return plugin.getConfig().getString("messages.join-prefix");
    }

    public String getJoinMessageSuffix() {
        return plugin.getConfig().getString("messages.join-suffix");
    }

    public void reload(){
        plugin.reloadConfig();
    }
}