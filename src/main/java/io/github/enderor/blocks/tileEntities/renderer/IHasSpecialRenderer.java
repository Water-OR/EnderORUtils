package io.github.enderor.blocks.tileEntities.renderer;

import io.github.enderor.blocks.tileEntities.IHasTileEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly (Side.CLIENT)
public interface IHasSpecialRenderer extends IHasTileEntity {
  void register();
  
  default <T extends TileEntity> void register_(Class<T> tile, TileEntitySpecialRenderer<? super T> specialRenderer) {
    ClientRegistry.bindTileEntitySpecialRenderer(tile, specialRenderer);
  }
}
