package io.github.pmeiggs.firstPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class FirstPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        IO.println("plugin started");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        IO.println("plugin stopped");
    }
}
