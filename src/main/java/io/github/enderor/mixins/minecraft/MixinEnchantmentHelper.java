package io.github.enderor.mixins.minecraft;

import io.github.enderor.items.ItemEnchantedPaper;
import io.github.enderor.utils.EnchantsHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin (value = EnchantmentHelper.class, priority = 999)
public abstract class MixinEnchantmentHelper {
  @Inject (method = "getEnchantmentLevel", at = @At ("HEAD"), cancellable = true)
  private static void getEnchantedPaperEnchantmentLevel(Enchantment enchID, @NotNull ItemStack stack, CallbackInfoReturnable<Integer> cir) {
    if (stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantedPaper)) { return; }
    cir.setReturnValue(ItemEnchantedPaper.getEnchants(stack).getOrDefault(enchID, 0));
  }
  
  @Inject (method = "getEnchantments", at = @At ("HEAD"), cancellable = true)
  private static void getEnchantedPaperEnchantments(@NotNull ItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> cir) {
    if (stack.isEmpty() || (!(stack.getItem() instanceof ItemEnchantedPaper))) { return; }
    cir.setReturnValue(ItemEnchantedPaper.getEnchants(stack));
  }
  
  @Inject (method = "setEnchantments", at = @At ("HEAD"), cancellable = true)
  private static void setEnchantedPaperEnchantments(Map<Enchantment, Integer> enchMap, @NotNull ItemStack stack, CallbackInfo ci) {
    if (stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantedPaper)) { return; }
    ItemEnchantedPaper.setEnchants(stack, EnchantsHelper.sortEnchants(enchMap));
    ci.cancel();
  }
  
  @Inject (method = "applyEnchantmentModifier", at = @At ("HEAD"), cancellable = true)
  private static void applyEnchantedPaperEnchantmentModifier(EnchantmentHelper.IModifier modifier, @NotNull ItemStack stack, CallbackInfo ci) {
    if (stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantedPaper)) { return; }
    ci.cancel();
  }
}
