package io.github.enderor.client.special;

import io.github.enderor.client.utils.ColorUtils;
import io.github.enderor.client.utils.PlayerUtils;
import io.github.enderor.config.EnderORClientConfigs;
import io.github.enderor.enchantments.EnchantmentLongSword;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly (Side.CLIENT)
@Mod.EventBusSubscriber
public class SwingRange {
  
  @SubscribeEvent
  public static void onEvent(RenderWorldLastEvent event) {
    if (!EnderORClientConfigs.ENABLE_SWING_RANGE_DISPLAY) { return; }
    EntityPlayerSP me = PlayerUtils.getPlayer();
    if (EnchantmentLongSword.canNotActive(me)) { return; }
    final double swingRange = EnchantmentLongSword.getSwingRange(me);
    if (swingRange <= 1D) { return; }
    final double reach = me.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).getAttributeValue();
    final double left  = me.rotationYaw + 90D - swingRange;
    final double right = me.rotationYaw + 90D + swingRange;
    final float  ticks = event.getPartialTicks();
    
    final RenderManager renderManager = PlayerUtils.getMc().getRenderManager();
    
    final double offsetX = me.posX + (me.prevPosX - me.posX) * (1D - ticks) - renderManager.viewerPosX;
    final double offsetY = me.posY + (me.prevPosY - me.posY) * (1D - ticks) - renderManager.viewerPosY;
    final double offsetZ = me.posZ + (me.prevPosZ - me.posZ) * (1D - ticks) - renderManager.viewerPosZ;
    
    GL11.glTranslated(offsetX, offsetY, offsetZ);
    draw(reach, left, right);
    GL11.glTranslated(-offsetX, -offsetY, -offsetZ);
  }
  
  public static void draw(final double reach, final double left, final double right) {
    boolean wasGlLineSmoothEnabled = GL11.glIsEnabled(GL11.GL_LINE_SMOOTH);
    boolean wasGlBlendEnabled      = GL11.glIsEnabled(GL11.GL_BLEND);
    boolean wasGlTexture2DEnabled  = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
    boolean wasGlCullFaceEnabled   = GL11.glIsEnabled(GL11.GL_CULL_FACE);
    boolean wasGLLightingEnabled   = GL11.glIsEnabled(GL11.GL_LIGHTING);
    
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL11.GL_LINE_SMOOTH);
    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);
    
    GL11.glLineWidth((float) EnderORClientConfigs.SWING_RANGE_BORDER_WIDTH);
    
    GL11.glBegin(GL11.GL_LINE_STRIP);
    ColorUtils.setGLColor4d(EnderORClientConfigs.SWING_RANGE_BORDER_COLOR);
    point(reach, left, right);
    GL11.glEnd();
    
    GL11.glLineWidth(1);
    
    GL11.glBegin(GL11.GL_POLYGON);
    ColorUtils.setGLColor4d(EnderORClientConfigs.SWING_RANGE_FACE_COLOR);
    point(reach, left, right);
    GL11.glEnd();
    
    ColorUtils.setGLColor4d(-1);
    
    if (!wasGlLineSmoothEnabled) { GL11.glDisable(GL11.GL_LINE_SMOOTH); }
    if (!wasGlBlendEnabled) { GL11.glDisable(GL11.GL_BLEND); }
    if (wasGlTexture2DEnabled) { GL11.glEnable(GL11.GL_TEXTURE_2D); }
    if (wasGlCullFaceEnabled) { GL11.glEnable(GL11.GL_CULL_FACE); }
    if (wasGLLightingEnabled) { GL11.glEnable(GL11.GL_LIGHTING); }
  }
  
  public static void point(final double reach, final double left, final double right) {
    final double  y        = .01;
    final boolean isCircle = right - left >= 179D;
    if (!isCircle) {
      for (double i = 0; i <= 1; i += .1) {
        GL11.glVertex3d(Math.cos(left * Math.PI / 180D) * reach * i, y,
                        Math.sin(left * Math.PI / 180D) * reach * i
        );
      }
    }
    
    for (double i = 0; i < 1.01; i += .01) {
      double angle = (left + (right - left) * i) * Math.PI / 180D;
      GL11.glVertex3d(Math.cos(angle) * reach, y,
                      Math.sin(angle) * reach
      );
    }
    
    if (!isCircle) {
      for (double i = 1; i >= 0; i -= .1) {
        GL11.glVertex3d(Math.cos(right * Math.PI / 180D) * reach * i, y,
                        Math.sin(right * Math.PI / 180D) * reach * i
        );
      }
    }
  }
}
