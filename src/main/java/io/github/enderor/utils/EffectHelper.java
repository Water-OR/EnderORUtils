package io.github.enderor.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.enderor.EnderORUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class EffectHelper {
  private static final Map<Potion, Set<Integer>> effectsAppeared        = Maps.newHashMap();
  private static final List<PotionType>          multipleEffectsPotions = Lists.newArrayList();
  
  public static Map<Potion, Set<Integer>> getEffectsAppeared() { return effectsAppeared; }
  
  public static List<PotionType> getMultipleEffectsPotions()   { return multipleEffectsPotions; }
  
  private static boolean effectsInit = false;
  
  public static void initAppearedEffects() {
    PotionType.REGISTRY.forEach(potionType -> {
      if (potionType.getEffects().isEmpty()) { return; }
      if (potionType.getEffects().size() > 1) {
        multipleEffectsPotions.add(potionType);
        EnderORUtils.log(Level.INFO, "Found multiple effects potion type: %s!", potionType.getRegistryName());
      } else {
        PotionEffect effect = potionType.getEffects().get(0);
        Potion       potion = effect.getPotion();
        if (!effectsAppeared.containsKey(potion)) { effectsAppeared.put(potion, Sets.newHashSet()); }
        effectsAppeared.get(potion).add(effect.getAmplifier());
        EnderORUtils.log(Level.INFO, "Found single effect potion type: %s! potion: %s, amplifier: %s", potionType.getRegistryName(), potion.getRegistryName(), effect.getAmplifier());
      }
    });
    effectsInit = true;
  }
  
  @Contract ("_, _ -> param1")
  public static Map<Potion, Integer> mergeEffects(Map<Potion, Integer> effects0, @NotNull Map<Potion, Integer> effects1) {
    effects1.forEach((potion, level) -> mergeEffect(effects0, potion, level));
    return effects0;
  }
  
  public static @NotNull Map<Potion, Integer> mergeEffects(@NotNull Map<Potion, Integer> effects0, @NotNull List<PotionEffect> effects1) {
    effects1.forEach(effect -> mergeEffect(effects0, effect));
    return effects0;
  }
  
  @Contract ("_, _, _ -> param1")
  public static @NotNull Map<Potion, Integer> mergeEffect(@NotNull Map<Potion, Integer> effects0, Potion potion, int level) {
    if (!effects0.containsKey(potion)) {
      effects0.put(potion, level);
    } else if (effects0.get(potion) < level) {
      effects0.replace(potion, level);
    } else if (effects0.get(potion) == level) {
      effects0.replace(potion, level + 1);
    }
    return effects0;
  }
  
  @Contract ("_, _ -> param1")
  public static @NotNull Map<Potion, Integer> mergeEffect(@NotNull Map<Potion, Integer> effects0, @NotNull PotionEffect effect) { return mergeEffect(effects0, effect.getPotion(), effect.getAmplifier()); }
  
  public static @NotNull SortedMap<Potion, Integer> sortEffects(@NotNull Map<Potion, Integer> effects) {
    final SortedMap<Potion, Integer> result = new TreeMap<>(Comparator.comparingInt(Potion::getIdFromPotion));
    result.putAll(effects);
    return result;
  }
  
  public static @NotNull String getEffectText(@NotNull Potion potion, int level) {
    TextFormatting color = potion.isBadEffect() ? TextFormatting.RED : TextFormatting.BLUE;
    return color.toString()
                .concat(I18n.format(potion.getName()).trim()).concat(" ")
                .concat(I18n.format("potion.potency." + level).trim());
  }
  
  public static boolean hasEffectsInit() { return effectsInit; }
}
