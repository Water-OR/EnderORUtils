package io.github.enderor.client.gui.basic;

import io.github.enderor.client.utils.PlayerUtils;
import io.github.enderor.utils.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EnderORGuiButton extends EnderORGuiBasic {
  public final int     iconX;
  public final int     iconY;
  public final int     backgroundX;
  public final int     backgroundY;
  public       int     buttonColor = 0;
  public final boolean hasIcon;
  
  public final ResourceLocation background;
  public final ResourceLocation icon;
  
  protected boolean hovering = false;
  protected boolean focusing = false;
  protected boolean autoBind = false;
  
  public EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int widthIn, int heightIn, ResourceLocation background) { this(buttonId, x, y, backgroundX, backgroundY, 0, 0, widthIn, heightIn, background, null); }
  
  protected EnderORGuiButton(int buttonId, int x, int y, int backgroundX, int backgroundY, int iconX, int iconY, int widthIn, int heightIn, ResourceLocation background, ResourceLocation icon) {
    super(buttonId, x, y, widthIn, heightIn);
    this.iconX       = iconX;
    this.iconY       = iconY;
    this.icon        = icon;
    this.backgroundX = backgroundX;
    this.backgroundY = backgroundY;
    this.background  = background;
    hasIcon          = (icon != null);
  }
  
  @Override
  public void draw(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    if (!visible) { return; }
    if (autoBind) { mc.getTextureManager().bindTexture(background); }
    Pair<Integer, Integer> drawP = getBackgroundPosition();
    int                    drawX = drawP.getKey();
    int                    drawY = drawP.getValue();
    dummyGui.drawTexturedModalRect(x, y, drawX, drawY, width, height);
    hovering = isHovering(mouseX, mouseY);
    
    if (hasIcon) {
      if (autoBind) { mc.getTextureManager().bindTexture(icon); }
      drawP = getIconPosition();
      drawX = drawP.getKey();
      drawY = drawP.getValue();
      dummyGui.drawTexturedModalRect(x, y, drawX, drawY, width, height);
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
  
  @Override
  public void mouseClicked(Minecraft mc, int mouseX, int mouseY, int state) {
    mouseDragged(mc, mouseX, mouseY, state, 0);
    if (focusing) { playPressSound(PlayerUtils.getMc().getSoundHandler()); }
  }
  
  @Override
  public void mouseDragged(@NotNull Minecraft mc, int mouseX, int mouseY, int state, long duration) { focusing = isHovering(mouseX, mouseY); }
  
  @Override
  public void mouseReleased(Minecraft mc, int mouseX, int mouseY, int state) { focusing = false; }
}
