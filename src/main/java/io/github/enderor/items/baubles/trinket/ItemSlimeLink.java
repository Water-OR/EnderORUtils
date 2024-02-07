package io.github.enderor.items.baubles.trinket;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import io.github.enderor.capabilities.PlayerCapability;
import io.github.enderor.items.EnderORItemHandler;
import io.github.enderor.recipes.EnderORRecipesHandler;
import io.github.enderor.recipes.IHasRecipe;
import io.github.enderor.recipes.ShapedRecipe;
import io.github.enderor.utils.NullHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemSlimeLink extends Item implements IBauble, IHasRecipe {
  public ItemSlimeLink() {
    setMaxDamage(0);
    setMaxStackSize(1);
    EnderORItemHandler.addModel(this, 0, "inventory");
  }
  
  @Override
  public void onEquipped(ItemStack itemstack, @NotNull EntityLivingBase player) {
    if (player.hasCapability(PlayerCapability.Provider.PLAYER_CAPABILITY, null)) {
      NullHelper.checkNull(player.getCapability(PlayerCapability.Provider.PLAYER_CAPABILITY, null)).setNoKB(true);
    }
  }
  
  @Override
  public void onUnequipped(ItemStack itemstack, @NotNull EntityLivingBase player) {
    if (player.hasCapability(PlayerCapability.Provider.PLAYER_CAPABILITY, null)) {
      NullHelper.checkNull(player.getCapability(PlayerCapability.Provider.PLAYER_CAPABILITY, null)).setNoKB(false);
    }
  }
  
  @Override
  public BaubleType getBaubleType(ItemStack itemStack) { return BaubleType.TRINKET; }
  
  @Override
  public void makeRecipe(@NotNull EnderORRecipesHandler handler) {
    final Ingredient slimeball = new OreIngredient("slimeball");
    final Ingredient emptySlot = Ingredient.EMPTY;
    handler.addRecipe(new ShapedRecipe(
      "slime_link_blank", 3, 3, getDefaultInstance(),
      emptySlot, slimeball, emptySlot,
      slimeball, emptySlot, slimeball,
      emptySlot, slimeball, emptySlot
    ));
  }
  
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
  public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
    tooltip.add(I18n.format(getUnlocalizedNameInefficiently(stack) + ".description"));
  }
}
