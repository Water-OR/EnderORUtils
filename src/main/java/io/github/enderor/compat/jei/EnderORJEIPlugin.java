package io.github.enderor.compat.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.enderor.blocks.EnderORBlockHandler;
import io.github.enderor.items.EnderORItemHandler;
import io.github.enderor.items.ItemEnchantedPaper;
import io.github.enderor.items.baubles.ring.ItemPotionRing;
import io.github.enderor.recipes.EnderORRecipesHandler;
import io.github.enderor.utils.NullHelper;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@JEIPlugin
public class EnderORJEIPlugin implements IModPlugin {
  @Override
  public void registerSubtypes(@NotNull ISubtypeRegistry subtypeRegistry) {
    subtypeRegistry.registerSubtypeInterpreter(EnderORItemHandler.ITEM_ENCHANTED_PAPER, stack -> {
      final List<String> result = Lists.newArrayList();
      ItemEnchantedPaper.getEnchants(stack).forEach((enchant, level) -> result.add(NullHelper.getRegistryNameString(enchant) + "%level:" + level));
      result.sort(Comparator.naturalOrder());
      return "ENCHANTED_PAPER:" + result;
    });
    subtypeRegistry.registerSubtypeInterpreter(EnderORItemHandler.ITEM_POTION_RING, stack -> {
      final List<String> result = Lists.newArrayList();
      ItemPotionRing.getEffects(stack).forEach((effect, level) -> result.add(NullHelper.getRegistryNameString(effect) + "%level:" + level));
      result.sort(Comparator.naturalOrder());
      return "POTION_RING:" + result;
    });
  }
  
  @Override
  public void registerIngredients(@NotNull IModIngredientRegistration registry) { }
  
  @Override
  public void registerCategories(@NotNull IRecipeCategoryRegistration registry) { }
  
  @Override
  public void register(@NotNull IModRegistry registry) {
    addEnderORRecipes(registry);
    addItemDescription(registry, new ItemBlock(EnderORBlockHandler.BLOCK_ENCHANT_MOVER));
    addItemDescription(registry, EnderORItemHandler.ITEM_MOD);
    addItemDescription(registry, EnderORItemHandler.ITEM_POTION_RING);
    addItemDescription(registry, EnderORItemHandler.ITEM_ENCHANTED_PAPER);
  }
  
  private static void addEnderORRecipes(@NotNull IModRegistry registry) {
    Map<String, List<IEnderORRecipeWrapper>> wrapperMap = Maps.newHashMap();
    EnderORRecipesHandler.RECIPES.stream().map(EnderORRecipeWrapper::getWrapper).forEach(wrapper -> {
      String categoryUid = wrapper.getCategoryUid();
      if (wrapperMap.containsKey(categoryUid)) {
        wrapperMap.get(categoryUid).add(wrapper);
      } else {
        wrapperMap.put(categoryUid, Lists.newArrayList(wrapper));
      }
    });
    wrapperMap.forEach((categoryUid, wrapperList) -> registry.addRecipes(wrapperList, categoryUid));
  }
  
  @Override
  public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) { }
  
  private static void addItemDescription(@NotNull IModRegistry registry, @NotNull Item item) {
    ResourceLocation registryName = item.getRegistryName();
    if (item instanceof ItemBlock) { registryName = ((ItemBlock) item).getBlock().getRegistryName(); }
    if (registryName == null) { throw new NullPointerException("This item has no registryName. It's not allowed!"); }
    registry.addIngredientInfo(item.getDefaultInstance(), VanillaTypes.ITEM, "jei." + registryName.toString().replace(':', '.'));
  }
}
