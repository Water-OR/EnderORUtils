package io.github.enderor.mixins.minecraft;

import io.github.enderor.items.ItemEnchantedPaper;
import io.github.enderor.utils.EnchantsHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (value = ItemStack.class, priority = 999)
public abstract class MixinItemStack {
  @Shadow
  @Final
  private Item item;
  
  @Shadow
  public abstract boolean isEmpty();
  
  @Inject (method = "addEnchantment", at = @At ("HEAD"), cancellable = true)
  public void addEnchantedPaperEnchantment(Enchantment ench, int level, CallbackInfo ci) {
    if (isEmpty() || !(item instanceof ItemEnchantedPaper)) { return; }
    ItemStack _this = (ItemStack) (Object) (this);
    ItemEnchantedPaper.setEnchants(_this, EnchantsHelper.sortEnchants(EnchantsHelper.mergeEnchant(ItemEnchantedPaper.getEnchants(_this), ench, level)));
    ci.cancel();
  }
}
