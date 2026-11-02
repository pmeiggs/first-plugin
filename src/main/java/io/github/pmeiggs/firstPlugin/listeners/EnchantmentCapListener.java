package io.github.pmeiggs.firstPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentCapListener implements Listener {

    //ENCHANTMENT TABLE
    //should modify the enchantment text to ensure it is accurate before rolling
    @EventHandler
      public void onPrepareEnchant(PrepareItemEnchantEvent event) {
          var offers = event.getOffers();
          for (var offer : offers) {
              if (offer != null && offer.getEnchantment() != null) {
                  Enchantment enchant = offer.getEnchantment();
                  if ((enchant.equals(Enchantment.PROTECTION) || enchant.equals(Enchantment.SHARPNESS)) && offer.getEnchantmentLevel() > 3) {
                      offer.setEnchantmentLevel(3);
                  }
              }
          }
      }
  
    //should cap any rolled Protection or Sharpness at Level 3 in the Enchantment Table
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        event.getEnchantsToAdd().replaceAll((enchant, level) -> {
            if ((enchant.equals(Enchantment.PROTECTION) || enchant.equals(Enchantment.SHARPNESS)) && level > 3) {
                return 3;
            }
            return level;
        });
    }


    //ANVIL
    //Should prevent combining books/items to bypass the level 3 cap in an anvil
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result != null) {
            capEnchantment(result, Enchantment.PROTECTION);
            capEnchantment(result, Enchantment.SHARPNESS);
        }
    }

    //VILLAGERS
    //should prevent Librarians from acquiring Prot 4 or Sharp 4/5 book trades
    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        MerchantRecipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        if (result != null && result.hasItemMeta()) {
            ItemMeta meta = result.getItemMeta();

            //check if it's an enchanted book or has stored enchants using meta
            if (result.getType() == Material.ENCHANTED_BOOK || meta instanceof EnchantmentStorageMeta) {
                	org.bukkit.inventory.meta.EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;
                	boolean modified = false;
    
                    for (var entry : bookMeta.getStoredEnchants().entrySet()) {
                        Enchantment enchant = entry.getKey();
                        int level = entry.getValue();
        
                        if ((enchant.equals(Enchantment.PROTECTION) || enchant.equals(Enchantment.SHARPNESS)) && level > 3) {
                            bookMeta.removeStoredEnchant(enchant);
                            bookMeta.addStoredEnchant(enchant, 3, true);
                            modified = true;
                        }
                    }
    
                    if (modified) {
                        ItemStack newResult = result.clone();
                        newResult.setItemMeta(bookMeta);
                        MerchantRecipe newRecipe = new MerchantRecipe(newResult, recipe.getUses(), recipe.getMaxUses(), recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier());
                        newRecipe.setIngredients(recipe.getIngredients());
                        event.setRecipe(newRecipe);
        		    }
            }
        }
    }


    //INVENTORY (also handles fishing/loot chests but is currently misleading)
    //Should double check inventory as a protection measure
    //Will remove inventory related checks later after accounting for all loot chests (nether/end/dungeon) and fishing drops
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        capEnchantment(event.getCursor(), Enchantment.PROTECTION);
        capEnchantment(event.getCursor(), Enchantment.SHARPNESS);
        capEnchantment(event.getCurrentItem(), Enchantment.PROTECTION);
        capEnchantment(event.getCurrentItem(), Enchantment.SHARPNESS);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (ItemStack item : event.getNewItems().values()) {
            capEnchantment(item, Enchantment.PROTECTION);
            capEnchantment(item, Enchantment.SHARPNESS);
        }
    }

  //HELPER FUNCTION
    private void capEnchantment(ItemStack item, Enchantment enchantment) {
        if (item != null) {
            if (item.containsEnchantment(enchantment) && item.getEnchantmentLevel(enchantment) > 3) {
                item.removeEnchantment(enchantment);
                item.addEnchantment(enchantment, 3);
            }
            // Also check stored enchants if it's an enchanted book
            if (item.getItemMeta() instanceof org.bukkit.inventory.meta.EnchantmentStorageMeta storeMeta) {
                if (storeMeta.hasStoredEnchant(enchantment) && storeMeta.getStoredEnchantLevel(enchantment) > 3) {
                    storeMeta.removeStoredEnchant(enchantment);
                    storeMeta.addStoredEnchant(enchantment, 3, true);
                    item.setItemMeta(storeMeta);
                }
            }
        }
    }
    
}
