package io.github.enderor.client.special;

import io.github.enderor.network.EnderORNetworkHandler;
import io.github.enderor.network.client.SPacketArrowHurtEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber
public class ArrowHitSound {
  @SubscribeEvent
  public static void onEvent(@NotNull LivingHurtEvent event) {
    DamageSource source = event.getSource();
    if (!(source instanceof EntityDamageSourceIndirect)) { return; }
    if (!(source.getImmediateSource() instanceof EntityArrow)) { return; }
    Entity trueSource = source.getTrueSource();
    if (!(trueSource instanceof EntityPlayerMP)) { return; }
    EnderORNetworkHandler.INSTANCE.sendTo(new SPacketArrowHurtEntity(), ((EntityPlayerMP) trueSource));
  }
}
