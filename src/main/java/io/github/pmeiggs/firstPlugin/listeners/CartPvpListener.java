package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CartPvpListener implements Listener {

    @EventHandler
    public void onCartDamage(EntityDamageByEntityEvent event) {

        if (!(event.getDamager().getType().equals(EntityType.TNT_MINECART))) {
            return;
        }

        //set damage cap of cart at 10 hearts
        if ((event.getFinalDamage() > 20)) {
            event.setDamage(event.getDamage() * (20/event.getFinalDamage()));
        }
    }
}
