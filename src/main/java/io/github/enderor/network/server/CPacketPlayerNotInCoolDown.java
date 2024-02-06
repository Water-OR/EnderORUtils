package io.github.enderor.network.server;

import io.github.enderor.network.IEnderORPacket;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CPacketPlayerNotInCoolDown implements IEnderORPacket<ClientPacketsHandler> {
  public boolean newState;
  
  public CPacketPlayerNotInCoolDown() { }
  
  public CPacketPlayerNotInCoolDown(boolean newState) { this.newState = newState; }
  
  @Override
  public int getId() { return 2; }
  
  @Override
  public void read(@NotNull PacketBuffer bufIn) throws IOException {
    newState = bufIn.readBoolean();
  }
  
  @Override
  public void write(@NotNull PacketBuffer bufOut) throws IOException {
    bufOut.writeBoolean(newState);
  }
  
  @Override
  public void progress(@NotNull ClientPacketsHandler packetHandler) {
    packetHandler.progressPlayerNotInCoolDown(this);
  }
}
