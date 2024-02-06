package io.github.enderor.network.server;

import io.github.enderor.EnderORUtils;
import io.github.enderor.config.EnderORConfigs;
import io.github.enderor.containers.ContainerEnchantMover;
import io.github.enderor.enchantments.EnchantmentLongSword;
import io.github.enderor.network.IPacketHandler;
import io.github.enderor.utils.NullHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ClientPacketsHandler implements IPacketHandler {
  private MultiPartEntityPart dummyPart;
  
  public ClientPacketsHandler setPlayerMP(EntityPlayerMP playerMP) {
    this.playerMP = playerMP;
    return this;
  }
  
  private EntityPlayerMP playerMP;
  
  public ClientPacketsHandler setWorldServer(WorldServer worldServer) {
    this.worldServer = worldServer;
    dummyPart = new MultiPartEntityPart(new DummyMultiPart(), "", 0, 0);
    return this;
  }
  
  private WorldServer worldServer;
  
  public ClientPacketsHandler() { }
  
  public void progressSlotChanged(@NotNull CPacketContainerSlotChanged packet) {
    Container container = playerMP.openContainer;
    
    if (packet.containerType.equals(CPacketContainerSlotChanged.ContainerType.ENCHANT_MOVER)) {
      if (!(container instanceof ContainerEnchantMover)) { return; }
      container.getSlot(packet.slot).putStack(packet.stack);
    } else {
      EnderORUtils.log(Level.ERROR, "Couldn't resolve packet container type!");
    }
  }
  
  public void progressPlayerAttackMob(@NotNull CPacketPlayerAttackMob packet) {
    final Entity entity = worldServer.getEntityByID(packet.entityId);
    if (entity == null || entity.isDead) { return; }
    if (!(entity instanceof EntityLivingBase)) {
      EnderORUtils.log(Level.ERROR, "Entity %s with id %s is not a living entity", entity, packet.entityId);
      return;
    }
    playerMP.ticksSinceLastSwing = packet.tickSinceLastSwing;
    playerMP.attackTargetEntityWithCurrentItem(entity);
    if (entity instanceof IEntityMultiPart) {
      Class<? extends Entity> clazz = entity.getClass();
      Field[]                 fields = clazz.getFields();
      for (Field field : fields) {
        if (field.getType().isAssignableFrom(MultiPartEntityPart.class)) {
          try {
            field.setAccessible(true);
            playerMP.attackTargetEntityWithCurrentItem(((MultiPartEntityPart) field.get(dummyPart)));
          } catch (Exception ignored) {}
        }
      }
    }
  }
  
  public class DummyMultiPart implements IEntityMultiPart {
    @Override
    public @NotNull World getWorld() { return worldServer; }
    
    @Override
    public boolean attackEntityFromPart(@NotNull MultiPartEntityPart part, @NotNull DamageSource source, float damage) { return false; }
  }
  
  public void progressPlayerNotInCoolDown(@NotNull CPacketPlayerNotInCoolDown packet) {
    if (packet.newState) {
      EnchantmentLongSword.playersNotInCoolDown.add(playerMP);
    } else {
      EnchantmentLongSword.playersNotInCoolDown.remove(playerMP);
    }
  }
}
