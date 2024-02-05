package io.github.enderor.enchantments;

import com.google.common.collect.Sets;
import io.github.enderor.attribute.EnderORAttributes;
import io.github.enderor.network.EnderORNetworkHandler;
import io.github.enderor.network.server.CPacketPlayerAttackMob;
import io.github.enderor.network.server.CPacketPlayerNotInCoolDown;
import io.github.enderor.utils.CalculateHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// TODO: Let it don't throw `ConcurrentModificationException` when it works with Ender IO `DirectUpgrade`
public class EnchantmentLongSword extends Enchantment {
  protected EnchantmentLongSword(String name) {
    super(Rarity.COMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    EnderOREnchantmentHandler.addEnchantment(this, name);
  }
  
  @Override
  public int getMaxLevel() { return 5; }
  
  @Override
  public int getMinEnchantability(int enchantmentLevel) { return 2 * enchantmentLevel - 1; }
  
  @Override
  public int getMaxEnchantability(int enchantmentLevel) { return getMinEnchantability(enchantmentLevel); }
  
  public static final Set<EntityLivingBase> entityWouldAttack    = Sets.newHashSet();
  public static final Set<EntityPlayer>     playersNotInCoolDown = Sets.newHashSet();
  
  public static  int     _ticksSinceLastSwing;
  
  public static boolean onLeftClick(@NotNull EntityPlayer player) { return onLeftClick(player, false); }
  
  public static boolean onLeftClick(@NotNull EntityPlayer player, boolean flag) {
    if (canNotActive(player)) { return false; }
    final int ticksSinceLastSwing = flag ? _ticksSinceLastSwing : player.ticksSinceLastSwing;
    player.ticksSinceLastSwing = ticksSinceLastSwing;
    EnderORNetworkHandler.INSTANCE.sendToServer(new CPacketPlayerNotInCoolDown(player.getCooledAttackStrength(0F) >= 1F));
    final double swingRange = getSwingRange(player);
    final double reach      = player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).getAttributeValue();
    double       l          = player.rotationYaw - swingRange;
    double       r          = player.rotationYaw + swingRange;
    for (; l < 0; l += 360D) { r += 360D; }
    final double finalL = l;
    final double finalR = r;
    player.getEntityWorld().getEntities(EntityLivingBase.class, player::canEntityBeSeen).stream().filter(entity -> player != entity)
          .filter(entity -> player.getDistanceSq(entity) <= reach * reach).filter(entity -> !(entity instanceof EntityPlayer))
          .filter(entity -> !entity.isDead).filter(entity -> {
            double angle = CalculateHelper.getAngle(entity.posX - player.posX, entity.posZ - player.posZ) - 90D;
            while (angle < finalL) { angle += 360D; }
            return angle < finalR;
          }).forEach(entityWouldAttack::add);
    for (EntityLivingBase entityLivingBase : entityWouldAttack) {
      player.ticksSinceLastSwing = ticksSinceLastSwing;
      EnderORNetworkHandler.INSTANCE.sendToServer(new CPacketPlayerAttackMob(ticksSinceLastSwing, entityLivingBase));
    }
    player.resetCooldown();
    entityWouldAttack.clear();
    EnderORNetworkHandler.INSTANCE.sendToServer(new CPacketPlayerNotInCoolDown(false));
    return true;
  }
  
  public static boolean canNotActive(@NotNull EntityPlayer player) {
    return player.isSpectator() ||
           !EnderOREnchantmentHandler.ENCHANTMENT_LONG_SWORD.canApply(player.getHeldItemMainhand()) &&
           !EnderOREnchantmentHandler.ENCHANTMENT_LONG_SWORD.canApply(player.getHeldItemOffhand());
  }
  
  public static double getSwingRange(@NotNull EntityPlayer player) {
    final double swingRange = player.getAttributeMap().getAttributeInstance(EnderORAttributes.SWING_RANGE).getAttributeValue() +
                              Math.max(EnchantmentHelper.getEnchantmentLevel(EnderOREnchantmentHandler.ENCHANTMENT_LONG_SWORD, player.getHeldItemMainhand()) * 5,
                                       EnchantmentHelper.getEnchantmentLevel(EnderOREnchantmentHandler.ENCHANTMENT_LONG_SWORD, player.getHeldItemOffhand()) * 5
                              );
    return swingRange >= 179D ? 180D : swingRange;
  }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public static void onEvent(PlayerInteractEvent.@NotNull LeftClickEmpty event) {
      if (event.getWorld().isRemote) {
        onLeftClick(event.getEntityPlayer(), true);
      }
    }
    
    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public static void onEvent(PlayerInteractEvent.@NotNull LeftClickBlock event) {
      if (event.getWorld().isRemote) {
        onLeftClick(event.getEntityPlayer());
      }
    }
    
    @SubscribeEvent (priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onEvent(@NotNull AttackEntityEvent event) {
      if (event.getEntity().getEntityWorld().isRemote && event.getTarget() instanceof EntityLivingBase &&
          !entityWouldAttack.contains((EntityLivingBase) event.getTarget()) && onLeftClick(event.getEntityPlayer())) {
        event.setCanceled(true);
      }
    }
    
    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onEvent(@NotNull CriticalHitEvent event) {
      if (!event.getEntity().getEntityWorld().isRemote && event.getTarget() instanceof EntityLivingBase &&
          playersNotInCoolDown.contains(event.getEntityPlayer())) {
        event.setDamageModifier(event.getDamageModifier() * 1.2F);
      }
    }
  }
}
