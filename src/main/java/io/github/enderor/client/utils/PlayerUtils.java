package io.github.enderor.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly (Side.CLIENT)
public class PlayerUtils {
  public static Minecraft getMc() { return Minecraft.getMinecraft(); }
  
  public static EntityPlayerSP getPlayer() { return getMc().player; }
  
  public static boolean isMoving() { return getMc().world != null && (getPlayer().moveForward != 0 && getPlayer().moveStrafing != 0); }
  
  public static boolean isSneaking() { return getPlayer().isSneaking(); }
  
  public static boolean isSprinting() { return getPlayer().isSprinting(); }
}
