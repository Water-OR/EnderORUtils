package io.github.enderor.items.baubles.ring;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class ItemMagneticRing extends Item implements IBauble, IHasRecipe, ITickable {
  private static final Map<EntityPlayer, List<EntityItem>> itemsNeedToPickUpByPlayer = Maps.newHashMap();
  private static final List<EntityPlayer>                  deadPlayers               = Lists.newArrayList();
  
  public ItemMagneticRing() {
    setMaxDamage(0);
    setMaxStackSize(1);
    EnderORItemHandler.addModel(this, 0, "inventory");
  }
  
  @Override
  public BaubleType getBaubleType(ItemStack itemStack) { return BaubleType.RING; }
  
  @Override
  public void onWornTick(ItemStack itemstack, @NotNull EntityLivingBase player) {
    if (!(player instanceof EntityPlayer)) { return; }
    AxisAlignedBB boxAABB  = player.getEntityBoundingBox();
    final boolean isRiding = player.isRiding() && !NullHelper.checkNull(player.getRidingEntity()).isDead;
    
    if (isRiding) { boxAABB = player.getEntityBoundingBox().union(player.getRidingEntity().getEntityBoundingBox()); }
    Set<EntityItem> itemSet = Sets.newHashSet(player.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, boxAABB.grow(1D, isRiding ? 0D : .5, 1D)));
    player.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, player.getEntityBoundingBox().grow(3D, 1D, 3D))
          .stream().filter(item -> !item.getItem().isEmpty()).filter(item -> !item.cannotPickup()).filter(item -> !itemSet.contains(item))
          .forEach(entityItem -> itemsNeedToPickUpByPlayer.computeIfAbsent((EntityPlayer) player, k -> Lists.newArrayList()).add(entityItem));
  }
  
  @Override
  public void makeRecipe(@NotNull EnderORRecipesHandler handler) {
    Ingredient emerald  = Ingredient.fromItem(Items.EMERALD);
    Ingredient redstone = Ingredient.fromItem(Items.REDSTONE);
    handler.addRecipe(new ShapedRecipe(
      "magnetic_ring_blank", 3, 3, getDefaultInstance(),
      redstone, emerald, Ingredient.EMPTY,
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
    if (container == null) { return new ActionResult<>(EnumActionResult.PASS, stack); }
    int fitSlot = IntStream.range(0, container.getSlots())
                           .filter(i -> container.isItemValidForSlot(i, stack, playerIn))
                           .findFirst().orElse(-1);
    if (fitSlot < 0) { return new ActionResult<>(EnumActionResult.FAIL, stack); }
    return new ActionResult<>(EnumActionResult.SUCCESS, container.insertItem(fitSlot, stack, false));
  }
  
  @Override
  public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) { return true; }
  
  @Override
  public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
    tooltip.add(I18n.format(getUnlocalizedNameInefficiently(stack) + ".description").trim());
  }
  
  @Override
  public void update() {
    itemsNeedToPickUpByPlayer.forEach((player, items) -> {
      if (player.isDead) { deadPlayers.add(player); } else {
        items.stream().filter(item -> !item.isDead).forEach(item -> item.onCollideWithPlayer(player));
        items.clear();
      }
    });
    deadPlayers.forEach(itemsNeedToPickUpByPlayer::remove);
    deadPlayers.clear();
  }
}
