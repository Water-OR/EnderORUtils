package io.github.enderor.mixins.minecraft;

import io.github.enderor.items.ItemEnchantedPaper;
import io.github.enderor.utils.EnchantsHelper;
import io.github.enderor.utils.NullHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin (value = EnchantmentHelper.class, priority = 999)
public abstract class MixinEnchantmentHelper {
  @Unique
  private static @Nullable Enchantment enderORUtils$enchant0;
  @Unique
  private static @Nullable Enchantment enderORUtils$enchant1;
  @Unique
  private static @NotNull  String      enderORUtils$enchantName0 = "";
  @Unique
  private static @Nullable Enchantment enderORUtils$enchant2;
  
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
  
  @Redirect (method = "getEnchantmentLevel", at = @At (value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;getCompoundTagAt(I)Lnet/minecraft/nbt/NBTTagCompound;", ordinal = 0))
  private static @NotNull NBTTagCompound getEnchant0(@NotNull NBTTagList tagList, int index) {
    enderORUtils$enchant0 = EnchantsHelper.getEnchantInNBT(tagList.getCompoundTagAt(index));
    return tagList.getCompoundTagAt(index);
  }
  
  @Redirect (method = "getEnchantmentLevel", at = @At (value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;", ordinal = 0))
  private static Enchantment returnEnchant0(int id) { return NullHelper.notNullOrElse(enderORUtils$enchant0, () -> Enchantment.getEnchantmentByID(id)); }
  
  @Redirect (method = "getEnchantments", at = @At (value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;getCompoundTagAt(I)Lnet/minecraft/nbt/NBTTagCompound;", ordinal = 0))
  private static @NotNull NBTTagCompound getEnchant1(@NotNull NBTTagList tagList, int index) {
    enderORUtils$enchant1 = EnchantsHelper.getEnchantInNBT(tagList.getCompoundTagAt(index));
    return tagList.getCompoundTagAt(index);
  }
  
  @Redirect (method = "getEnchantments", at = @At (value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;", ordinal = 0))
  private static Enchantment returnEnchant1(int id) { return NullHelper.notNullOrElse(enderORUtils$enchant1, () -> Enchantment.getEnchantmentByID(id)); }
  
  @Redirect (method = "setEnchantments", at = @At (value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentID(Lnet/minecraft/enchantment/Enchantment;)I", ordinal = 0))
  private static int getEnchantName0(@NotNull Enchantment enchant) {
    enderORUtils$enchantName0 = NullHelper.getRegistryNameString(enchant);
    return Enchantment.getEnchantmentID(enchant);
  }
  
  @ModifyArg (method = "setEnchantments", at = @At (value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;appendTag(Lnet/minecraft/nbt/NBTBase;)V", ordinal = 0), index = 0)
  private static @NotNull NBTBase setEnchantName0(@NotNull NBTBase compound) {
    if (compound instanceof NBTTagCompound) {
      ((NBTTagCompound) compound).setString(EnchantsHelper.TAG_ID, enderORUtils$enchantName0);
    }
    return compound;
  }
  
  @Redirect (method = "applyEnchantmentModifier", at = @At (value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;getCompoundTagAt(I)Lnet/minecraft/nbt/NBTTagCompound;", ordinal = 0))
  private static @NotNull NBTTagCompound getEnchant2(@NotNull NBTTagList tagList, int index) {
    enderORUtils$enchant2 = EnchantsHelper.getEnchantInNBT(tagList.getCompoundTagAt(index));
    return tagList.getCompoundTagAt(index);
  }
  
  @Redirect (method = "applyEnchantmentModifier", at = @At (value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;"))
  private static Enchantment returnEnchant2(int id) { return NullHelper.notNullOrElse(enderORUtils$enchant2, () -> Enchantment.getEnchantmentByID(id)); }
}
