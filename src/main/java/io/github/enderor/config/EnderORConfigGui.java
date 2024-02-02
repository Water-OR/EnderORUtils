package io.github.enderor.config;

import io.github.enderor.EnderORUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

@SideOnly (Side.CLIENT)
public class EnderORConfigGui implements IModGuiFactory {
  @Override
  public void initialize(Minecraft minecraftInstance) { }
  
  @Override
  public boolean hasConfigGui() { return true; }
  
  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {
    return new GuiConfig(
      parentScreen,
      ConfigElement.from(EnderORConfigs.class).getChildElements(),
      EnderORUtils.MOD_ID,
      false,
      false,
      I18n.format("config." + EnderORUtils.MOD_NAME + ".default")
    );
  }
  
  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return Collections.emptySet(); }
}
