package io.github.enderor.blocks.tileEntities;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public interface IHasTileEntity {
  Class<? extends TileEntity> getTileClass();
}
