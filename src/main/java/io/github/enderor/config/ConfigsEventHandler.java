package io.github.enderor.config;

import io.github.enderor.EnderORUtils;
import io.github.enderor.network.EnderORNetworkHandler;
import io.github.enderor.network.client.SPacketConfigSync;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class ConfigsEventHandler {
  @SubscribeEvent
  public static void onEvent(ConfigChangedEvent.@NotNull OnConfigChangedEvent event) {
    if (event.getModID().equals(EnderORUtils.MOD_ID)) {
      ConfigManager.sync(EnderORUtils.MOD_ID, Config.Type.INSTANCE);
    }
  }
  
  @SubscribeEvent
  public static void onEvent(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
    EnderORUtils.log(Level.INFO, "Sending server configs to client!");
    EnderORNetworkHandler.INSTANCE.sendTo(new SPacketConfigSync(), ((EntityPlayerMP) event.player));
  }
}
