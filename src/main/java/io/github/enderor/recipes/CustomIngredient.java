package io.github.enderor.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CustomIngredient extends Ingredient {
  public final CustomMatcher matcher;
  public CustomIngredient(CustomMatcher matcher, ItemStack... stacks) {
    super(stacks);
    this.matcher = matcher;
  }
  
  @Override
  public boolean apply(@Nullable ItemStack stack) {
    return matcher.apply(Arrays.asList(matchingStacks), stack);
  }
  
  @Override
  public boolean isSimple() { return false; }
  
  public interface CustomMatcher {
    boolean apply(List<ItemStack> stackList, ItemStack stack);
  }
  
  public static final CustomIngredient EMPTY = new CustomIngredient((stackList, stack) -> stack.isEmpty()) {
    @Override
    public boolean apply(@Nullable ItemStack stack) {
      return stack == null || stack.isEmpty();
    }
  };
  
  public static boolean isMatched(@NotNull ItemStack stack0, ItemStack stack1) {
    if (stack1 == null) { return false; }
    if (stack0.isEmpty() && stack1.isEmpty()) { return true; }
    if (stack0.isEmpty() || stack1.isEmpty()) { return false; }
    if (stack0.getItem() != stack1.getItem()) { return false; }
    int meta = stack0.getMetadata();
    return meta == Short.MAX_VALUE || meta == stack1.getMetadata();
  }
}
