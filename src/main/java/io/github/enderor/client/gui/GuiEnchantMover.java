package io.github.enderor.client.gui;

import io.github.enderor.EnderORUtils;
import io.github.enderor.blocks.tileEntities.TileEntityEnchantMover;
import io.github.enderor.client.gui.basic.EnderORGuiBasic;
import io.github.enderor.client.gui.basic.EnderORGuiButton;
import io.github.enderor.client.utils.EnchantsHelperClient;
import io.github.enderor.containers.ContainerEnchantMover;
import io.github.enderor.network.EnderORNetworkHandler;
import io.github.enderor.network.server.CPacketContainerSlotChanged;
import io.github.enderor.utils.EnchantsHelper;
import io.github.enderor.utils.Pair;
import net.darkhax.enchdesc.client.TooltipHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@SideOnly (Side.CLIENT)
public class GuiEnchantMover extends GuiContainer {
  public static final ResourceLocation ENCHANT_MOVER_GUI = new ResourceLocation(EnderORUtils.MOD_ID, "textures/gui/container/enchant_mover.png");
  
  protected final TileEntityEnchantMover tile;
  protected final InventoryPlayer        inventoryPlayer;
  protected final ContainerEnchantMover  container;
  
  protected int drawX, drawY;
  
  protected final List<String> hoveringTexts = new ArrayList<>();
  
  protected List<EnderORGuiBasic> entryList = new ArrayList<>();
  
  public GuiEnchantMover(InventoryPlayer inventoryPlayer, @NotNull TileEntityEnchantMover tileEnchantMover) {
    super(tileEnchantMover.createContainer(inventoryPlayer, inventoryPlayer.player));
    tile                 = tileEnchantMover;
    container            = (ContainerEnchantMover) inventorySlots;
    this.inventoryPlayer = inventoryPlayer;
    
    xSize = 256;
    ySize = 238;
    entryList.add(new ButtonRemoveAll());
    entryList.add(new ButtonMergeAll());
    entryList.add(new ButtonListRemove());
    entryList.add(new ButtonListMerge());
  }
  
  @Override
  public void initGui() {
    super.initGui();
  }
  
  protected void calcDrawPos() {
    drawX = (width - xSize) / 2;
    drawY = (height - ySize) / 2;
  }
  
  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    GL11.glPushMatrix();
    container.calcEnchants();
    drawDefaultBackground();
    calcDrawPos();
    hoveringTexts.clear();
    super.drawScreen(mouseX, mouseY, partialTicks);
    
    if (inventoryPlayer.getItemStack().isEmpty() && getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty()) {
      hoveringTexts.addAll(getItemToolTip(getSlotUnderMouse().getStack()));
    }
    
