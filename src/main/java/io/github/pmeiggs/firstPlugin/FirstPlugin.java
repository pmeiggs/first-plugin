package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.firstPlugin.listeners.ConnectionListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class FirstPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        IO.println("[pm] Plugin Started properly");
        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        IO.println("[pm] Plugin Stopped properly");
    }
}
