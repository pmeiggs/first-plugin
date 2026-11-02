package io.github.pmeiggs.firstPlugin.listeners;

import io.github.pmeiggs.config.PluginConfig;
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

    private final PluginConfig pluginConfig;

    public EnchantmentCapListener(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    //ENCHANTMENT TABLE
    //should modify the enchantment text to ensure it is accurate before rolling
    @EventHandler
      public void onPrepareEnchant(PrepareItemEnchantEvent event) {
          int maxProt = pluginConfig.getMaxProtectionLevel();
          int maxSharp = pluginConfig.getMaxSharpnessLevel();
          
          var offers = event.getOffers();
          for (var offer : offers) {
              if (offer != null && offer.getEnchantment() != null) {
                  Enchantment enchant = offer.getEnchantment();
                if (enchant.equals(Enchantment.PROTECTION) && offer.getEnchantmentLevel() > maxProt) {
                    offer.setEnchantmentLevel(maxProt);
                } else if (enchant.equals(Enchantment.SHARPNESS) && offer.getEnchantmentLevel() > maxSharp) {
                    offer.setEnchantmentLevel(maxSharp);
                }
              }
          }
      }
  
    //should cap any rolled Protection or Sharpness at Level 3 in the Enchantment Table
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        int maxProt = pluginConfig.getMaxProtectionLevel();
        int maxSharp = pluginConfig.getMaxSharpnessLevel();
        event.getEnchantsToAdd().replaceAll((enchant, level) -> {
            if (enchant.equals(Enchantment.PROTECTION) && level > maxProt) {
                return maxProt;
            }
            if (enchant.equals(Enchantment.SHARPNESS) && level > maxSharp) {
                return maxSharp;
            }
            return level;
        });
    }


    //ANVIL
    //Should prevent combining books/items to bypass the level 3 cap in an anvil
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        int maxProt = pluginConfig.getMaxProtectionLevel();
        int maxSharp = pluginConfig.getMaxSharpnessLevel();
        
        ItemStack result = event.getResult();
        if (result != null) {
            capEnchantment(result, Enchantment.PROTECTION, maxProt);
            capEnchantment(result, Enchantment.SHARPNESS, maxSharp);
        }
    }

    //VILLAGERS
    //should prevent Librarians from acquiring Prot 4 or Sharp 4/5 book trades
    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        int maxProt = pluginConfig.getMaxProtectionLevel();
        int maxSharp = pluginConfig.getMaxSharpnessLevel();
        
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
        
                        if (enchant.equals(Enchantment.PROTECTION) && level > maxProt) {
                            bookMeta.removeStoredEnchant(enchant);
                            bookMeta.addStoredEnchant(enchant, maxProt, true);
                            modified = true;
                        } else if (enchant.equals(Enchantment.SHARPNESS) && level > maxSharp) {
                            bookMeta.removeStoredEnchant(enchant);
                            bookMeta.addStoredEnchant(enchant, maxSharp, true);
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
        int maxProt = pluginConfig.getMaxProtectionLevel();
        int maxSharp = pluginConfig.getMaxSharpnessLevel();
        
        capEnchantment(event.getCursor(), Enchantment.PROTECTION, maxProt);
        capEnchantment(event.getCursor(), Enchantment.SHARPNESS, maxSharp);
        capEnchantment(event.getCurrentItem(), Enchantment.PROTECTION, maxProt);
        capEnchantment(event.getCurrentItem(), Enchantment.SHARPNESS, maxSharp);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        int maxProt = pluginConfig.getMaxProtectionLevel();
        int maxSharp = pluginConfig.getMaxSharpnessLevel();

        for (ItemStack item : event.getNewItems().values()) {
            capEnchantment(item, Enchantment.PROTECTION, maxProt);
            capEnchantment(item, Enchantment.SHARPNESS, maxSharp);
        }
    }

  //HELPER FUNCTION
    private void capEnchantment(ItemStack item, Enchantment enchantment, int maxLevel) {
        if (item != null) {
            if (item.containsEnchantment(enchantment) && item.getEnchantmentLevel(enchantment) > maxLevel) {
                item.removeEnchantment(enchantment);
                item.addEnchantment(enchantment, maxLevel);
            }
            if (item.getItemMeta() instanceof EnchantmentStorageMeta storeMeta) {
                if (storeMeta.hasStoredEnchant(enchantment) && storeMeta.getStoredEnchantLevel(enchantment) > maxLevel) {
                    storeMeta.removeStoredEnchant(enchantment);
                    storeMeta.addStoredEnchant(enchantment, maxLevel, true);
                    item.setItemMeta(storeMeta);
                }
            }
        }
    }
    
}
