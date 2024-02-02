package io.github.enderor.mixins.minecraft;

import io.github.enderor.items.ItemEnchantedPaper;
import io.github.enderor.utils.EnchantsHelper;
import io.github.enderor.utils.NullHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (value = ItemStack.class, priority = 999)
public abstract class MixinItemStack {
  @Shadow
  @Final
  private Item item;
  
  @Shadow
  public abstract boolean isEmpty();
  
  @Unique
  private static @NotNull String enderORUtils$enchantName0 = "";
  
  @Inject (method = "addEnchantment", at = @At ("HEAD"), cancellable = true)
  public void addEnchantment1(Enchantment ench, int level, CallbackInfo ci) {
    if (isEmpty() || !(item instanceof ItemEnchantedPaper)) { return; }
    ItemStack _this = (ItemStack) (Object) (this);
    ItemEnchantedPaper.setEnchants(_this, EnchantsHelper.sortEnchants(EnchantsHelper.mergeEnchant(ItemEnchantedPaper.getEnchants(_this), ench, level)));
    ci.cancel();
  }
  
  @Redirect (method = "addEnchantment", at = @At (value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentID(Lnet/minecraft/enchantment/Enchantment;)I", ordinal = 0))
  public int getEnchant0(@NotNull Enchantment enchant) {
    enderORUtils$enchantName0 = NullHelper.getRegistryNameString(enchant);
    return Enchantment.getEnchantmentID(enchant);
  }
  
  @ModifyArg (method = "addEnchantment", at = @At (value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;appendTag(Lnet/minecraft/nbt/NBTBase;)V"), index = 0)
  public NBTBase setEnchant0(@NotNull NBTBase compound) {
    if (compound instanceof NBTTagCompound) {
      ((NBTTagCompound) compound).setString(EnchantsHelper.TAG_ID, enderORUtils$enchantName0);
    }
    return compound;
  }
}
