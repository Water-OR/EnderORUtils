package io.github.enderor.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.*;

@SideOnly (Side.CLIENT)
public class Draw3D {
  public static void drawBoxBorder(AxisAlignedBB box, ColorUtils borderColor, float borderWidth, boolean enableBlend) {
    RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
    
    final boolean wasTexture2DEnabled = glIsEnabled(GL_TEXTURE_2D);
    final boolean wasLightingEnabled  = glIsEnabled(GL_LIGHTING);
    final boolean wasBlendEnabled     = glIsEnabled(GL_BLEND);
    
    glDisable(GL_TEXTURE_2D);
    glDisable(GL_LIGHTING);
    glDisable(GL_BLEND);
    
    if (enableBlend) {
      glEnable(GL_BLEND);
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    
    glLineWidth(borderWidth);
    
    ColorUtils.setGLColor4d(borderColor.getRGBA());
    AxisAlignedBB boxDraw = box.offset(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ);
    drawBoxBorder(boxDraw);
    
    glLineWidth(1.0F);
    glColor4d(0D, 0D, 0D, 0D);
    if (!wasBlendEnabled && enableBlend) { glDisable(GL_BLEND); }
    if (wasBlendEnabled && !enableBlend) { glEnable(GL_BLEND); }
    if (wasLightingEnabled) { glEnable(GL_LIGHTING); }
    if (wasTexture2DEnabled) { glEnable(GL_TEXTURE_2D); }
  }
  
  public static void drawBoxFace(AxisAlignedBB box, ColorUtils faceColor, float faceWidth, boolean enableBlend) {
    RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
    
    glDisable(GL_TEXTURE_2D);
    glDisable(GL_LIGHTING);
    glDisable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    if (enableBlend) {
      glEnable(GL_BLEND);
    }
    
    glLineWidth(faceWidth);
    
    ColorUtils.setGLColor4d(faceColor.getRGBA());
    AxisAlignedBB boxDraw = box.offset(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ);
    drawBoxFace(boxDraw);
    
    glLineWidth(1.0F);
    glColor4d(0D, 0D, 0D, 0D);
    glDisable(GL_BLEND);
    glEnable(GL_LIGHTING);
    glEnable(GL_TEXTURE_2D);
  }
  
  public static void drawBoxBorder(@NotNull AxisAlignedBB boxDraw) {
    
    glBegin(GL_LINE_STRIP);
    // Bottom
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.minZ);
    // Top
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.minZ);
    // Others
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.minZ);
    glEnd();
  }
  
  public static void drawBoxFace(AxisAlignedBB boxDraw) {
    glCullFace(GL_FRONT_AND_BACK);
    glBegin(GL_QUADS);
    
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.maxZ);
    
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.maxZ);
    
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.minZ);
    
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.minZ);
    
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.minZ);
    
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.maxZ);
    glVertex3d(boxDraw.minX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.minX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.minZ);
    glVertex3d(boxDraw.maxX, boxDraw.maxY, boxDraw.maxZ);
    glVertex3d(boxDraw.maxX, boxDraw.minY, boxDraw.maxZ);
    
    glEnd();
  }
  
  public static void drawRound(int glMode, Vec3d position, double radix) {
    glEnable(GL_LINE_SMOOTH);
    glBegin(glMode);
    for (int i = 0; i < 360; ++i) {
      glVertex3d(position.x + Math.cos(i * Math.PI / 180.0D) * radix, position.y, position.z + Math.sin(i * Math.PI / 180.0D) * radix);
    }
    glEnd();
    glDisable(GL_LINE_SMOOTH);
  }
}
