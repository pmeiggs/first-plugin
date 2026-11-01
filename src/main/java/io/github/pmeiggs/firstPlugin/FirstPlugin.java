package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.config.PluginConfig;
import io.github.pmeiggs.firstPlugin.commands.HealCommand;
import io.github.pmeiggs.firstPlugin.commands.PvpToggle;
import io.github.pmeiggs.firstPlugin.listeners.ConnectionListener;
import io.github.pmeiggs.firstPlugin.listeners.CrystalListener;
import io.github.pmeiggs.firstPlugin.listeners.NetheriteListener;
import io.github.pmeiggs.firstPlugin.listeners.WorldNerfListener;
import io.github.pmeiggs.firstPlugin.listeners.CombatListener;
import io.github.pmeiggs.firstPlugin.listeners.PvpDamageListener;
import io.github.pmeiggs.firstPlugin.listeners.TunnelingListener;

import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public class FirstPlugin extends JavaPlugin {

    private PluginConfig pluginConfig;

    @Override
    public void onEnable() {
        //Pass plugin config to loisteners
        pluginConfig = new PluginConfig(this);
        pluginConfig.load();

        // Plugin startup logic
        IO.println("[pm] Plugin Started properly");

        getServer().getPluginManager().registerEvents(new CombatListener(this, new PvpToggle(this)), this);
        getServer().getPluginManager().registerEvents(new WorldNerfListener(), this);
        getServer().getPluginManager().registerEvents(new ConnectionListener(pluginConfig), this);

       

        getServer().getPluginManager().registerEvents(new CrystalListener(pluginConfig), this);
        getServer().getPluginManager().registerEvents(new NetheriteListener(pluginConfig), this);

        //register pvp toggle command
        PvpToggle pvpToggle = new PvpToggle(this);
        registerCommand("togglepvp", pvpToggle);
        getServer().getPluginManager().registerEvents(new PvpDamageListener(pvpToggle), this);

        //register heal command
        registerCommand("heal", new HealCommand());

        //hide locator bar

        loadCustomEnchants();
        
    }


    public void loadCustomEnchants(){

        Enchantment tunneling = Registry.ENCHANTMENT.get(TunnelingListener.getEnchantmentKey());
        if (tunneling == null) {
            getLogger().warning("Datapack enchantment fockysenchants:tunneling was not loaded; tunneling will be inactive.");
        }
        getServer().getPluginManager().registerEvents(new TunnelingListener(this, pluginConfig, tunneling), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        IO.println("[pm] Plugin Stopped properly");
    }
}
