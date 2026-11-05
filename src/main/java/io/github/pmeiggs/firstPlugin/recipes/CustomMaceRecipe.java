package io.github.pmeiggs.firstPlugin.recipes;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomMaceRecipe {
    //remove old recipe
    private final JavaPlugin plugin;

    public CustomMaceRecipe(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void removeRecipe(){
        NamespacedKey maceKey = new NamespacedKey("minecraft", "mace");
        plugin.getServer().removeRecipe(maceKey);

    }

    public void addRecipe(){
        NamespacedKey key = new NamespacedKey(plugin, "custom_mace");

        ItemStack result = ItemStack.of(Material.MACE);
        result.setData(DataComponentTypes.ITEM_NAME, Component.text("Mace"));

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(
                "ABA",
                "ECE",
                "ADA"
        );
        recipe.setIngredient('A', Material.NETHER_STAR);
        recipe.setIngredient('B', Material.DRAGON_EGG);
        recipe.setIngredient('C', Material.HEAVY_CORE);
        recipe.setIngredient('D', Material.BREEZE_ROD);
        recipe.setIngredient('E', Material.NETHERITE_INGOT);
        //might be a little hard to get xd?
        this.plugin.getServer().addRecipe(recipe);
    }
}
