package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.config.PluginConfig;
import io.github.pmeiggs.firstPlugin.listeners.ConnectionListener;
import io.github.pmeiggs.firstPlugin.listeners.CrystalListener;
import io.github.pmeiggs.firstPlugin.listeners.WorldNerfListener;
import io.github.pmeiggs.firstPlugin.listeners.CombatListener;

import org.bukkit.plugin.java.JavaPlugin;

public final class FirstPlugin extends JavaPlugin {

    private PluginConfig pluginConfig;

    @Override
    public void onEnable() {
        //Pass plugin config to loisteners
        pluginConfig = new PluginConfig(this);
        pluginConfig.load();

        // Plugin startup logic
        IO.println("[pm] Plugin Started properly");

        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new WorldNerfListener(), this);
        getServer().getPluginManager().registerEvents(new ConnectionListener(pluginConfig), this);

        //if crystal pvp is disabled registerListener 
        if(!pluginConfig.isCrystalPvpEnabled()){
            getServer().getPluginManager().registerEvents(new CrystalListener(), this);
        }
        
        //hide locator bar
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        IO.println("[pm] Plugin Stopped properly");
    }
}
