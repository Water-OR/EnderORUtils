package io.github.enderor.recipes;

import io.github.enderor.recipes.types.ICraftingRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ShapelessRecipe extends EnderORRecipe implements ICraftingRecipe {
  
  public ShapelessRecipe(String name, ItemStack output, Ingredient... inputs) {
    super(name, output, inputs);
    this.inputs.removeIf(ingredient -> ingredient == Ingredient.EMPTY);
  }
  
  @Override
  public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
    if (!canFit(inv.getWidth(), inv.getHeight())) { return false; }
    Boolean[] isUsed = new Boolean[getSize()];
    Arrays.fill(isUsed, false);
    int emptyCount = 0;
    for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
      ItemStack stack      = inv.getStackInSlot(i);
      int       matchIndex = -1;
      if (stack.isEmpty()) {
        ++emptyCount;
        continue;
      }
      for (int j = 0; j < getSize(); ++j) {
        if (isUsed[j] || !inputs.get(j).apply(stack)) { continue; }
        matchIndex = j;
        break;
      }
      if (matchIndex < 0) { return false; }
      isUsed[matchIndex] = true;
    }
    return getSize() + emptyCount == inv.getSizeInventory();
  }
}
