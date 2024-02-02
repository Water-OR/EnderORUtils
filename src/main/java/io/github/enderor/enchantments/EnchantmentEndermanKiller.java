package io.github.enderor.enchantments;

import io.github.enderor.capabilities.ArrowCapability;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentEndermanKiller extends Enchantment {
  protected EnchantmentEndermanKiller(String name) {
    super(Rarity.COMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    EnderOREnchantmentHandler.addEnchantment(this, name);
  }
  
  @Override
  public int getMinEnchantability(int enchantmentLevel) { return 0; }
  
  @Override
  public int getMaxEnchantability(int enchantmentLevel) { return 0; }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent (priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onEvent(EntityJoinWorldEvent event) {
      
      Entity arrow = event.getEntity();
      if (!(arrow instanceof EntityArrow)) { return; }
      Entity shooter = ((EntityArrow) arrow).shootingEntity;
      if (!(shooter instanceof EntityPlayer)) { return; }
      if (EnchantmentHelper.getEnchantmentLevel(EnderOREnchantmentHandler.ENCHANTMENT_ENDERMAN_KILLER, ((EntityPlayer) shooter).getActiveItemStack()) > 0) {
        if (!arrow.hasCapability(ArrowCapability.Provider.ARROW_CAPABILITY, null)) { return; }
        arrow.getCapability(ArrowCapability.Provider.ARROW_CAPABILITY, null).setCanDamageEMan(true);
      }
    }
  }
}
