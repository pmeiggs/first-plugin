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

    public boolean isPvPEnabled(){
        return plugin.getConfig().getBoolean("pvpEnabled");
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