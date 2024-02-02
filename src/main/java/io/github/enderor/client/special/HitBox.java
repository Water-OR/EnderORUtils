package io.github.enderor.client.special;

import io.github.enderor.config.EnderORClientConfigs;
import io.github.enderor.client.utils.ColorUtils;
import io.github.enderor.client.utils.Draw3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly (Side.CLIENT)
@Mod.EventBusSubscriber
public class HitBox {
  @SubscribeEvent
  public static void onRender(@NotNull RenderWorldLastEvent event) {
    if (!EnderORClientConfigs.ENABLE_HIT_BOX_DISPLAY) { return; }
    Minecraft      mc               = Minecraft.getMinecraft();
    List<Entity>   loadedEntityList = mc.world.loadedEntityList;
    EntityPlayerSP player           = mc.player;
    for (Entity entity : loadedEntityList) {
      if (!player.canEntityBeSeen(entity) || (entity == player && mc.gameSettings.thirdPersonView == 0)) { continue; }
      doRender(entity, event.getPartialTicks());
      if (entity instanceof EntityDragon) {
        MultiPartEntityPart[] parts      = ((EntityDragon) entity).dragonPartArray;
        float                 renderTime = event.getPartialTicks();
        for (MultiPartEntityPart part : parts) {
          AxisAlignedBB hitBox = part.getEntityBoundingBox();
          hitBox = hitBox.offset(
            (part.posX - part.prevPosX) * (renderTime),
            (part.posY - part.prevPosY) * (renderTime),
            (part.posZ - part.prevPosZ) * (renderTime)
          );
          GL11.glPushMatrix();
          Draw3D.drawBoxBorder(hitBox, ColorUtils.makeFromRGBA(EnderORClientConfigs.HIT_BOX_COLOR), (float) EnderORClientConfigs.HIT_BOX_WIDTH, false);
          GL11.glPopMatrix();
        }
      }
    }
  }
  
  public static void doRender(@NotNull Entity entity, double renderTime) {
    AxisAlignedBB hitBox = entity.getEntityBoundingBox().offset(
      (entity.prevPosX - entity.posX) * (1.0D - renderTime),
      (entity.prevPosY - entity.posY) * (1.0D - renderTime),
      (entity.prevPosZ - entity.posZ) * (1.0D - renderTime)
    );
    GL11.glPushMatrix();
    Draw3D.drawBoxBorder(hitBox, ColorUtils.makeFromRGBA(EnderORClientConfigs.HIT_BOX_COLOR), (float) EnderORClientConfigs.HIT_BOX_WIDTH, true);
    GL11.glPopMatrix();
  }
}
