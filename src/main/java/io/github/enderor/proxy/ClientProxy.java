package io.github.enderor.proxy;

import io.github.enderor.EnderORUtils;
import io.github.enderor.blocks.EnderORBlockHandler;
import io.github.enderor.items.EnderORItemHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
  @Override
  public void registerModel() {
    EnderORItemHandler.registerModel();
    EnderORBlockHandler.registerModel();
    super.registerModel();
  }
  
  @Mod.EventBusSubscriber
  public static class Events {
    @SubscribeEvent
    public static void onEvent(ModelRegistryEvent event) {
      EnderORUtils.proxy.registerModel();
    }
  }
}
