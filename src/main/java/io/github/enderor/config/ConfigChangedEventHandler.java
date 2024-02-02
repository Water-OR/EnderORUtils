package io.github.enderor.config;

import io.github.enderor.EnderORUtils;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class ConfigChangedEventHandler {
  @SubscribeEvent
  public static void onEvent(ConfigChangedEvent.@NotNull OnConfigChangedEvent event) {
    if (event.getModID().equals(EnderORUtils.MOD_ID)) {
      ConfigManager.sync(EnderORUtils.MOD_ID, Config.Type.INSTANCE);
    }
  }
}
