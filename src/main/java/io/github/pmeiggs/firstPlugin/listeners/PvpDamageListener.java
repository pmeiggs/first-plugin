package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.firstPlugin.commands.PvpToggle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvpDamageListener implements Listener {

    private final PvpToggle pvpToggle;

    public PvpDamageListener(PvpToggle pvpToggle) {
        this.pvpToggle = pvpToggle;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player attacked)) {
            return;
        }
        //check if they have pvp and respond depending on taht
        if (event.getDamager() instanceof Player player) {
            checkPvp(event, player, attacked);
        } else if (event.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof Player shooter) {
            checkPvp(event, shooter, attacked); 
        }
    }

    private void checkPvp(EntityDamageByEntityEvent event, Player attacker, Player attacked) {
        //made booleans to use much easier later on
        boolean attackedHasPvpDisabled = pvpToggle.isPvpDisabled(attacked);
        boolean attackerHasPvpDisabled = pvpToggle.isPvpDisabled(attacker);
        //check if both have pvp on
        if (!attackedHasPvpDisabled && !attackerHasPvpDisabled) {
            return; //return early if both have i ton
        }

        event.setCancelled(true);
        //send corresponding msg
        attacker.sendMessage(Component.text(attackedHasPvpDisabled
                        ? "This player has PvP disabled."
                        : "You have PvP disabled.")
                .color(NamedTextColor.RED));
    }
}
