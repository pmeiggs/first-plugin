package io.github.pmeiggs.config;

import org.bukkit.plugin.java.JavaPlugin;


//handles the config

public class PluginConfig{
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

    public boolean isCrystalPvpEnabled(){
        return plugin.getConfig().getBoolean("crystalPvpEnabled");
    }

    public boolean isNetheriteSmithingEnabled(){
        return plugin.getConfig().getBoolean("netheriteSmithingEnabled");
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