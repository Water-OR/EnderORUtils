package io.github.enderor.network;

import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface IEnderORPacket<T extends IPacketHandler> {
  int getId();
  
  void read(@NotNull PacketBuffer bufIn) throws IOException;
  
  void write(@NotNull PacketBuffer bufOut) throws IOException;
  
  void progress(@NotNull T packetHandler);
}
