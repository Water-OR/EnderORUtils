package io.github.enderor.network.client;

import io.github.enderor.config.EnderORConfigs;
import io.github.enderor.network.IEnderORPacket;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

public class SPacketConfigSync implements IEnderORPacket<ServerPacketsHandler> {
  public boolean effectShowParticles;
  
  public SPacketConfigSync() {
    effectShowParticles = EnderORConfigs.EFFECT_SHOW_PARTICLES;
  }
  
  @Override
  public int getId() { return 2; }
  
  @Override
  public void read(@NotNull PacketBuffer bufIn) {
    effectShowParticles = bufIn.readBoolean();
  }
  
  @Override
  public void write(@NotNull PacketBuffer bufOut) {
    bufOut.writeBoolean(effectShowParticles);
  }
  
  @Override
  public void progress(@NotNull ServerPacketsHandler packetHandler) { packetHandler.progressConfigSync(this); }
}
