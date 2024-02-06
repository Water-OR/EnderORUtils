package io.github.enderor.network.server;

import io.github.enderor.network.IEnderORPacket;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

public class CPacketPlayerAttackMob implements IEnderORPacket<ClientPacketsHandler> {
  public int tickSinceLastSwing;
  public int entityId;
  
  public CPacketPlayerAttackMob() { }
  
  public CPacketPlayerAttackMob(final int tickSinceLastSwing, final @NotNull EntityLivingBase target) {
    this.tickSinceLastSwing = tickSinceLastSwing;
    entityId                = target.getEntityId();
  }
  
  @Override
  public int getId() { return 1; }
  
  @Override
  public void read(@NotNull PacketBuffer bufIn) {
    tickSinceLastSwing = bufIn.readInt();
    entityId           = bufIn.readInt();
  }
  
  @Override
  public void write(@NotNull PacketBuffer bufOut) {
    bufOut.writeInt(tickSinceLastSwing);
    bufOut.writeInt(entityId);
  }
  
  @Override
  public void progress(@NotNull ClientPacketsHandler packetHandler) { packetHandler.progressPlayerAttackMob(this); }
}
