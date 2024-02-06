package io.github.enderor.capabilities;

import io.github.enderor.EnderORUtils;
import io.github.enderor.utils.NullHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerCapability implements IPlayerCapability {
  private boolean noKB;
  
  public void setNoKB(boolean value) { noKB = value; }
  
  public boolean isNoKB()            { return noKB; }
  
  @Override
  public String getName() { return "playerCapability"; }
  
  public static class Storage implements Capability.IStorage<IPlayerCapability> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IPlayerCapability> capability, @NotNull IPlayerCapability instance, EnumFacing side) {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setBoolean("noKB", instance.isNoKB());
      return compound;
    }
    
    @Override
    public void readNBT(Capability<IPlayerCapability> capability, @NotNull IPlayerCapability instance, EnumFacing side, NBTBase nbt) {
      NBTTagCompound compound = (NBTTagCompound) nbt;
      instance.setNoKB(compound.getBoolean("noKB"));
    }
  }
  
  public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
    @CapabilityInject (IPlayerCapability.class)
    public static Capability<IPlayerCapability> PLAYER_CAPABILITY = null;
    private final IPlayerCapability             instance;
    
    public Provider() {
      instance = PLAYER_CAPABILITY.getDefaultInstance();
    }
    
    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == PLAYER_CAPABILITY;
    }
    
    @Nullable
    @Override
    public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
      return capability == PLAYER_CAPABILITY ? PLAYER_CAPABILITY.cast(instance) : null;
    }
    
    @Override
    public NBTTagCompound serializeNBT() {
      return (NBTTagCompound) PLAYER_CAPABILITY.getStorage().writeNBT(PLAYER_CAPABILITY, instance, null);
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
      PLAYER_CAPABILITY.getStorage().readNBT(PLAYER_CAPABILITY, instance, null, nbt);
    }
  }
  
  @Mod.EventBusSubscriber
  public static class Handler {
    @SubscribeEvent
    public static void onEvent(@NotNull AttachCapabilitiesEvent<EntityPlayer> event) {
      if (event.getObject().hasCapability(Provider.PLAYER_CAPABILITY, null)) { return; }
      event.addCapability(new ResourceLocation(EnderORUtils.MOD_ID, NullHelper.checkNull(Provider.PLAYER_CAPABILITY).getName()), new Provider());
    }
    
    @SubscribeEvent (receiveCanceled = true)
    public static void onEvent(@NotNull LivingKnockBackEvent event) {
      if (!(event.getEntityLiving() instanceof EntityPlayer)) { return; }
      EntityPlayer player = ((EntityPlayer) event.getEntityLiving());
      if (!player.hasCapability(Provider.PLAYER_CAPABILITY, null)) { return; }
      if (NullHelper.checkNull(player.getCapability(Provider.PLAYER_CAPABILITY, null)).isNoKB()) {
        event.setCanceled(true);
      }
    }
  }
}
