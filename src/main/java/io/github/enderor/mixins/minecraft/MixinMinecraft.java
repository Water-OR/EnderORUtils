package io.github.enderor.mixins.minecraft;

import io.github.enderor.enchantments.EnchantmentLongSword;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (Minecraft.class)
public abstract class MixinMinecraft implements IThreadListener, ISnooperInfo {
  @Shadow
  public EntityPlayerSP player;
  
  @Inject (method = "clickMouse", at = @At("HEAD"))
  private void onEmptyClick(@NotNull CallbackInfo ci) {
    EnchantmentLongSword._ticksSinceLastSwing = player.ticksSinceLastSwing;
  }
}
