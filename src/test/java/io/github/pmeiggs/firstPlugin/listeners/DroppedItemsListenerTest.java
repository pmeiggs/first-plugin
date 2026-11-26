package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.firstPlugin.FirstPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DroppedItemsListenerTest {

    private FirstPlugin plugin;
    private NamespacedKey pvpDisabledKey;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
        pvpDisabledKey = new NamespacedKey(plugin, "pvp_disabled");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void pvpOffDropIsTaggedAndBlockedForPvpEnabledPlayer() {
        Player playerOff = MockBukkit.getMock().addPlayer();
        Player playerOn = MockBukkit.getMock().addPlayer();
        setPvpDisabled(playerOff, true);

        Item droppedItem = dropItem(playerOff);
        EntityPickupItemEvent pickupEvent = new EntityPickupItemEvent(playerOn, droppedItem, 0);

        plugin.getServer().getPluginManager().callEvent(pickupEvent);

        assertEquals((byte) 1, droppedItem.getPersistentDataContainer().get(
                new NamespacedKey(plugin, "dropped_with_pvp_off"),
                PersistentDataType.BYTE
        ));
        assertTrue(pickupEvent.isCancelled());
    }

    @Test
    void pvpDisabledPlayerCanPickUpDropMarkedAsPvpOff() {
        Player playerOff = MockBukkit.getMock().addPlayer();
        Player recipientOff = MockBukkit.getMock().addPlayer();
        setPvpDisabled(playerOff, true);
        setPvpDisabled(recipientOff, true);

        Item droppedItem = dropItem(playerOff);
        EntityPickupItemEvent pickupEvent = new EntityPickupItemEvent(recipientOff, droppedItem, 0);

        plugin.getServer().getPluginManager().callEvent(pickupEvent);

        assertFalse(pickupEvent.isCancelled());
    }

    @Test
    void pvpEnabledPlayerCanPickUpOrdinaryDrop() {
        Player playerOn = MockBukkit.getMock().addPlayer();
        Player recipientOn = MockBukkit.getMock().addPlayer();

        Item droppedItem = dropItem(playerOn);
        EntityPickupItemEvent pickupEvent = new EntityPickupItemEvent(recipientOn, droppedItem, 0);

        plugin.getServer().getPluginManager().callEvent(pickupEvent);

        assertFalse(pickupEvent.isCancelled());
    }

    private Item dropItem(Player player) {
        Item item = player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.APPLE));
        plugin.getServer().getPluginManager().callEvent(new PlayerDropItemEvent(player, item));
        return item;
    }

    private void setPvpDisabled(Player player, boolean disabled) {
        if (disabled) {
            player.getPersistentDataContainer().set(pvpDisabledKey, PersistentDataType.BYTE, (byte) 1);
        } else {
            player.getPersistentDataContainer().remove(pvpDisabledKey);
        }
    }
}
