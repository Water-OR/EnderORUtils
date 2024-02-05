package io.github.enderor.network;

import io.github.enderor.EnderORUtils;
import io.github.enderor.network.client.ClientPacketHandler;
import io.github.enderor.network.client.SPacketArrowHurtEntity;
import io.github.enderor.network.client.SPacketEnchantMaxLevelChange;
import io.github.enderor.network.server.CPacketContainerSlotChanged;
import io.github.enderor.network.server.CPacketPlayerAttackMob;
import io.github.enderor.network.server.CPacketPlayerNotInCoolDown;
import io.github.enderor.network.server.ServerPacketHandler;
import io.github.enderor.utils.ExceptionUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum EnderORNetworkHandler {
  INSTANCE;
  private final String          channelName = EnderORUtils.MOD_NAME.replace(' ', '_');
  private final FMLEventChannel channel     = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelName);
  
  private final ClientPacketHandler clientPacketHandler = new ClientPacketHandler();
  private final ServerPacketHandler serverPacketHandler = new ServerPacketHandler();
  
  EnderORNetworkHandler() {
    channel.register(this);
  }
  
  @SubscribeEvent
  public void onClientPacket(FMLNetworkEvent.@NotNull ClientCustomPacketEvent event) {
    if (event.getPacket().channel().equals(channelName)) {
      decodeClientPacket(event.getPacket().payload(), Minecraft.getMinecraft().player);
    }
  }
  
  @SubscribeEvent
  public void onServerPacket(FMLNetworkEvent.@NotNull ServerCustomPacketEvent event) {
    if (event.getPacket().channel().equals(channelName)) {
      decodeServerPacket(event.getPacket().payload(), ((NetHandlerPlayServer) event.getHandler()).player);
    }
  }
  
  private void decodeClientPacket(@NotNull ByteBuf buf, EntityPlayerSP player) {
    PacketBuffer buffer = new PacketBuffer(buf);
    int          id     = buffer.readInt();
    if (!packetsClient.containsKey(id)) {
      EnderORUtils.log(Level.ERROR, "Can't resolve client packet type of id {}", id);
      return;
    }
    IEnderORPacket<ClientPacketHandler> packet = packetsClient.get(id);
    try {
      packet.read(buffer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try {
      packet.progress(clientPacketHandler.setPlayerSP(player).setWorldClient(Minecraft.getMinecraft().world));
    } catch (Exception e) {
      ExceptionUtils.print(e.fillInStackTrace());
    }
  }
  
  private void decodeServerPacket(@NotNull ByteBuf buf, EntityPlayerMP player) {
    PacketBuffer buffer = new PacketBuffer(buf);
    int          id     = buffer.readInt();
    if (!packetsServer.containsKey(id)) {
      EnderORUtils.log(Level.ERROR, "Can't resolve server packet type of id {}", id);
      return;
    }
    IEnderORPacket<ServerPacketHandler> packet = packetsServer.get(id);
    try {
      packet.read(buffer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try {
      packet.progress(serverPacketHandler.setPlayerMP(player).setWorldServer(player.getServerWorld()));
    } catch (Exception e) {
      ExceptionUtils.print(e.fillInStackTrace());
    }
  }
  
  @Contract ("_ -> new")
  private @NotNull FMLProxyPacket createPacket(@NotNull IEnderORPacket<?> packet) {
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    buffer.writeInt(packet.getId());
    try {
      packet.write(buffer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new FMLProxyPacket(new PacketBuffer(buffer), channelName);
  }
  
  public void sendToDimension(IEnderORPacket<?> packet, int dim)                                      { channel.sendToDimension(createPacket(packet), dim); }
  
  public void sendToAllAround(IEnderORPacket<?> packet, int dim, @NotNull BlockPos pos, double range) { channel.sendToAllAround(createPacket(packet), new NetworkRegistry.TargetPoint(dim, pos.getX(), pos.getY(), pos.getZ(), range)); }
  
  public void sendTo(IEnderORPacket<?> packet, EntityPlayerMP player)                                 { channel.sendTo(createPacket(packet), player); }
  
  public void sendToAll(IEnderORPacket<?> packet)                                                     { channel.sendToAll(createPacket(packet)); }
  
  public void sendToServer(IEnderORPacket<?> packet)                                                  { channel.sendToServer(createPacket(packet)); }
  
  private static final Map<Integer, IEnderORPacket<ServerPacketHandler>> packetsServer = new HashMap<>();
  private static final Map<Integer, IEnderORPacket<ClientPacketHandler>> packetsClient = new HashMap<>();
  
  private static void addClientPacket(@NotNull IEnderORPacket<ClientPacketHandler> dummyPacket) {
    if (packetsClient.containsKey(dummyPacket.getId())) {
      EnderORUtils.log(Level.WARN, "Duplicate client packet! id {} is already used!", dummyPacket.getId());
      return;
    }
    packetsClient.put(dummyPacket.getId(), dummyPacket);
  }
  
  private static void addServerPacket(@NotNull IEnderORPacket<ServerPacketHandler> dummyPacket) {
    if (packetsServer.containsKey(dummyPacket.getId())) {
      EnderORUtils.log(Level.WARN, "Duplicate server packet! id {} is already used!", dummyPacket.getId());
      return;
    }
    packetsServer.put(dummyPacket.getId(), dummyPacket);
  }
  
  static {
    addServerPacket(new CPacketContainerSlotChanged());
    addServerPacket(new CPacketPlayerAttackMob());
    addServerPacket(new CPacketPlayerNotInCoolDown());
    addClientPacket(new SPacketArrowHurtEntity());
    addClientPacket(new SPacketEnchantMaxLevelChange());
  }
}
