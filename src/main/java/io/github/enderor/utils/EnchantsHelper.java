package io.github.enderor.utils;

import com.google.common.collect.Lists;
import io.github.enderor.config.EnchantsMaxLevel;
import net.minecraft.enchantment.Enchantment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EnchantsHelper {
  private static final List<Enchantment> enchantsAppeared = Lists.newArrayList();
  private static       boolean           enchantsInit     = false;
  
  /**
   * Merge all the enchants from enchant1 to enchant0
   *
   * @param enchant0 enchant0
   * @param enchant1 enchant1
   *
   * @return enchant0 after merge, enchant0 will also be changed
   */
  public static Map<Enchantment, Integer> mergeEnchants(Map<Enchantment, Integer> enchant0, @NotNull Map<Enchantment, Integer> enchant1) {
    enchant1.forEach((enchant, level) -> mergeEnchant(enchant0, enchant, level));
    return enchant0;
  }
  
  @Contract ("_, _, _ -> param1")
  public static @NotNull Map<Enchantment, Integer> mergeEnchant(@NotNull Map<Enchantment, Integer> enchants, Enchantment enchant, int level) {
    if (!enchants.containsKey(enchant)) {
      enchants.put(enchant, level);
    } else if (enchants.get(enchant) < level) {
      enchants.replace(enchant, level);
    } else if (enchants.get(enchant) == level) {
      enchants.replace(enchant, Math.min(level + 1, EnchantsMaxLevel.getMaxLevel(enchant)));
    }
    return enchants;
  }
  
  @Contract ("_, _ -> param1")
  public static @NotNull Map<Enchantment, Integer> removeEnchants(@NotNull Map<Enchantment, Integer> enchants0, @NotNull Collection<Enchantment> enchants1) {
    enchants1.forEach(enchants0::remove);
    return enchants0;
  }
  
  public static @NotNull Map<Enchantment, Integer> removeEnchant(@NotNull Map<Enchantment, Integer> enchants, Enchantment enchant) {
    enchants.remove(enchant);
    return enchants;
  }
  
  public static @NotNull SortedMap<Enchantment, Integer> sortEnchants(Map<Enchantment, Integer> enchants) {
    final SortedMap<Enchantment, Integer> result = new TreeMap<>(Comparator.comparingInt(Enchantment::getEnchantmentID));
    result.putAll(enchants);
    return result;
  }
  
  public static boolean hasEnchantsInit() { return enchantsInit; }
  
  public static void initAppearedEnchantments() {
    Enchantment.REGISTRY.forEach(enchantment -> enchantsAppeared.add(NullHelper.checkNull(enchantment)));
    enchantsAppeared.sort(Comparator.comparingInt(Enchantment::getEnchantmentID));
    enchantsInit = true;
    EnchantsMaxLevel.load();
  }
  
  public static List<Enchantment> getEnchantsAppeared() { return enchantsAppeared; }
}
