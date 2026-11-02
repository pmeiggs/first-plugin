package io.github.pmeiggs.config;

import org.bukkit.plugin.java.JavaPlugin;


//handles the config

public class PluginConfig{
    public static final long DEFAULT_COMBAT_COOLDOWN_MS = 15000;
    public static final double DEFAULT_ENCHANTED_BOOK_PRICE_MULTIPLIER = 1.5;

    private JavaPlugin plugin;
    private boolean invalidEnchantedBookPriceMultiplierLogged;

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

    public double getEnchantedBookPriceMultiplier() {
        double multiplier = plugin.getConfig().getDouble(
                "villager-trades.enchanted-book-price-multiplier",
                DEFAULT_ENCHANTED_BOOK_PRICE_MULTIPLIER
        );
        if (!Double.isFinite(multiplier) || multiplier < 1.0) {
            if (!invalidEnchantedBookPriceMultiplierLogged) {
                plugin.getLogger().warning(
                        "Invalid villager-trades.enchanted-book-price-multiplier; using "
                                + DEFAULT_ENCHANTED_BOOK_PRICE_MULTIPLIER + ". The value must be finite and at least 1.0."
                );
                invalidEnchantedBookPriceMultiplierLogged = true;
            }
            return DEFAULT_ENCHANTED_BOOK_PRICE_MULTIPLIER;
        }
        return multiplier;
    }

    public void setEnchantedBookPriceMultiplier(double multiplier) {
        plugin.getConfig().set("villager-trades.enchanted-book-price-multiplier", multiplier);
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

    public int getMaxProtectionLevel() {
    return plugin.getConfig().getInt("max-levels.protection", 3);
    }
    
    public int getMaxSharpnessLevel() {
        return plugin.getConfig().getInt("max-levels.sharpness", 3);
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
