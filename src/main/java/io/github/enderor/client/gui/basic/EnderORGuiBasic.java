package io.github.enderor.client.gui.basic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.SoundEvents;
import org.jetbrains.annotations.NotNull;

public abstract class EnderORGuiBasic {
  protected static final Gui dummyGui = new Gui();
  public final           int x, y, width, height, id;
  public boolean enabled = true;
  public boolean visible = true;
  
  public EnderORGuiBasic(int id, int x, int y, int width, int height) {
    this.id     = id;
    this.x      = x;
    this.y      = y;
    this.width  = width;
    this.height = height;
  }
  
  public abstract void draw(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks);
  
  public abstract void mouseClicked(Minecraft mc, int mouseX, int mouseY, int state);
  
  public abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY, int state, long duration);
  
  public abstract void mouseReleased(Minecraft mc, int mouseX, int mouseY, int state);
  
  public void mouseScrolled(Minecraft mc, int mouseX, int mouseY, int scroll) { }
  
  public void keyTyped(char ch, int key)                                      { }
  
  public void playPressSound(@NotNull SoundHandler soundHandlerIn) {
    soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
  }
  
  public boolean isHovering(Minecraft mc, int mouseX, int mouseY) { return enabled && visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height; }
}
