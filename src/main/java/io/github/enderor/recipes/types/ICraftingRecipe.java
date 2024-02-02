package io.github.enderor.recipes.types;

import io.github.enderor.compat.jei.IHasSpecialCategory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

public interface ICraftingRecipe extends IHasSpecialCategory {
  @Override
  default String getCategoryUid() { return VanillaRecipeCategoryUid.CRAFTING; }
}
