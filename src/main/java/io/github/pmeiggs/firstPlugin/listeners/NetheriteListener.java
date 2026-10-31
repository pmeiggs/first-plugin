package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.config.PluginConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

public class NetheriteListener implements Listener {

    private final PluginConfig pluginConfig;

    public NetheriteListener(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    private boolean isNetherite(ItemStack item) {
        return item != null && item.getType().name().startsWith("NETHERITE_");
    }

    @EventHandler 
    public void onPrepareSmithing(PrepareSmithingEvent event){
        if (pluginConfig.isNetheriteSmithingEnabled()) {
            return;
        }

        ItemStack result = event.getResult();

        if(isNetherite(result)){
            event.setResult(null);
        }
    }

    @EventHandler 
    public void onSmithItem(SmithItemEvent event){
        if (pluginConfig.isNetheriteSmithingEnabled()) {
            return;
        }

        if (isNetherite(event.getCurrentItem())){
            event.setCancelled(true);
        }
    }

    
}
