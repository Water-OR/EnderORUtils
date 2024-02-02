package io.github.enderor.client.gui.basic;

import io.github.enderor.utils.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EnderORGuiButton extends EnderORGuiBasic {
  public final int iconX, iconY, backgroundX, backgroundY;
  public final ResourceLocation icon, background;
  public final boolean hasIcon;
  public final String  buttonText;
  public       int     buttonColor = 0;
  
  protected boolean hovering = false;
  protected boolean focusing = false;
  
  protected EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int iconX, int iconY, int widthIn, int heightIn, ResourceLocation background, ResourceLocation icon, String buttonText) {
    super(buttonId, x, y, widthIn, heightIn);
    this.buttonText  = buttonText;
    this.iconX       = iconX;
    this.iconY       = iconY;
    this.icon        = icon;
    this.backgroundX = backgroundX;
    this.backgroundY = backgroundY;
    this.background  = background;
    hasIcon     = (icon != null);
  }
  
  public EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int widthIn, int heightIn, ResourceLocation background, String buttonText) {
    this(buttonId, x, y, backgroundX, backgroundY, 0, 0, widthIn, heightIn, background, null, buttonText);
  }
  
  public EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int iconX, int iconY, int widthIn, int heightIn, ResourceLocation background, ResourceLocation icon) {
    this(buttonId, x, y, backgroundX, backgroundY, iconX, iconY, widthIn, heightIn, background, icon, "");
  }
  
  public EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int iconX, int iconY, int widthIn, int heightIn, ResourceLocation texture) {
    this(buttonId, x, y, backgroundX, backgroundY, iconX, iconY, widthIn, heightIn, texture, texture);
  }
  
  public EnderORGuiButton(int buttonId, int x, int y, int textureX, int textureY, int widthIn, int heightIn, ResourceLocation texture) {
    this(buttonId, x, y, textureX, textureY, textureX, textureY, widthIn, heightIn, texture);
  }
  
  @Override
  public void draw(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    if (!visible) {
      return;
    }
    mc.getTextureManager().bindTexture(background);
    Pair<Integer, Integer> drawPosition = getBackgroundPosition();
    int                    drawX        = drawPosition.getKey();
    int                    drawY        = drawPosition.getValue();
    
    dummyGui.drawTexturedModalRect(x, y, drawX, drawY, width, height);
    
    hovering = isHovering(mc, mouseX, mouseY);
    
    if (hasIcon) {
      mc.getTextureManager().bindTexture(icon);
      drawPosition = getIconPosition();
      drawX        = drawPosition.getKey();
      drawY        = drawPosition.getValue();
      
      dummyGui.drawTexturedModalRect(x, y, drawX, drawY, width, height);
    } else {
      drawText(mc, mouseX, mouseY, partialTicks);
    }
  }
  
  /**
   * <p>
   * Get the position of button background
   * <br>
   * <br>
   * Some button has different texture on different stat. And Their background textures are at different position.
   * </p>
   *
   * @return The background position of the button
   */
  public Pair<Integer, Integer> getBackgroundPosition() {
    return new Pair<>(backgroundX, backgroundY);
  }
  
  /**
   * <p>
   * Get the position of button icon.
   * <br>
   * <br>
   * Some button has different texture on different stat. And Their icon textures are at different position.
   * </p>
   *
   * @return The icon position of the button
   */
  public Pair<Integer, Integer> getIconPosition() {
    return new Pair<>(iconX, iconY);
  }
  
  
  /**
   * <p>
   * Get the color of button text
   * <br>
   * <br>
   * Some button has different color on different stat.
   * </p>
   *
   * @return The color of the text
   */
  public int getTextColor() {
    if (buttonColor != 0) { return buttonColor; }
    if (!enabled) { return 0xa0a0a0; }
    if (hovering) { return 0xffffa0; }
    return 0xe0e0e0;
  }
  
  public void drawText(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    FontRenderer fontRenderer = mc.fontRenderer;
    int          drawX        = x + (width) / 2;
    int          drawY        = y + (height - fontRenderer.FONT_HEIGHT) / 2;
    dummyGui.drawCenteredString(mc.fontRenderer, buttonText, drawX, drawY, getTextColor());
  }
  
  @Override
  public void mouseClicked(Minecraft mc, int mouseX, int mouseY, int state) { mouseDragged(mc, mouseX, mouseY, state, 0); }
  
  @Override
  public void mouseDragged(@NotNull Minecraft mc, int mouseX, int mouseY, int state, long duration) { focusing = isHovering(mc, mouseX, mouseY); }
  
  @Override
  public void mouseReleased(Minecraft mc, int mouseX, int mouseY, int state) { focusing = false; }
  
  public int getIconX()                   { return iconX; }
  
  public int getIconY()                   { return iconY; }
  
  public int getBackgroundX()             { return backgroundX; }
  
  public int getBackgroundY()             { return backgroundY; }
  
  public ResourceLocation getIcon()       { return icon; }
  
  public ResourceLocation getBackground() { return background; }
  
  public boolean isHasIcon()              { return hasIcon; }
}
