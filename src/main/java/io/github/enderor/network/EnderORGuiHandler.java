package io.github.enderor.network;

import io.github.enderor.blocks.tileEntities.TileEntityEnchantMover;
import io.github.enderor.client.gui.GuiEnchantMover;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EnderORGuiHandler implements IGuiHandler {
  INSTANCE;
  
  @Nullable
  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, @NotNull World world, int x, int y, int z) {
//    if (world.isRemote) { return null; }
    TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
    if (tile instanceof TileEntityEnchantMover) {
      return new GuiEnchantMover(player.inventory, ((TileEntityEnchantMover) tile));
    } else {
      return null;
    }
  }
  
  @Nullable
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, @NotNull World world, int x, int y, int z) {
//    if (world.isRemote) { return null; }
    TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
    if (tile instanceof TileEntityEnchantMover) {
      return ((TileEntityEnchantMover) tile).createContainer(player.inventory, player);
    } else {
      return null;
    }
  }
}
