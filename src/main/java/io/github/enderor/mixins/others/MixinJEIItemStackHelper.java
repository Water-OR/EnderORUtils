package io.github.enderor.mixins.others;

import com.google.common.collect.Lists;
import io.github.enderor.items.ItemEnchantedPaper;
import io.github.enderor.items.baubles.ring.ItemPotionRing;
import io.github.enderor.utils.NullHelper;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin (value = ItemStackHelper.class, priority = 999)
public class MixinJEIItemStackHelper {
  @Inject (method = "getWildcardId(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", at = @At (value = "HEAD"), cancellable = true, remap = false)
  public void getEnderORItemsWildcardId(ItemStack stack, @NotNull CallbackInfoReturnable<String> cir) {
    NullHelper.checkNull(stack);
    if (stack.getItem() instanceof ItemEnchantedPaper) {
      final StringBuilder builder = new StringBuilder(NullHelper.getRegistryNameString(stack.getItem())).append("+[");
      ItemEnchantedPaper.getEnchants(stack).forEach((enchant, level) -> builder.append(NullHelper.getRegistryNameString(enchant)).append("%level:").append(level).append(";"));
      cir.setReturnValue(builder.append("]").toString());
    } else if (stack.getItem() instanceof ItemPotionRing) {
      final StringBuilder builder = new StringBuilder(NullHelper.getRegistryNameString(stack.getItem())).append("+[");
      ItemPotionRing.getEffects(stack).forEach((potion, level) -> builder.append(NullHelper.getRegistryNameString(potion)).append("%level:").append(level).append(";"));
      cir.setReturnValue(builder.append("]").toString());
    }
  }
}
