package io.github.enderor.compat.jei;

import com.google.common.collect.Lists;
import io.github.enderor.recipes.EnderORRecipe;
import io.github.enderor.recipes.ShapedRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderORRecipeWrapper implements IEnderORRecipeWrapper {
  public final EnderORRecipe recipe;
  public final String        categoryUid;
  
  @Contract ("_ -> new")
  public static @NotNull IEnderORRecipeWrapper getWrapper(@NotNull EnderORRecipe recipe) {
    if (recipe instanceof IEnderORRecipeWrapper) {
      return ((IEnderORRecipeWrapper) recipe);
    } else if (recipe instanceof ShapedRecipe) {
      return new ShapedRecipeWrapper((ShapedRecipe) recipe);
    } else {
      return new EnderORRecipeWrapper(recipe);
    }
  }
  
  protected EnderORRecipeWrapper(@NotNull EnderORRecipe recipe) {
    this.recipe = recipe;
    categoryUid = recipe.getCategoryUid();
  }
  
  @Override
  public void getIngredients(@NotNull IIngredients ingredients) {
    List<List<ItemStack>> inputs = Lists.newArrayList();
    recipe.getIngredients().forEach(ingredient -> inputs.add(Lists.newArrayList(ingredient.getMatchingStacks())));
    ingredients.setInputLists(VanillaTypes.ITEM, inputs);
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }
  
  @Override
  public String getCategoryUid() { return categoryUid; }
  
  public static class ShapedRecipeWrapper extends EnderORRecipeWrapper implements IShapedCraftingRecipeWrapper {
    public ShapedRecipeWrapper(@NotNull ShapedRecipe recipe) { super(recipe); }
    
    @Override
    public int getWidth() { return ((ShapedRecipe) recipe).width; }
    
    @Override
    public int getHeight() { return ((ShapedRecipe) recipe).height; }
  }
}
