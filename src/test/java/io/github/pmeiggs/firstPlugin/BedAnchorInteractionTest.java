package io.github.pmeiggs.firstPlugin;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import static org.junit.jupiter.api.Assertions.*;

class BedAnchorInteractionTest {
    private FirstPlugin plugin;
    private PlayerMock player;
    private WorldMock world;

    @BeforeEach void setup() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
        plugin.getConfig().set("crystalPvpEnabled", false);
        player = MockBukkit.getMock().addPlayer();
        world = (WorldMock) player.getWorld();
    }

    @AfterEach void cleanup() { MockBukkit.unmock(); }

    private Block bed(Material material) {
        Block block = world.getBlockAt(0, 64, 0);
        block.setType(material);
        return block;
    }

    private Block anchor(int charges) {
        Block block = bed(Material.RESPAWN_ANCHOR);
        RespawnAnchor data = (RespawnAnchor) block.getBlockData();
        data.setCharges(charges);
        block.setBlockData(data);
        return block;
    }

    private PlayerInteractEvent event(Block block, Action action, EquipmentSlot hand, Material item) {
        ItemStack held = new ItemStack(item);
        if (hand == EquipmentSlot.HAND) player.getInventory().setItemInMainHand(held);
        else player.getInventory().setItemInOffHand(held);
        var event = new PlayerInteractEvent(player, action, held, block, BlockFace.UP, hand);
        event.setUseInteractedBlock(Event.Result.DEFAULT);
        event.setUseItemInHand(Event.Result.DEFAULT);
        return event;
    }

    private PlayerInteractEvent click(Block block, EquipmentSlot hand, Material item) {
        var event = event(block, Action.RIGHT_CLICK_BLOCK, hand, item);
        plugin.getServer().getPluginManager().callEvent(event);
        return event;
    }

    private void assertBlocked(PlayerInteractEvent event) {
        assertEquals(Event.Result.DENY, event.useInteractedBlock());
        assertEquals(Event.Result.DENY, event.useItemInHand());
    }

    private void assertAllowed(PlayerInteractEvent event) {
        assertNotEquals(Event.Result.DENY, event.useInteractedBlock());
        assertNotEquals(Event.Result.DENY, event.useItemInHand());
    }

    @Test void allBedColoursAreBlockedInNetherAndEndForBothHands() {
        for (World.Environment environment : new World.Environment[]{World.Environment.NETHER, World.Environment.THE_END}) {
            world.setEnvironment(environment);
            for (Material material : Tag.BEDS.getValues()) {
                Block bed = bed(material);
                for (EquipmentSlot hand : new EquipmentSlot[]{EquipmentSlot.HAND, EquipmentSlot.OFF_HAND}) {
                    assertBlocked(click(bed, hand, Material.AIR));
                    assertEquals(material, bed.getType());
                }
            }
        }
    }

    @Test void overworldBedsAndNetherAnchorsRemainUsable() {
        world.setEnvironment(World.Environment.NORMAL);
        assertAllowed(click(bed(Material.RED_BED), EquipmentSlot.HAND, Material.AIR));
        world.setEnvironment(World.Environment.NETHER);
        for (int charges = 0; charges <= 4; charges++) {
            assertAllowed(click(anchor(charges), EquipmentSlot.HAND, Material.AIR));
            assertAllowed(click(anchor(charges), EquipmentSlot.HAND, Material.GLOWSTONE));
        }
    }

    @Test void chargedAnchorsCannotActivateOutsideNetherWithEitherHand() {
        for (World.Environment environment : new World.Environment[]{World.Environment.NORMAL, World.Environment.THE_END}) {
            world.setEnvironment(environment);
            for (int charges = 1; charges <= 4; charges++) {
                Block anchor = anchor(charges);
                for (EquipmentSlot hand : new EquipmentSlot[]{EquipmentSlot.HAND, EquipmentSlot.OFF_HAND}) {
                    assertBlocked(click(anchor, hand, Material.AIR));
                    assertEquals(charges, ((RespawnAnchor) anchor.getBlockData()).getCharges());
                }
            }
        }
    }

    @Test void chargingIsAllowedOutsideNetherUntilAnchorIsFull() {
        for (World.Environment environment : new World.Environment[]{World.Environment.NORMAL, World.Environment.THE_END}) {
            world.setEnvironment(environment);
            for (int charges = 0; charges < 4; charges++) {
                for (EquipmentSlot hand : new EquipmentSlot[]{EquipmentSlot.HAND, EquipmentSlot.OFF_HAND}) {
                    assertAllowed(click(anchor(charges), hand, Material.GLOWSTONE));
                }
            }
            for (EquipmentSlot hand : new EquipmentSlot[]{EquipmentSlot.HAND, EquipmentSlot.OFF_HAND}) {
                assertBlocked(click(anchor(4), hand, Material.GLOWSTONE));
            }
        }
    }

    @Test void mainHandCanDeferToOffHandGlowstoneButNotWhenAnchorIsFull() {
        world.setEnvironment(World.Environment.NORMAL);
        player.getInventory().setItemInOffHand(new ItemStack(Material.GLOWSTONE));
        assertAllowed(click(anchor(1), EquipmentSlot.HAND, Material.STICK));
        assertAllowed(click(anchor(1), EquipmentSlot.OFF_HAND, Material.GLOWSTONE));
        assertBlocked(click(anchor(4), EquipmentSlot.HAND, Material.STICK));
    }

    @Test void unchargedAnchorsDoNotNeedProtection() {
        for (World.Environment environment : new World.Environment[]{World.Environment.NORMAL, World.Environment.THE_END}) {
            world.setEnvironment(environment);
            assertAllowed(click(anchor(0), EquipmentSlot.HAND, Material.AIR));
        }
    }

    @Test void sneakingWithItemsAllowsBuildingButEmptyHandSneakingIsProtected() {
        player.setSneaking(true);
        world.setEnvironment(World.Environment.NETHER);
        assertAllowed(click(bed(Material.RED_BED), EquipmentSlot.HAND, Material.STONE));
        player.getInventory().clear();
        assertBlocked(click(bed(Material.RED_BED), EquipmentSlot.HAND, Material.AIR));
        world.setEnvironment(World.Environment.NORMAL);
        assertAllowed(click(anchor(4), EquipmentSlot.HAND, Material.STONE));
        player.getInventory().clear();
        assertBlocked(click(anchor(4), EquipmentSlot.HAND, Material.AIR));
    }

    @Test void itemUseDenialDoesNotSkipProtectionForAnOtherwiseAllowedBlockUse() {
        world.setEnvironment(World.Environment.NETHER);
        var event = event(bed(Material.RED_BED), Action.RIGHT_CLICK_BLOCK, EquipmentSlot.HAND, Material.AIR);
        event.setUseItemInHand(Event.Result.DENY);
        plugin.getServer().getPluginManager().callEvent(event);
        assertBlocked(event);
    }

    @Test void priorCancellationIsPreserved() {
        world.setEnvironment(World.Environment.NETHER);
        var event = event(bed(Material.RED_BED), Action.RIGHT_CLICK_BLOCK, EquipmentSlot.HAND, Material.AIR);
        event.setUseInteractedBlock(Event.Result.DENY);
        plugin.getServer().getPluginManager().callEvent(event);
        assertEquals(Event.Result.DENY, event.useInteractedBlock());
        assertEquals(Event.Result.DEFAULT, event.useItemInHand());
    }

    @Test void normalPlacementLeftClicksAndUnrelatedBlocksAreUnaffected() {
        world.setEnvironment(World.Environment.NETHER);
        assertAllowed(click(bed(Material.STONE), EquipmentSlot.HAND, Material.RED_BED));
        assertAllowed(click(bed(Material.STONE), EquipmentSlot.HAND, Material.RESPAWN_ANCHOR));
        var leftClick = event(bed(Material.RED_BED), Action.LEFT_CLICK_BLOCK, EquipmentSlot.HAND, Material.AIR);
        plugin.getServer().getPluginManager().callEvent(leftClick);
        assertAllowed(leftClick);
    }

    @Test void enablingCrystalPvpRestoresVanillaInteraction() {
        plugin.getConfig().set("crystalPvpEnabled", true);
        world.setEnvironment(World.Environment.NETHER);
        assertAllowed(click(bed(Material.RED_BED), EquipmentSlot.HAND, Material.AIR));
        world.setEnvironment(World.Environment.NORMAL);
        assertAllowed(click(anchor(4), EquipmentSlot.HAND, Material.AIR));
    }
}
