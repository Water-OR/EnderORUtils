package io.github.enderor.recipes;

import org.jetbrains.annotations.NotNull;

public interface IHasRecipe {
  void makeRecipe(@NotNull EnderORRecipesHandler handler);
}
