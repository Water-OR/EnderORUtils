package io.github.enderor.network.client;

import io.github.enderor.network.IEnderORPacket;
import io.github.enderor.network.WrongPacketException;
import io.github.enderor.utils.NullHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SPacketEnchantMaxLevelChange implements IEnderORPacket<ClientPacketHandler> {
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
  public void read(@NotNull PacketBuffer bufIn) throws IOException {
    String enchantName = bufIn.readString(bufIn.readableBytes());
    enchantment = Enchantment.getEnchantmentByLocation(enchantName);
    if (enchantment == null) {
      throw new WrongPacketException("Receive packet with unregistered enchant \"%s\"", enchantName);
    }
    maxLevel = bufIn.readInt();
  }
  
  @Override
  public void write(@NotNull PacketBuffer bufOut) throws IOException {
    bufOut.writeString(NullHelper.getRegistryNameString(enchantment));
    bufOut.writeInt(maxLevel);
  }
  
  @Override
  public void progress(@NotNull ClientPacketHandler packetHandler) {
    packetHandler.progressEnchantMaxLevelChange(this);
  }
}
