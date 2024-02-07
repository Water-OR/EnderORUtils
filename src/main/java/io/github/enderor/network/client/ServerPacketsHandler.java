package io.github.enderor.network.client;

import io.github.enderor.EnderORUtils;
import io.github.enderor.config.EnchantsMaxLevel;
import io.github.enderor.config.EnderORClientConfigs;
import io.github.enderor.config.EnderORConfigs;
import io.github.enderor.network.IPacketHandler;
import io.github.enderor.utils.NullHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly (Side.CLIENT)
public class ServerPacketsHandler implements IPacketHandler {
  public ServerPacketsHandler setPlayerSP(EntityPlayerSP playerSP) {
    this.playerSP = playerSP;
    return this;
  }
  
  private EntityPlayerSP playerSP;
  
  public ServerPacketsHandler setWorldClient(WorldClient worldClient) {
    this.worldClient = worldClient;
    return this;
  }
  
  private WorldClient worldClient;
  
  public ServerPacketsHandler() { }
  
  public void progressSoundPlay() {
    if (!EnderORClientConfigs.ENABLE_ARROW_HURT_ENTITY_SOUND) { return; }
    worldClient.playSound(playerSP, playerSP.posX, playerSP.posY, playerSP.posZ, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, .18F, .45F);
  }
  
  public void progressEnchantMaxLevelChange(@NotNull SPacketEnchantMaxLevelChange packet) {
    EnchantsMaxLevel.setMaxLevel(packet.enchantment, packet.maxLevel);
    playerSP.sendMessage(new TextComponentString(I18n.format(EnderORUtils.MOD_ID + ".enchantment_max_level_changed", NullHelper.getRegistryNameString(packet.enchantment), packet.maxLevel)));
  }
  
  public void progressConfigSync(@NotNull SPacketConfigSync packet) {
    EnderORConfigs.EFFECT_SHOW_PARTICLES = packet.effectShowParticles;
  }
}
