package io.github.enderor.recipes;

import io.github.enderor.recipes.types.ICraftingRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.IntStream;

public class ShapedRecipe extends EnderORRecipe implements ICraftingRecipe {
  public final int                     width;
  public final int                     height;
  
  public ShapedRecipe(String name, int width, int height, ItemStack output, Ingredient... inputs) {
    super(name, output, inputs);
    this.width  = width;
    this.height = height;
    if (inputs.length > width * height) {
      this.inputs = NonNullList.withSize(width * height, Ingredient.EMPTY);
      IntStream.range(0, width * height).forEachOrdered(i -> this.inputs.set(i, inputs[i] == null ? Ingredient.EMPTY : inputs[i]));
    } else {
      this.inputs = NonNullList.from(Ingredient.EMPTY, inputs);
      IntStream.range(0, width * height - inputs.length).forEach(i -> this.inputs.add(Ingredient.EMPTY));
    }
  }
  
  @Override
  public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
    if (!canFit(inv.getWidth(), inv.getHeight())) { return false; }
    for (int i = 0; i + width <= inv.getWidth(); ++i) {
      for (int j = 0; j + height <= inv.getHeight(); ++j) {
        if (matchSimple(i, j, inv)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean matchSimple(int dx, int dy, @NotNull InventoryCrafting inv) {
    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        if (!getInput(x, y).apply(inv.getStackInRowAndColumn(x + dx, y + dy))) { return false; }
      }
    }
    return true;
  }
  
  @Override
  public boolean canFit(int width, int height) { return this.width <= width && this.height <= height; }
  
  protected Ingredient getInput(int x, int y) { return inputs.get(y * width + x); }
}
