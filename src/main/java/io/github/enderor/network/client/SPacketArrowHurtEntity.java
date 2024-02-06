package io.github.enderor.network.client;

import io.github.enderor.network.IEnderORPacket;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SPacketArrowHurtEntity implements IEnderORPacket<ServerPacketsHandler> {
  
  public SPacketArrowHurtEntity() {
  }
  
  @Override
  public int getId() { return 0; }
  
  @Override
  public void read(@NotNull PacketBuffer bufIn) throws IOException { }
  
  @Override
  public void write(@NotNull PacketBuffer bufOut) throws IOException { }
  
  @Override
  public void progress(@NotNull ServerPacketsHandler packetHandler) {
    packetHandler.progressSoundPlay();
  }
}
