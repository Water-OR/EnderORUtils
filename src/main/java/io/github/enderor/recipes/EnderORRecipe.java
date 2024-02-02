package io.github.enderor.recipes;

import io.github.enderor.EnderORUtils;
import io.github.enderor.compat.jei.IHasSpecialCategory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class EnderORRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe, IHasSpecialCategory {
  public final ItemStack               output;
  protected    NonNullList<Ingredient> inputs;
  public       String                  group;
  
  public int getSize()                                                      { return inputs.size(); }
  
  public EnderORRecipe(String name, ItemStack output, Ingredient... inputs) { this(name, output, NonNullList.from(Ingredient.EMPTY, inputs)); }
  
  protected EnderORRecipe(String name, ItemStack output, @NotNull NonNullList<Ingredient> inputs) {
    ResourceLocation registryName = new ResourceLocation(EnderORUtils.MOD_ID, name);
    setGroup(registryName.toString()).setRegistryName(registryName);
    this.inputs = inputs;
    this.output = output;
  }
  
  @Override
  public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
    return canFit(inv.getWidth(), inv.getHeight());
  }
  
  @Override
  public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) { return output.copy(); }
  
  @Override
  public boolean canFit(int width, int height) { return getSize() <= width * height; }
  
  @Override
  public @NotNull ItemStack getRecipeOutput() { return output; }
  
  @Override
  public @NotNull NonNullList<Ingredient> getIngredients() { return inputs; }
  
  public EnderORRecipe setGroup(String group) {
    this.group = group;
    return this;
  }
  
  @Override
  public @NotNull String getGroup() { return group; }
}
