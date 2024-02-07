package io.github.enderor.network.server;

import io.github.enderor.EnderORUtils;
import io.github.enderor.network.IEnderORPacket;
import io.github.enderor.utils.NullHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CPacketContainerSlotChanged implements IEnderORPacket<ClientPacketsHandler> {
  public CPacketContainerSlotChanged() { }
  
  ContainerType containerType;
  int           slot;
  ItemStack     stack;
  
  public CPacketContainerSlotChanged(ContainerType type, int slot, ItemStack stack) {
    containerType = type;
    this.slot     = slot;
    this.stack    = stack;
  }
  
  @Override
  public int getId() { return 0; }
  
  @Override
  public void read(@NotNull PacketBuffer bufIn) throws IOException {
    containerType = ContainerType.getContainerFromName(bufIn.readString(ContainerType.MAX_TYPE_NAME_LENGTH));
    slot          = bufIn.readInt();
    stack         = new ItemStack(NullHelper.notNullCompound(bufIn.readCompoundTag()));
  }
  
  @Override
  public void write(@NotNull PacketBuffer bufOut) {
    bufOut.writeString(containerType.typeName);
    bufOut.writeInt(slot);
    bufOut.writeCompoundTag(stack.serializeNBT());
  }
  
  @Override
  public void progress(@NotNull ClientPacketsHandler packetHandler) {
    packetHandler.progressSlotChanged(this);
  }
  
  public enum ContainerType {
    NULL,
    ENCHANT_MOVER("enchant mover");
    
    ContainerType(String typeName) { this.typeName = EnderORUtils.MOD_NAME.concat("|").concat(typeName); }
    
    ContainerType()                { typeName = "null"; }
    
    public final String typeName;
    
    @Override
    public String toString() {
      return typeName;
    }
    
    private static final Map<String, ContainerType> containers = new HashMap<>();
    
    public static final int MAX_TYPE_NAME_LENGTH;
    
    static {
      Arrays.asList(ContainerType.values()).forEach(type -> containers.put(type.typeName, type));
      MAX_TYPE_NAME_LENGTH = Arrays.stream(ContainerType.values()).max(Comparator.comparingInt(type0 -> type0.typeName.length())).map(type -> type.typeName.length()).orElse(0);
    }
    
    public static ContainerType getContainerFromName(String name) { return containers.getOrDefault(name, NULL); }
  }
}
