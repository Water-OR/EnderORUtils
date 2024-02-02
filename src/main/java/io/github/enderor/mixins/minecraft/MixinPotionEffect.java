package io.github.enderor.mixins.minecraft;

import io.github.enderor.utils.NullHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (value = PotionEffect.class, priority = 999)
public abstract class MixinPotionEffect {
  @Unique private static final String TAG_ID = "ID";
  
  @Unique
  private static @Nullable Potion enderORUtils$pot1;
  
  @Shadow
  public abstract Potion getPotion();
  
  @Inject (method = "writeCustomPotionEffectToNBT", at = @At (value = "TAIL"))
  public void setPot0(@NotNull NBTTagCompound nbt, @NotNull CallbackInfoReturnable<NBTTagCompound> cir) {
    nbt.setString(TAG_ID, NullHelper.getRegistryNameString(getPotion()));
  }
  
  @Inject (method = "readCustomPotionEffectFromNBT", at = @At (value = "HEAD"))
  private static void getPot1(@NotNull NBTTagCompound compound, @NotNull CallbackInfoReturnable<PotionEffect> cir) {
    enderORUtils$pot1 = Potion.getPotionFromResourceLocation(compound.getString(TAG_ID));
  }
  
  @Redirect (method = "readCustomPotionEffectFromNBT", at = @At (value = "INVOKE", target = "Lnet/minecraft/potion/Potion;getPotionById(I)Lnet/minecraft/potion/Potion;", ordinal = 0))
  private static @Nullable Potion returnPot1(int id) { return NullHelper.notNullOrElse(enderORUtils$pot1, () -> Potion.getPotionById(id)); }
}
