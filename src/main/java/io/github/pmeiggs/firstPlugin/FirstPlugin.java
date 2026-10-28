package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.firstPlugin.listeners.ConnectionListener;
import io.github.pmeiggs.firstPlugin.listeners.CrystalListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class FirstPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        IO.println("[pm] Plugin Started properly");
        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new CrystalListener(), this);
        //hide locator bar
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        IO.println("[pm] Plugin Stopped properly");
    }
}
