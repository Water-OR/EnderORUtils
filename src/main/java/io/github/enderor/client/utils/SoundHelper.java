package io.github.enderor.client.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundHelper {
  public static SoundEvent getSoundEvent(String registerName) {
    return SoundEvent.REGISTRY.getObject(new ResourceLocation(registerName));
  }
}
