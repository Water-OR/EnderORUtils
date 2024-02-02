package io.github.enderor.mixins.minecraft;

import io.github.enderor.capabilities.ArrowCapability;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (EntityEnderman.class)
public abstract class MixinEntityEnderman extends EntityMob {
  
  public MixinEntityEnderman(World worldIn) {
    super(worldIn);
  }
  
  @Shadow
  public abstract boolean teleportRandomly();
  
  @Inject (method = "attackEntityFrom", at = @At ("HEAD"), cancellable = true)
  public void attackEntityFrom0(@NotNull DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (!(source instanceof EntityDamageSourceIndirect) || !(source.getImmediateSource() instanceof EntityArrow)) {
      return;
    }
    EntityArrow arrow = ((EntityArrow) source.getImmediateSource());
    if (!arrow.hasCapability(ArrowCapability.Provider.ARROW_CAPABILITY, null) ||
        !arrow.getCapability(ArrowCapability.Provider.ARROW_CAPABILITY, null).getCanDamageEMan()) { return; }
    if (arrow.isBurning()) { setFire(5); }
    boolean flag = super.attackEntityFrom(source, amount);
    
    if (source.isUnblockable() && rand.nextInt(10) != 0) {
      teleportRandomly();
    }
    
    cir.setReturnValue(flag);
  }
}
