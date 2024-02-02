package io.github.enderor.containers;

import io.github.enderor.blocks.tileEntities.TileEntityEnchantMover;
import io.github.enderor.utils.actions.ActionNoIO;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class ContainerEnchantMover extends Container {
  public final TileEntityEnchantMover tileEnchantMover;
  public final InventoryPlayer        inventoryPlayer;
  
  public ContainerEnchantMover(InventoryPlayer playerInv, TileEntityEnchantMover tileEnchantMover, EntityPlayer user) {
    this.tileEnchantMover = tileEnchantMover;
    this.tileEnchantMover.openInventory(user);
    this.tileEnchantMover.addListener(this::onTileUpdate);
    inventoryPlayer = playerInv;
    addSlotToContainer(new EnchantSlot(0, 70, 134));
    addSlotToContainer(new EnchantSlot(1, 170, 134));
    IntStream.range(0, 27).mapToObj(index -> new Slot(inventoryPlayer, index + 9, 48 + (index % 9) * 18, 156 + (index / 9) * 18)).forEachOrdered(this::addSlotToContainer);
    IntStream.range(0, 9).mapToObj(index -> new Slot(inventoryPlayer, index, 48 + index * 18, 214)).forEachOrdered(this::addSlotToContainer);
  }
  
  private void onTileUpdate() {
    IntStream.range(0, tileEnchantMover.inventorySize).forEach(i -> inventoryItemStacks.set(i, tileEnchantMover.getStackInSlot(i)));
    refreshAll();
  }
  
  @Override
  public boolean canInteractWith(@NotNull EntityPlayer playerIn) { return tileEnchantMover.isUsableByPlayer(playerIn); }
  
  @Override
  public @NotNull ItemStack transferStackInSlot(@NotNull EntityPlayer playerIn, int index) {
    Slot slot = inventorySlots.get(index);
    
    if (slot == null || !slot.getHasStack()) { return ItemStack.EMPTY; }
    final ItemStack result        = slot.getStack().copy();
    final int       inventorySize = tileEnchantMover.inventorySize;
    
    if (index < inventorySize) {
      if (!mergeItemStack(result, inventorySize, inventorySlots.size(), true)) { return ItemStack.EMPTY; }
    } else {
      if (!mergeItemStack(result, 0, inventorySize, false)) { return ItemStack.EMPTY; }
    }
    
    if (result.isEmpty()) { slot.putStack(ItemStack.EMPTY); } else { slot.onSlotChanged(); }
    return result;
  }
  
  @Override
  public void detectAndSendChanges() {
    super.detectAndSendChanges();
    onTileUpdate();
    refreshAll();
  }
  
  @Override
  public void onContainerClosed(@NotNull EntityPlayer playerIn) {
    super.onContainerClosed(playerIn);
    detectAndSendChanges();
    tileEnchantMover.closeInventory(playerIn);
    tileEnchantMover.deleteListener(this::onTileUpdate);
    listeners.clear();
  }
  
  protected NonNullList<Map<Enchantment, Integer>> enchantsList = NonNullList.withSize(2, new HashMap<>());
  protected NonNullList<ActionNoIO>                listeners    = NonNullList.create();
  
  public void addListener(ActionNoIO listener) { listeners.add(listener); }
  
  private void refreshAll() { listeners.forEach(ActionNoIO::apply); }
  
  public void calcEnchants() {
    if (!needRefresh) { return; }
    needRefresh = false;
    enchantsList.set(0, EnchantmentHelper.getEnchantments(getSlot(0).getStack()));
    enchantsList.set(1, EnchantmentHelper.getEnchantments(getSlot(1).getStack()));
    refreshAll();
  }
  
  public Map<Enchantment, Integer> getEnchantsInSlot(int slot) {
    calcEnchants();
    return enchantsList.get(slot);
  }
  
  protected boolean needRefresh = true;
  
  protected class EnchantSlot extends Slot {
    public EnchantSlot(int index, int xPosition, int yPosition) {
      super(tileEnchantMover, index, xPosition, yPosition);
    }
    
    @Override
    public void onSlotChanged() {
      super.onSlotChanged();
      needRefresh = true;
    }
  }
  
  @SideOnly(Side.CLIENT)
  public ContainerEnchantMover setEnchants0(Map<Enchantment, Integer> enchants) {
    ItemStack stack = getSlot(0).getStack().copy();
    if (stack.isEmpty()) { return this; }
    EnchantmentHelper.setEnchantments(enchants, stack);
    getSlot(0).putStack(stack);
    needRefresh = true;
    return this;
  }
}
