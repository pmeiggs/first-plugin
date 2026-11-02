package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.config.PluginConfig;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class LibrarianBookPriceListener implements Listener {
    private static final int MAX_EMERALDS = 64;
    private final PluginConfig pluginConfig;

    public LibrarianBookPriceListener(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!(event.getEntity() instanceof Villager villager)
                || villager.getProfession() != Villager.Profession.LIBRARIAN) {
            return;
        }

        MerchantRecipe recipe = event.getRecipe();
        if (recipe.getResult().getType() != Material.ENCHANTED_BOOK) {
            return;
        }

        List<ItemStack> ingredients = new ArrayList<>(recipe.getIngredients().size());
        boolean priceChanged = false;
        for (ItemStack ingredient : recipe.getIngredients()) {
            ItemStack adjusted = new ItemStack(ingredient);
            if (adjusted.getType() == Material.EMERALD) {
                int increasedAmount = (int) Math.min(
                        MAX_EMERALDS,
                        Math.ceil(adjusted.getAmount() * pluginConfig.getEnchantedBookPriceMultiplier())
                );
                priceChanged |= increasedAmount != adjusted.getAmount();
                adjusted.setAmount(increasedAmount);
            }
            ingredients.add(adjusted);
        }

        if (priceChanged) {
            MerchantRecipe adjustedRecipe = new MerchantRecipe(recipe);
            adjustedRecipe.setIngredients(ingredients);
            event.setRecipe(adjustedRecipe);
        }
    }
}
