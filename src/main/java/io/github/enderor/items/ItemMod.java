package io.github.enderor.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class ItemMod extends Item {
  public ItemMod() {
    setMaxStackSize(1);
    setMaxDamage(0);
    EnderORItemHandler.addModel(this, 0, "inventory");
  }
  
  boolean enableEffect = false;
  
  public void changeEnabled() { enableEffect = !enableEffect; }
  
  @Override
  public boolean hasEffect(@NotNull ItemStack stack) { return super.hasEffect(stack) || enableEffect; }
  
  @Override
  public void onPlayerStoppedUsing(@NotNull ItemStack stack, @NotNull World worldIn, @NotNull EntityLivingBase entityLiving, int timeLeft) { ((ItemMod) stack.getItem()).changeEnabled(); }
  
  @Override
  public boolean isBeaconPayment(@NotNull ItemStack stack) { return super.isBeaconPayment(stack) || (stack.getItem() instanceof ItemMod); }
  
  @Override
  public boolean canDisableShield(@NotNull ItemStack stack, @NotNull ItemStack shield, @NotNull EntityLivingBase entity, @NotNull EntityLivingBase attacker) {
    return super.canDisableShield(stack, shield, entity, attacker) || (stack.getItem() instanceof ItemMod);
  }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent (priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onEvent(@NotNull LivingDeathEvent event) {
      if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
      EntityPlayer player = (EntityPlayer) event.getEntityLiving();
      if ((player.getHeldItemMainhand().getItem() instanceof ItemMod) ||
          (player.getHeldItemOffhand().getItem() instanceof ItemMod)) {
        event.setCanceled(true);
        player.setHealth(player.getMaxHealth());
      }
    }
    
    @SubscribeEvent (priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onEvent(@NotNull LivingHurtEvent event) {
      if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
      EntityPlayer player = (EntityPlayer) event.getEntityLiving();
      if ((player.getHeldItemMainhand().getItem() instanceof ItemMod) ||
          (player.getHeldItemOffhand().getItem() instanceof ItemMod)) {
        event.setCanceled(true);
        player.setHealth(Math.max(player.getMaxHealth(), player.getHealth() + event.getSource().getHungerDamage()));
      }
    }
  }
}
