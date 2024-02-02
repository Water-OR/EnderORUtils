package io.github.enderor.network;

import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public interface IEnderORPacket<T extends IPacketHandler> {
  int getId();
  
  void read(PacketBuffer bufIn) throws IOException;
  
  void write(PacketBuffer bufOut) throws IOException;
  
  void progress(T packetHandler);
}
