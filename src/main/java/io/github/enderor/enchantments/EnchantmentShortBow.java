package io.github.enderor.enchantments;

import io.github.enderor.capabilities.ArrowCapability;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class EnchantmentShortBow extends Enchantment {
  public EnchantmentShortBow(String name) {
    super(Rarity.COMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    EnderOREnchantmentHandler.addEnchantment(this, name);
  }
  
  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return 0;
  }
  
  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return 0;
  }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent (priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onEvent(LivingEntityUseItemEvent.@NotNull Tick event) {
      EntityLivingBase player = event.getEntityLiving();
      if (!(player instanceof EntityPlayer)) { return; }
      Item bow = event.getItem().getItem();
      if (!(bow instanceof ItemBow)) { return; }
      if (EnchantmentHelper.getEnchantmentLevel(EnderOREnchantmentHandler.ENCHANTMENT_SHORT_BOW, event.getItem()) <= 0) {
        return;
      }
      if (bow.getMaxItemUseDuration(event.getItem()) - event.getDuration() <= 20) {
        return;
      }
      player.activeItemStackUseCount = Math.max(20, event.getDuration());
      player.stopActiveHand();
      event.setCanceled(true);
    }
    
    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onEvent(@NotNull EntityJoinWorldEvent event) {
      Entity arrow = event.getEntity();
      if (!(arrow instanceof EntityArrow)) { return; }
      Entity shooter = ((EntityArrow) arrow).shootingEntity;
      if (!(shooter instanceof EntityPlayer)) { return; }
      if (isShortBowActive(((EntityPlayer) shooter))) {
        if (!arrow.hasCapability(ArrowCapability.Provider.ARROW_CAPABILITY, null) || ((EntityArrow) arrow).pickupStatus == EntityArrow.PickupStatus.ALLOWED) { return; }
        arrow.getCapability(ArrowCapability.Provider.ARROW_CAPABILITY, null).setDisappearAfterLanded(true);
      }
    }
    
    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public static void onEvent(@NotNull FOVUpdateEvent event) {
      EntityPlayer player = event.getEntity();
      if (isShortBowActive(player)) {
        float f1 = (float) player.getItemInUseMaxCount() / 20.0F;
        event.setNewfov(event.getNewfov() / (1F - .15F * (f1 > 1F ? 1F : f1 * f1)));
      }
    }
    
    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public static void onEvent(InputUpdateEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      
      if (isShortBowActive(player)) {
        event.getMovementInput().moveForward *= 5;
        event.getMovementInput().moveStrafe *= 5;
      }
    }
    
    private static boolean isShortBowActive(EntityPlayer player) {
      return player.isHandActive() && player.getActiveItemStack().getItem() instanceof ItemBow && EnchantmentHelper.getEnchantmentLevel(EnderOREnchantmentHandler.ENCHANTMENT_SHORT_BOW, player.getActiveItemStack()) > 0;
    }
  }
}
