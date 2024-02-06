package io.github.enderor.items.baubles.ring;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import com.google.common.collect.Sets;
import io.github.enderor.items.EnderORItemHandler;
import io.github.enderor.recipes.EnderORRecipesHandler;
import io.github.enderor.recipes.IHasRecipe;
import io.github.enderor.recipes.ShapedRecipe;
import io.github.enderor.utils.NullHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class ItemMagneticRing extends Item implements IBauble, IHasRecipe {
  
  public ItemMagneticRing() {
    setMaxDamage(0);
    setMaxStackSize(1);
    EnderORItemHandler.addModel(this, 0, "inventory");
  }
  
  @Override
  public BaubleType getBaubleType(ItemStack itemStack) { return BaubleType.RING; }
  
  @Override
  public void onWornTick(ItemStack itemstack, @NotNull EntityLivingBase playerIn) {
    if (!(playerIn instanceof EntityPlayer)) { return; }
    final AxisAlignedBB boxAABB  = playerIn.getEntityBoundingBox();
    final boolean       isRiding = playerIn.isRiding() && !NullHelper.checkNull(playerIn.getRidingEntity()).isDead;
    
    if (isRiding) { boxAABB.union(playerIn.getRidingEntity().getEntityBoundingBox()); }
    final Set<EntityItem> itemSet = Sets.newHashSet(playerIn.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, boxAABB.grow(1D, isRiding ? 0D : .5, 1D)));
    for (EntityItem item : playerIn.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, boxAABB.grow(3D, 1D, 3D))) {
      if (itemSet.contains(item) || item.cannotPickup() || item.getItem().isEmpty() || item.isDead) { continue; }
      item.onCollideWithPlayer(((EntityPlayer) playerIn));
    }
  }
  
  @Override
  public void makeRecipe(@NotNull EnderORRecipesHandler handler) {
    Ingredient emerald  = Ingredient.fromItem(Items.EMERALD);
    Ingredient redStone = Ingredient.fromItem(Items.REDSTONE);
    handler.addRecipe(new ShapedRecipe(
      "magnetic_ring_blank", 3, 3, getDefaultInstance(),
      redStone, emerald, Ingredient.EMPTY,
      emerald, Ingredient.EMPTY, emerald,
      Ingredient.EMPTY, emerald, Ingredient.EMPTY
    ));
  }
  
  @Override
  public @NotNull ItemStack getDefaultInstance() { return new ItemStack(this, 1, 0); }
  
  @Override
  public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, @NotNull EntityPlayer playerIn, @NotNull EnumHand handIn) {
    final ItemStack        stack     = playerIn.getHeldItem(handIn);
    final BaublesContainer container = (BaublesContainer) playerIn.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
    if (container == null) { return new ActionResult<>(EnumActionResult.FAIL, stack); }
    int bound = container.getSlots();
    for (int i = 0; i < bound; i++) {
      if (!container.isItemValidForSlot(i, stack, playerIn)) { continue; }
      if (!container.getStackInSlot(i).isEmpty()) { continue; }
      return new ActionResult<>(EnumActionResult.SUCCESS, container.insertItem(i, stack, false));
    }
    return new ActionResult<>(EnumActionResult.FAIL, stack);
  }
  
  @Override
  public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) { return true; }
  
  @Override
  public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
    tooltip.add(I18n.format(getUnlocalizedNameInefficiently(stack) + ".description").trim());
  }
}
