package io.github.enderor.client.utils;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class Draw2D {
  public static void drawRect(int xMin, int yMin, int xMax, int yMax, @NotNull ColorUtils color, boolean enableBlend) {
    drawRect((double) xMin, yMin, xMax, yMax, color.getARGB(), enableBlend);
  }
  
  public static void drawRect(int xMin, int yMin, int xMax, int yMax, int ARGB, boolean enableBlend) {
    drawRect((double) xMin, yMin, xMax, yMax, ARGB, enableBlend);
  }
  
  public static void drawRect(double xMin, double yMin, double xMax, double yMax, int ARGB, boolean enableBlend) {
    double tmp;
    if (xMin < xMax) {
      tmp  = xMin;
      xMin = xMax;
      xMax = tmp;
    }
    
    if (yMin < yMax) {
      tmp  = yMin;
      yMin = yMax;
      yMax = tmp;
    }
    
    GL11.glPushMatrix();
    if (enableBlend) {
      GL11.glEnable(GL11.GL_BLEND);
    }
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    ColorUtils.setGLColor4d(ColorUtils.ARGB_to_RGBA(ARGB));
    GL11.glBegin(GL11.GL_QUADS);
    GL11.glVertex2d(xMin, yMax);
    GL11.glVertex2d(xMax, yMax);
    GL11.glVertex2d(xMax, yMin);
    GL11.glVertex2d(xMin, yMin);
    GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glPopMatrix();
  }
  
  public static void drawLine(double x1, double y1, double x2, double y2, int RGBA, boolean enableBlend) {
    GL11.glPushMatrix();
    if (enableBlend) {
      GL11.glEnable(GL11.GL_BLEND);
    }
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    ColorUtils.setGLColor4d(RGBA);
    GL11.glBegin(GL11.GL_LINE_STRIP);
    GL11.glVertex2d(x1, y1);
    GL11.glVertex2d(x2, y2);
    GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glPopMatrix();
  }
  
  public static void drawCircle(final double x, final double y, final double r, int beginAngle, int endAngle, final int RGBA, final boolean enableBlend) {
    if (endAngle - beginAngle < 360) {
      beginAngle %= 360;
      endAngle = beginAngle + 360;
    } else {
      beginAngle %= 360;
      endAngle %= 360;
      if (endAngle < beginAngle) {
        endAngle += 360;
      }
    }
    
    GL11.glPushMatrix();
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL11.GL_LINE_SMOOTH);
    if (enableBlend) {
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
    GL11.glBegin(GL11.GL_LINE_STRIP);
    for (int i = beginAngle; i <= endAngle; ++i) {
      GL11.glVertex2d(x + r * Math.sin(i * Math.PI / 180D), y + r * Math.cos(i * Math.PI / 180D));
    }
    GL11.glEnd();
    ColorUtils.setGLColor4d(RGBA);
    GL11.glDisable(GL11.GL_LINE_SMOOTH);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glPopMatrix();
  }
}
