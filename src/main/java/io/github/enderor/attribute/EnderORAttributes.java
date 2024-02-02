package io.github.enderor.attribute;

import io.github.enderor.EnderORUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EnderORAttributes {
  public static final IAttribute SWING_RANGE = new RangedAttribute(null, EnderORUtils.MOD_ID + ".swing_range", 0D, 0D, 180D).setShouldWatch(true);
  
  public static final UUID ENDER_OR_UUID = UUID.fromString("72795db7-c9ff-9b31-de72-e64a4b0c95b0");
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(EntityEvent.@NotNull EntityConstructing event) {
      if (!(event.getEntity() instanceof EntityLivingBase)) { return; }
      EntityLivingBase living = ((EntityLivingBase) event.getEntity());
      AbstractAttributeMap attributes = living.getAttributeMap();
      // TODO: Add other attributes
      if (!(event.getEntity() instanceof EntityPlayer)) { return; }
      attributes.registerAttribute(SWING_RANGE);
    }
  }
}
