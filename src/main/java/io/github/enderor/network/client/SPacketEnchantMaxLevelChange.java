package io.github.enderor.network.client;

import io.github.enderor.network.IEnderORPacket;
import io.github.enderor.utils.NullHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

public class SPacketEnchantMaxLevelChange implements IEnderORPacket<ServerPacketsHandler> {
  public Enchantment enchantment;
  public int         maxLevel;
  
  public SPacketEnchantMaxLevelChange() { }
  
  public SPacketEnchantMaxLevelChange(Enchantment enchantment, int maxLevel) {
    this.enchantment = enchantment;
    this.maxLevel    = maxLevel;
  }
  
  @Override
  public int getId() { return 1; }
  
  @Override
  public void read(@NotNull PacketBuffer bufIn) {
    String enchantName = bufIn.readString(bufIn.readableBytes());
    enchantment = Enchantment.getEnchantmentByLocation(enchantName);
    maxLevel    = bufIn.readInt();
  }
  
  @Override
  public void write(@NotNull PacketBuffer bufOut) {
    bufOut.writeString(NullHelper.getRegistryNameString(enchantment));
    bufOut.writeInt(maxLevel);
  }
  
  @Override
  public void progress(@NotNull ServerPacketsHandler packetHandler) {
    packetHandler.progressEnchantMaxLevelChange(this);
  }
}
