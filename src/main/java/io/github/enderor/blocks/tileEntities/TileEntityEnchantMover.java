package io.github.enderor.blocks.tileEntities;

import io.github.enderor.EnderORUtils;
import io.github.enderor.containers.ContainerEnchantMover;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TileEntityEnchantMover extends TileEntityLockable implements ITickable {
  public int    tickCount;
  public double pageFlip;
  public double pageFlipPrev;
  public double flipT;
  public double flipA;
  public double bookSpread;
  public double bookSpreadPrev;
  public double bookRotation;
  public double bookRotationPrev;
  public double tRot;
  
  public final    int                    inventorySize = 2;
  protected final NonNullList<ItemStack> stacks        = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
  
  private static final Random rand = new Random();
  
  protected String customName;
  
  public TileEntityEnchantMover() { }
  
  @Override
  public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
    super.writeToNBT(compound);
    
    ItemStackHelper.saveAllItems(compound, stacks);
    
    if (hasCustomName()) { compound.setString("CustomName", customName); }
    return compound;
  }
  
  @Override
  public void readFromNBT(@NotNull NBTTagCompound compound) {
    super.readFromNBT(compound);
    
    ItemStackHelper.loadAllItems(compound, stacks);
    
    if (compound.hasKey("CustomName", 8)) { customName = compound.getString("CustomName"); }
  }
  
  @Override
  public void update() {
    bookSpreadPrev   = bookSpread;
    bookRotationPrev = bookRotation;
    EntityPlayer entityplayer = world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 3.0D, false);
    
    if (entityplayer != null) {
      double dx = entityplayer.posX - (pos.getX() + 0.5D);
      double dy = entityplayer.posZ - (pos.getZ() + 0.5D);
      tRot = MathHelper.atan2(dy, dx);
      bookSpread += 0.1D;
      
      if (bookSpread < 0.5D || rand.nextInt(40) == 0) {
        double flip = flipT;
        do { flipT += (rand.nextInt(4) - rand.nextInt(4)); } while (flip == flipT);
      }
    } else {
      tRot += 0.02D;
      bookSpread -= 0.1D;
    }
    
    while (bookRotation >= Math.PI) { bookRotation -= Math.PI * 2D; }
    while (bookRotation < -Math.PI) { bookRotation += Math.PI * 2D; }
    while (tRot >= Math.PI) { tRot -= Math.PI * 2D; }
    while (tRot < -Math.PI) { tRot += Math.PI * 2D; }
    double rotate = tRot - bookRotation;
    while (rotate >= Math.PI) { rotate -= Math.PI * 2D; }
    while (rotate < -Math.PI) { rotate += Math.PI * 2D; }
    
    bookRotation += rotate * 0.4F;
    bookSpread = MathHelper.clamp(bookSpread, 0.0D, 1.0D);
    ++tickCount;
    
    pageFlipPrev = pageFlip;
    double flip = (flipT - pageFlip) * 0.4D;
    flip = MathHelper.clamp(flip, -0.2D, 0.2D);
    flipA += (flip - flipA) * 0.9D;
    pageFlip += flipA;
  }
  
  @Override
  public int getSizeInventory() { return inventorySize; }
  
  @Override
  public boolean isEmpty() { return stacks.stream().allMatch(ItemStack::isEmpty); }
  
  @Override
  public @NotNull ItemStack getStackInSlot(int index) { return stacks.get(index); }
  
  @Override
  public @NotNull ItemStack decrStackSize(int index, int count) {
    ItemStack stack = ItemStackHelper.getAndSplit(stacks, index, count);
    if (!stack.isEmpty()) { markDirty(); }
    return stack;
  }
  
  @Override
  public @NotNull ItemStack removeStackFromSlot(int index) {
    ItemStack result = stacks.get(index);
    stacks.set(index, ItemStack.EMPTY);
    return result.isEmpty() ? ItemStack.EMPTY : result;
  }
  
  @Override
  public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
    if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) { stack.setCount(getSizeInventory()); }
    stacks.set(index, stack);
    markDirty();
  }
  
  @Override
  public int getInventoryStackLimit() { return 1; }
  
  @Override
  public boolean isUsableByPlayer(@NotNull EntityPlayer player) { return true; }
  
  @Override
  public void openInventory(@NotNull EntityPlayer player) { }
  
  @Override
  public void closeInventory(@NotNull EntityPlayer player) { }
  
  @Override
  public boolean isItemValidForSlot(int index, @NotNull ItemStack stack) { return stack.getCount() == 1; }
  
  @Override
  public int getField(int id) { return 0; }
  
  @Override
  public void setField(int id, int value) { }
  
  @Override
  public int getFieldCount() { return 0; }
  
  @Override
  public void clear() {
    Collections.fill(stacks, ItemStack.EMPTY);
  }
  
  @Override
  public @NotNull Container createContainer(@NotNull InventoryPlayer playerInventory, @NotNull EntityPlayer playerIn) { return new ContainerEnchantMover(playerInventory, this, playerIn); }
  
  @Override
  public @NotNull String getGuiID() { return EnderORUtils.MOD_ID + ":enchant_mover"; }
  
  @Override
  public @NotNull String getName() { return hasCustomName() ? customName : "container.enchant_mover"; }
  
  @Override
  public @NotNull ITextComponent getDisplayName() { return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName()); }
  
  @Override
  public boolean hasCustomName() { return customName != null && !customName.isEmpty(); }
  
  public TileEntityEnchantMover setCustomName(String customName) { this.customName = customName; return this; }
  
  protected Collection<Listener> listenerList = new HashSet<>();
  
  public void addListener(Listener listener) { listenerList.add(listener); }
  
  public void deleteListener(Listener listener) { listenerList.remove(listener); }
  
  public interface Listener {
    void onChange();
  }
  
  @Override
  public void markDirty() {
    super.markDirty();
    listenerList.forEach(Listener::onChange);
  }
  
  @Nullable
  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(pos, 0, stacks.get(0).serializeNBT());
  }
  
  @Override
  public void onDataPacket(@NotNull NetworkManager net, @NotNull SPacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    NBTTagCompound compound0 = pkt.getNbtCompound();
    NBTTagCompound compound1 = stacks.get(0).serializeNBT();
    if (compound0.equals(compound1)) { return; }
    stacks.set(0, new ItemStack(compound0));
    markDirty();
  }
}
