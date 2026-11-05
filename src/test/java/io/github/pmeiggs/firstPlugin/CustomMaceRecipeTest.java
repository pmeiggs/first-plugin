package io.github.pmeiggs.firstPlugin;

import io.github.pmeiggs.firstPlugin.recipes.CustomMaceRecipe;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomMaceRecipeTest {

    private FirstPlugin plugin;
    private CustomMaceRecipe customMaceRecipe;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FirstPlugin.class);
        customMaceRecipe = new CustomMaceRecipe(plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void removeRecipeRemovesMinecraftMaceRecipe() {
        customMaceRecipe.removeRecipe();

        Recipe recipe = plugin.getServer().getRecipe(new NamespacedKey("minecraft", "mace"));
        assertNull(recipe);
    }

    @Test
    void addRecipeRegistersCustomMaceRecipe() {
        customMaceRecipe.addRecipe();

        Recipe recipe = plugin.getServer().getRecipe(new NamespacedKey(plugin, "custom_mace"));
        assertNotNull(recipe);
        ItemStack result = recipe.getResult();

        assertEquals(Material.MACE, result.getType());
    }
}
