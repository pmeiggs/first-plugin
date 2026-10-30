package io.github.pmeiggs.firstPlugin.listeners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

public class NetheriteListener implements Listener {

    private boolean isNetherite(ItemStack item) {
        return item != null && item.getType().name().startsWith("NETHERITE_");
    }

    @EventHandler 
    public void onPrepareSmithing(PrepareSmithingEvent event){
        ItemStack result = event.getResult();

        if(isNetherite(result)){
            event.setResult(null);
        }
    }

    @EventHandler 
    public void onSmithItem(SmithItemEvent event){
        if (isNetherite(event.getCurrentItem())){
            event.setCancelled(true);
        }
    }

    
}
