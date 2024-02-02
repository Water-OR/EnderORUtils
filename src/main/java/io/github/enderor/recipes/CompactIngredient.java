package io.github.enderor.recipes;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CompactIngredient extends Ingredient {
  public final Ingredient[] ingredients;
  public final boolean      isSimple;
  protected    IntList[]    ingredientsItemIds;
  protected    IntList      itemIds = null;
  public final int          size;
  
  public CompactIngredient(Ingredient... ingredients) {
    super(0);
    this.ingredients   = ingredients;
    isSimple           = Arrays.stream(ingredients).allMatch(Ingredient::isSimple);
    size               = ingredients.length;
    ingredientsItemIds = new IntArrayList[size];
  }
  
  @Override
  public ItemStack @NotNull [] getMatchingStacks() {
    return Arrays.stream(ingredients).flatMap(ingredient -> Arrays.stream(ingredient.getMatchingStacks())).toArray(ItemStack[]::new);
  }
  
  @Override
  public boolean apply(@Nullable ItemStack p_apply_1_) {
    return Arrays.stream(ingredients).anyMatch(ingredient -> ingredient.apply(p_apply_1_));
  }
  
  @Override
  public @NotNull IntList getValidItemStacksPacked() {
    boolean hasIngredientsItemIdChanged = false;
    int     ingredientItemIdsSize        = 0;
    
    for (int i = 0; i < size; ++i) {
      IntList ingredientIItemIds = ingredients[i].getValidItemStacksPacked();
      if (ingredientIItemIds != ingredientsItemIds[i]) {
        hasIngredientsItemIdChanged = true;
        ingredientsItemIds[i]       = ingredientIItemIds;
      }
      ingredientItemIdsSize += ingredientIItemIds.size();
    }
    
    if (itemIds == null || hasIngredientsItemIdChanged) {
      itemIds = new IntArrayList(ingredientItemIdsSize);
      for (int i = 0; i < size; ++i) { itemIds.addAll(ingredientsItemIds[i]); }
    }
    return itemIds;
  }
  
  @Override
  protected void invalidate() { itemIds = null; }
  
  @Override
  public boolean isSimple() { return isSimple; }
}