    entryList.stream().filter(entry -> entry instanceof IHasHoveringText).forEach(entry -> ((IHasHoveringText) entry).addHoveringText(hoveringTexts));
    drawHoveringText(hoveringTexts, mouseX, mouseY);
    GL11.glPopMatrix();
  }
  
  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    entryList.forEach(entry -> entry.mouseClicked(mc, mouseX - drawX, mouseY - drawY, mouseButton));
  }
  
  @Override
  protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    entryList.forEach(entry -> entry.mouseDragged(mc, mouseX - drawX, mouseY - drawY, clickedMouseButton, timeSinceLastClick));
  }
  
  @Override
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    super.mouseReleased(mouseX, mouseY, state);
    entryList.forEach(entry -> entry.mouseReleased(mc, mouseX - drawX, mouseY - drawY, state));
  }
  
  protected void mouseScrolled(int mouseX, int mouseY, int scroll) {
    entryList.forEach(entry -> entry.mouseScrolled(mc, mouseX - drawX, mouseY - drawY, scroll));
  }
  
  @Override
  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    int dWheel = Mouse.getEventDWheel();
    if (dWheel != 0) {
      mouseScrolled(Mouse.getEventX() * width / mc.displayWidth, height - Mouse.getEventY() * height / mc.displayHeight - 1, dWheel);
    }
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GL11.glPushMatrix();
    mc.getTextureManager().bindTexture(ENCHANT_MOVER_GUI);
    GL11.glTranslated(drawX, drawY, 0);
    drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);
    
    entryList.forEach(entry -> entry.draw(mc, mouseX - drawX, mouseY - drawY, partialTicks));
    GL11.glPopMatrix();
  }
  
  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    fontRenderer.drawString(tile.getDisplayName().getUnformattedText(), 91, 119, 4210752);
    fontRenderer.drawString(inventoryPlayer.getDisplayName().getUnformattedText(), 91, 145, 4210752);
  }
  
  protected static class BasicButton extends EnderORGuiButton implements IHasHoveringText {
    protected List<String> hoveringText = new ArrayList<>();
    
    protected BasicButton(int buttonId, int x, int y, int iconX, int iconY) {
      super(buttonId, x, y, 96, 238, iconX, iconY, 18, 18, ENCHANT_MOVER_GUI, ENCHANT_MOVER_GUI);
      visible = true;
      enabled = true;
    }
    
    @Override
    public Pair<Integer, Integer> getBackgroundPosition() { return new Pair<>(backgroundX + (focusing ? 18 : 0), backgroundY); }
    
    @Override
    public void addHoveringText(@NotNull List<String> hoveringText) {
      if (hovering) {
        hoveringText.addAll(this.hoveringText);
      }
    }
  }
  
  protected class ButtonRemoveAll extends BasicButton {
    protected ButtonRemoveAll() {
      super(0, 47, 133, 0, 238);
      container.addListener(() -> {
        hoveringText.clear();
        container.getEnchantsInSlot(0).forEach((enchant, level) -> hoveringText.add(EnchantsHelperClient.getEnchantText(enchant, level)));
      });
    }
    
    @Override
    public void mouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
      super.mouseReleased(mc, mouseX, mouseY, state);
      if (!isHovering(mc, mouseX, mouseY)) { return; }
      removeEnchants();
    }
  }
  
  protected class ButtonMergeAll extends BasicButton {
    protected ButtonMergeAll() {
      super(1, 191, 133, 36, 238);
      container.addListener(() -> {
        hoveringText.clear();
        container.getEnchantsInSlot(1).forEach((enchant, level) -> hoveringText.add(EnchantsHelperClient.getEnchantText(enchant, level)));
      });
    }
    
    @Override
    public void mouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
      super.mouseReleased(mc, mouseX, mouseY, state);
      if (!isHovering(mc, mouseX, mouseY)) { return; }
      mergeEnchants();
    }
  }
  
  protected abstract class BasicButtonList extends EnderORGuiBasic implements IHasHoveringText {
    protected    List<EnchantButton> buttonList     = new ArrayList<>();
    public final int                 enchantSlotId;
    protected    EnchantButton       buttonReleased = null;
    
    protected float   scrolledHeight   = 0F;
    protected int     scrollableHeight = 0;
    protected boolean hovering         = false;
    
    
    protected ScrollBar scrollBar = new ScrollBar();
    
    protected Map<Enchantment, Integer> enchants = new HashMap<>();
    
    public BasicButtonList(int id, int x, int y) {
      super(id, x, y, 119, 108);
      container.addListener(this::refreshButtons);
      refreshButtons();
      enchantSlotId = id & 1;
    }
    
    public abstract void afterButtonMouseReleased(Minecraft mc, int buttonId);
    
    protected void refreshButtons() {
      AtomicInteger i = new AtomicInteger();
      buttonList.clear();
      enchants = container.getEnchantsInSlot(enchantSlotId);
      enchants.forEach((enchant, level) -> {
        buttonList.add(new EnchantButton(i.get(), 1, 1 + i.get() * 14, enchant, level));
        i.incrementAndGet();
      });
      scrollableHeight = Math.max(0, i.get() * 14 - height + 2);
    }
    
    @Override
    public void draw(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      hovering = isHovering(mc, mouseX, mouseY);
      GL11.glPushMatrix();
      GL11.glTranslated(x, y, 0);
      scrollBar.draw(mc, mouseX - x, mouseY - y, partialTicks);
      GL11.glTranslated(0, -scrolledHeight, 0);
      
      final boolean wasGlScissorEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
      if (!wasGlScissorEnabled) { GL11.glEnable(GL11.GL_SCISSOR_TEST); }
      
      final int scale = getScaledResolution(mc).getScaleFactor();
      GL11.glScissor((drawX + x) * scale, (GuiEnchantMover.this.height - height - drawY - y - 1) * scale + 4, width * scale, (height - 2) * scale);
      
      int i = Math.max(0, Math.min((int) scrolledHeight / 14, buttonList.size() - 1));
      for (; i < buttonList.size() && i * 14 - scrolledHeight <= height - 2; ++i) {
        buttonList.get(i).draw(mc, mouseX - x, (int) (mouseY - y + scrolledHeight), partialTicks);
      }
      if (!wasGlScissorEnabled) { GL11.glDisable(GL11.GL_SCISSOR_TEST); }
      GL11.glPopMatrix();
    }
    
    @Override
    public void mouseClicked(Minecraft mc, int mouseX, int mouseY, int state) {
      scrollBar.mouseClicked(mc, mouseX - x, mouseY - y, state);
      int i = Math.max(0, Math.min((int) scrolledHeight / 14, buttonList.size() - 1));
      for (; i < buttonList.size() && i * 14 - scrolledHeight <= height - 2; ++i) {
        buttonList.get(i).mouseClicked(mc, mouseX - x, (int) (mouseY - y + scrolledHeight), state);
      }
    }
    
    @Override
    public void mouseDragged(Minecraft mc, int mouseX, int mouseY, int state, long duration) {
      scrollBar.mouseDragged(mc, mouseX - x, mouseY - y, state, duration);
      int i = Math.max(0, Math.min((int) scrolledHeight / 14, buttonList.size() - 1));
      for (; i < buttonList.size() && i * 14 - scrolledHeight <= height - 2; ++i) {
        buttonList.get(i).draw(mc, mouseX - x, (int) (mouseY - y + scrolledHeight), duration);
      }
    }
    
    @Override
    public void mouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
      scrollBar.mouseReleased(mc, mouseX - x, mouseY - y, state);
      buttonReleased = null;
      int i = Math.max(0, Math.min((int) scrolledHeight / 14, buttonList.size() - 1));
      for (; i < buttonList.size() && i * 14 - scrolledHeight <= height - 2 && buttonReleased == null; ++i) {
        buttonList.get(i).draw(mc, mouseX - x, (int) (mouseY - y + scrolledHeight), state);
      }
      if (buttonReleased != null) { afterButtonMouseReleased(mc, buttonReleased.id); }
    }
    
    @Override
    public void mouseScrolled(Minecraft mc, int mouseX, int mouseY, int scroll) {
      if (isHovering(mc, mouseX, mouseY)) {
        if (isShiftKeyDown()) { scroll *= 7; }
        scrolledHeight = Math.max(0, Math.min(scrolledHeight - scroll * 2 / 120F, scrollableHeight));
      }
    }
    
    @Override
    public void addHoveringText(@NotNull List<String> hoveringText) {
      if (hovering) { buttonList.forEach(button -> button.addHoveringText(hoveringText)); }
    }
    
    protected class ScrollBar extends EnderORGuiBasic {
      public final int childWith        = 12;
      public final int childHeight      = 15;
      public final int scrollableHeight = 91;
      
      protected boolean focusing = false;
      protected float   scroll   = 0F;
      protected float   lastScroll;
      protected int     lastY;
      
      
      public ScrollBar() {
        super(0, 105, 0, 14, 108);
      }
      
      public void calcScroll() {
        if (BasicButtonList.this.scrollableHeight == 0) {
          scroll = 0;
          return;
        }
        scroll = scrolledHeight * scrollableHeight / BasicButtonList.this.scrollableHeight;
      }
      
      @Override
      public void draw(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!focusing) { calcScroll(); }
        mc.getTextureManager().bindTexture(ENCHANT_MOVER_GUI);
        
        dummyGui.drawTexturedModalRect(x + 1F, 1 + y + scroll, focusing ? 244 : 232, 241, childWith, childHeight);
      }
      
      @Override
      public void mouseClicked(Minecraft mc, int mouseX, int mouseY, int state) {
        if (!isHovering(mc, mouseX, mouseY)) { return; }
        if (mouseY < scroll + 1 || scroll + 1 + childHeight <= mouseY) {
          scroll = Math.max(0, Math.min(mouseY - childHeight / 2, scrollableHeight));
        }
        lastScroll = scroll;
        lastY      = mouseY;
        focusing   = true;
        mouseDragged(mc, mouseX, mouseY, state, 0);
      }
      
      @Override
      public void mouseDragged(Minecraft mc, int mouseX, int mouseY, int state, long duration) {
        if (!focusing) { return; }
        scroll         = Math.max(0, Math.min(lastScroll + mouseY - lastY, scrollableHeight));
        scrolledHeight = scroll * BasicButtonList.this.scrollableHeight / scrollableHeight;
      }
      
      @Override
      public void mouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
        if (!focusing) { return; }
        focusing = false;
      }
    }
    
    protected class EnchantButton extends EnderORGuiButton implements IHasHoveringText {
      Enchantment  enchant;
      int          level;
      List<String> hoveringText = new ArrayList<>();
      String       enchantName;
      
      public EnchantButton(int buttonId, int x, int y, @NotNull Enchantment enchant, int level) {
        super(buttonId, x, y, 72, 242, 0, 0, 103, 14, ENCHANT_MOVER_GUI, null);
        this.enchant = enchant;
        this.level   = level;
        hoveringText.add(EnchantsHelperClient.getEnchantText(enchant, level));
        hoveringText.add(EnchantsHelperClient.getDescription(enchant));
        hoveringText.add(TextFormatting.BLUE.toString().concat(TooltipHandler.getModName(enchant)));
        visible     = enabled = true;
        enchantName = I18n.format(enchant.getName()).trim();
      }
      
      @Override
      public int getTextColor() {
        if (hovering && !focusing) { return 0xffffa0; }
        return enchant.isCurse() ? 0xff5555 : 0xffffff;
      }
      
      @Override
      public void draw(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        hovering = isHovering(mc, mouseX, mouseY);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        mc.getTextureManager().bindTexture(ENCHANT_MOVER_GUI);
        dummyGui.drawTexturedModalRect(x, y, backgroundX, backgroundY, width, height);
        dummyGui.drawString(mc.fontRenderer, enchantName, x + 4, y + (height - mc.fontRenderer.FONT_HEIGHT + 1) / 2, getTextColor());
        GL11.glPopAttrib();
      }
      
      @Override
      public void mouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
        super.mouseReleased(mc, mouseX, mouseY, state);
        if (!isHovering(mc, mouseX, mouseY)) { return; }
        buttonReleased = this;
      }
      
      @Override
      public void addHoveringText(@NotNull List<String> hoveringText) {
        if (hovering) { hoveringText.addAll(this.hoveringText); }
      }
      
      @Override
      public boolean isHovering(Minecraft mc, int mouseX, int mouseY) { return BasicButtonList.this.hovering && super.isHovering(mc, mouseX, mouseY); }
    }
  }
  
  protected class ButtonListRemove extends BasicButtonList {
    public ButtonListRemove() {
      super(2, 7, 7);
    }
    
    @Override
    public void afterButtonMouseReleased(Minecraft mc, int buttonId) {
      EnchantButton button = buttonList.get(buttonId);
      removeEnchant(button.enchant);
    }
  }
  
  protected class ButtonListMerge extends BasicButtonList {
    public ButtonListMerge() {
      super(3, 130, 7);
    }
    
    @Override
    public void afterButtonMouseReleased(Minecraft mc, int buttonId) {
      EnchantButton button = buttonList.get(buttonId);
      mergeEnchant(button.enchant, button.level);
    }
  }
  
  protected interface IHasHoveringText {
    void addHoveringText(@NotNull List<String> hoveringText);
  }
  
  protected void removeEnchants()                             { setEnchants0(Collections.emptyMap()); }
  
  protected void mergeEnchants()                              { setEnchants0(EnchantsHelper.mergeEnchants(container.getEnchantsInSlot(0), container.getEnchantsInSlot(1))); }
  
  protected void removeEnchant(Enchantment enchant)           { setEnchants0(EnchantsHelper.removeEnchant(container.getEnchantsInSlot(0), enchant)); }
  
  protected void mergeEnchant(Enchantment enchant, int level) { setEnchants0(EnchantsHelper.mergeEnchant(container.getEnchantsInSlot(0), enchant, level)); }
  
  protected void setEnchants0(Map<Enchantment, Integer> enchants) {
    container.setEnchants0(enchants);
    EnderORNetworkHandler.INSTANCE.sendToServer(new CPacketContainerSlotChanged(CPacketContainerSlotChanged.ContainerType.ENCHANT_MOVER, 0, container.getSlot(0).getStack()));
  }
  
  @Contract ("_ -> new")
  public static @NotNull ScaledResolution getScaledResolution(Minecraft mc) { return new ScaledResolution(mc); }
}