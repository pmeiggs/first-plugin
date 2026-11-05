package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.config.PluginConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class InvisibleKillListener implements Listener {

    private PluginConfig pluginConfig;
    @EventHandler
    public void onInvisibleKill(PlayerDeathEvent event) {
        //check config if invisible kills are enabled
        if(!pluginConfig.isInvisKilslEnabled()) {
            return;
        }
        //hide killer if killer is invisible
        Player killer = event.getEntity().getKiller();
        Player killed = event.getEntity();

        if (killer != null && killer.isInvisible()) {
            event.deathMessage(Component.text(killed.getName() + " was killed by §kinvisible§r."));
        }
    }
}

