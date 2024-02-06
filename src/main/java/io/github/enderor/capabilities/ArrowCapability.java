package io.github.enderor.capabilities;

import com.google.common.collect.Lists;
import io.github.enderor.EnderORUtils;
import io.github.enderor.utils.NullHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArrowCapability implements IArrowCapability {
  private boolean disappearAfterLanded = false;
  private boolean canDamageEMan        = false;
  
  @Override
  public void setDisappearAfterLanded(boolean value) {
    disappearAfterLanded = value;
  }
  
  @Override
  public boolean getDisappearAfterLanded() {
    return disappearAfterLanded;
  }
  
  @Override
  public void setCanDamageEMan(boolean value) {
    canDamageEMan = value;
  }
  
  @Override
  public boolean getCanDamageEMan() {
    return canDamageEMan;
  }
  
  @Override
  public String getName() { return "arrowCapability"; }
  
  public static class Storage implements Capability.IStorage<IArrowCapability> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IArrowCapability> capability, @NotNull IArrowCapability instance, EnumFacing side) {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setBoolean("DisappearAfterLanded", instance.getDisappearAfterLanded());
      compound.setBoolean("CanDamageEMan", instance.getCanDamageEMan());
      return compound;
    }
    
    @Override
    public void readNBT(Capability<IArrowCapability> capability, @NotNull IArrowCapability instance, EnumFacing side, NBTBase nbt) {
      NBTTagCompound compound = (NBTTagCompound) nbt;
      instance.setDisappearAfterLanded(compound.getBoolean("DisappearAfterLanded"));
      instance.setCanDamageEMan(compound.getBoolean("CanDamageEMan"));
    }
  }
  
  public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
    @CapabilityInject (IArrowCapability.class)
    public static Capability<IArrowCapability> ARROW_CAPABILITY = null;
    private final IArrowCapability             instance;
    
    public Provider() {
      instance = ARROW_CAPABILITY.getDefaultInstance();
    }
    
    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == ARROW_CAPABILITY;
    }
    
    @Nullable
    @Override
    public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
      return capability == ARROW_CAPABILITY ? ARROW_CAPABILITY.cast(instance) : null;
    }
    
    @Override
    public NBTTagCompound serializeNBT() {
      return (NBTTagCompound) ARROW_CAPABILITY.getStorage().writeNBT(ARROW_CAPABILITY, instance, null);
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
      ARROW_CAPABILITY.getStorage().readNBT(ARROW_CAPABILITY, instance, null, nbt);
    }
    
  }
  
  @Mod.EventBusSubscriber
  public static class Handler {
    @SubscribeEvent
    public static void onEvent(TickEvent.@NotNull WorldTickEvent event) {
      if (event.phase != TickEvent.Phase.START) { return; }
      final List<Entity> arrows = Lists.newArrayList();
      for (EntityArrow arrow : event.world.getEntities(EntityArrow.class, entity -> entity.inGround)) {
        if (!arrow.hasCapability(Provider.ARROW_CAPABILITY, null)) { continue; }
        if (NullHelper.checkNull(arrow.getCapability(Provider.ARROW_CAPABILITY, null)).getDisappearAfterLanded()) {
          arrows.add(arrow);
        }
      }
      for (int i = 0, iMax = arrows.size(); i < iMax; ++i) { if (!arrows.get(i).isDead) { arrows.get(i).setDead(); } }
    }
    
    @SubscribeEvent
    public static void onEvent(@NotNull AttachCapabilitiesEvent<EntityArrow> event) {
      if (event.getObject().hasCapability(Provider.ARROW_CAPABILITY, null)) { return; }
      event.addCapability(new ResourceLocation(EnderORUtils.MOD_ID, NullHelper.checkNull(Provider.ARROW_CAPABILITY.getDefaultInstance()).getName()), new Provider());
    }
  }
}
