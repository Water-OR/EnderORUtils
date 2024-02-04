package io.github.enderor.config;

import com.google.common.collect.Maps;
import io.github.enderor.utils.EnchantsHelper;
import io.github.enderor.utils.NullHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

public class EnchantsMaxLevel {
  private static final Configuration             config          = new Configuration(new File("enchantments_max_level", "config.cfg"));
  private static       boolean                   configPrepared  = false;
  private static final String                    CONFIG_CATEGORY = "enchantments_max_level";
  private static final Map<Enchantment, Integer> MAX_LEVEL       = Maps.newHashMap();
  
  public static int getMaxLevel(Enchantment enchant) { return MAX_LEVEL.get(enchant); }
  
  public static void setMaxLevel(Enchantment enchant, int maxLevel) {
    if (checkLevelLegal(enchant, maxLevel)) {
      MAX_LEVEL.replace(enchant, maxLevel);
    }
    save();
  }
  
  private static Property getEnchantProperty(Enchantment enchant) {
    return config.get(CONFIG_CATEGORY, NullHelper.getRegistryNameString(enchant), enchant.getMaxLevel() > 1 ? enchant.getMaxLevel() + 2 : 1, null, enchant.getMaxLevel(), Integer.MAX_VALUE);
  }
  
  public static void load() {
    if (!EnchantsHelper.hasEnchantsInit()) { return; }
    EnchantsHelper.getEnchantsAppeared().forEach(enchant -> MAX_LEVEL.put(enchant, getEnchantProperty(enchant).getInt()));
    configPrepared = true;
    save();
  }
  
  public static void save() {
    EnchantsHelper.getEnchantsAppeared().forEach(enchant -> {
      Property property = getEnchantProperty(enchant);
      property.set(MAX_LEVEL.getOrDefault(enchant, Integer.parseInt(property.getDefault())));
    });
    config.save();
  }
  
  public static boolean hasConfigPrepared() { return configPrepared; }
  
  public static boolean checkLevelLegal(@NotNull Enchantment enchant, int level) {
    return enchant.getMaxLevel() <= level;
  }
}
