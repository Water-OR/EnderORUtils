package io.github.enderor.items.baubles.ring;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.enderor.config.EnderORConfigs;
import io.github.enderor.items.EnderORItemHandler;
import io.github.enderor.recipes.*;
import io.github.enderor.utils.EffectHelper;
import io.github.enderor.utils.NullHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreIngredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

public class ItemPotionRing extends Item implements IBauble, IHasRecipe {
  public ItemPotionRing() {
    setMaxDamage(0);
    setMaxStackSize(1);
    EnderORItemHandler.addModel(this, 0, "inventory");
  }
  
  @Override
  public void makeRecipe(@NotNull EnderORRecipesHandler handler) {
    final Ingredient ingotGold  = new OreIngredient("ingotGold");
    final Ingredient potionAdd  = Ingredient.fromStacks(new ItemStack(Items.POTIONITEM, 1, Short.MAX_VALUE));
    final Ingredient potionRing = new CustomIngredient((stackList, stack) -> stackList.stream().anyMatch(stack1 -> stack1.getItem() == stack.getItem()), getDefaultInstance());
    handler.addRecipe(new ShapedRecipe(
      "potion_ring_blank", 3, 3, getDefaultInstance(),
      new OreIngredient("nuggetIron"), ingotGold, Ingredient.EMPTY,
      ingotGold, Ingredient.EMPTY, ingotGold,
      Ingredient.EMPTY, ingotGold, Ingredient.EMPTY
    ), new ShapelessRecipe("potion_ring_clear", getDefaultInstance(), potionRing) {
      @Override
      public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
          final ItemStack stack = inv.getStackInSlot(i);
          if (stack.getItem() instanceof ItemPotionRing) { return resetEffect(stack.copy()); }
        }
        return super.getCraftingResult(inv);
      }
    }, new ShapelessRecipe(
      "potion_ring_merge", getDefaultInstance(), potionRing, potionAdd) {
      @Override
      public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        final Map<Potion, Integer> result      = Maps.newHashMap();
        ItemStack                  resultStack = null;
        
        for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
          ItemStack stack = inv.getStackInSlot(i);
          if (stack.isEmpty()) { continue; }
          if (stack.getItem() instanceof ItemPotionRing) { resultStack = stack; } else {
            EffectHelper.mergeEffects(result, PotionUtils.getEffectsFromStack(stack));
          }
        }
        if (resultStack == null) { return setEffects(super.getCraftingResult(inv), result); } else {
          return setEffects(resultStack.copy(), EffectHelper.mergeEffects(result, getEffects(resultStack)));
        }
      }
      
      @Override
      public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
        if (!canFit(inv.getWidth(), inv.getHeight())) { return false; }
        boolean[] isMatched = new boolean[inv.getSizeInventory()];
        Arrays.fill(isMatched, false);
        for (int i = 0; i < getSize(); ++i) {
          int matchIndex = -1;
          for (int j = 0, jMax = inv.getSizeInventory(); j < jMax; ++j) {
            if (isMatched[j] || !inputs.get(i).apply(inv.getStackInSlot(j))) { continue; }
            matchIndex = j;
            break;
          }
          if (matchIndex < 0) { return false; }
          isMatched[matchIndex] = true;
        }
        final ItemStack dummyPotion = Items.POTIONITEM.getDefaultInstance();
        for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
          if (!isMatched[i] && !inv.getStackInSlot(i).isEmpty() && !CustomIngredient.isMatched(inv.getStackInSlot(i), dummyPotion)) {
            return false;
          }
        }
        return true;
      }
      
      @Override
      public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
        NonNullList<ItemStack> result = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0, iMax = inv.getSizeInventory(); i < iMax; ++i) {
          ItemStack stack = inv.getStackInSlot(i);
          if (!(stack.getItem() instanceof ItemPotion)) { continue; }
          ItemStack copy = stack.copy();
          copy.setCount(1);
          result.set(i, copy);
        }
        return result;
      }
    });
  }
  
  @Override
  public @NotNull ItemStack getDefaultInstance() {
    return resetEffect(new ItemStack(this, 1, 0));
  }
  
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
  public BaubleType getBaubleType(ItemStack itemStack) { return BaubleType.RING; }
  
  @Override
  public void onEquipped(ItemStack itemstack, @NotNull EntityLivingBase player) { getEffects(itemstack).forEach((potion, integer) -> player.addPotionEffect(new PotionEffect(potion, EnderORConfigs.EFFECT_LENGTH, integer))); }
  
  @Override
  public void onUnequipped(ItemStack itemstack, EntityLivingBase player) { getEffects(itemstack).forEach((potion, integer) -> player.removePotionEffect(potion)); }
  
  @Override
  public void onWornTick(ItemStack itemstack, @NotNull EntityLivingBase player) {
    getEffects(itemstack).forEach((potion, level) -> player.addPotionEffect(new PotionEffect(potion, EnderORConfigs.EFFECT_LENGTH, level)));
  }
  
  @Override
  public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) { return true; }
  
  @Override
  public @NotNull String getItemStackDisplayName(@NotNull ItemStack stack) {
    Map<Potion, Integer> potions = getEffects(stack);
    if (potions.isEmpty()) { return super.getItemStackDisplayName(stack); }
    if (potions.size() == 1) {
      String potionName = I18n.format(potions.keySet().toArray(new Potion[0])[0].getName()).trim();
      return I18n.format(getUnlocalizedNameInefficiently(stack) + ".name.single", potionName).trim();
    }
    return I18n.format(getUnlocalizedNameInefficiently(stack) + ".name.multiple").trim();
  }
  
  @Override
  public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
    if (!isInCreativeTab(tab)) { return; }
    items.add(getDefaultInstance());
    final Map<Potion, Set<Integer>> effectsAppeared = EffectHelper.getEffectsAppeared();
    final Map<Potion, Integer>      maxLevels       = Maps.newTreeMap(Comparator.comparingInt(Potion::getIdFromPotion));
    
    Potion[] potions = effectsAppeared.keySet().stream().sorted(Comparator.comparingInt(Potion::getIdFromPotion)).toArray(Potion[]::new);
    for (int i = 0, j, iMax = potions.length; i < iMax; ++i) {
      Integer[] levels = effectsAppeared.get(potions[i]).stream().sorted(Integer::compare).toArray(Integer[]::new);
      for (j = 0; j < levels.length; ++j) {
        items.add(setEffect(getDefaultInstance(), potions[i], levels[j]));
        EffectHelper.mergeEffect(maxLevels, potions[i], levels[j]);
      }
    }
    
    final List<PotionType> effectsMultiple = EffectHelper.getMultipleEffectsPotions();
    final Map<Potion, Integer> effectsIn = Maps.newTreeMap(Comparator.comparingInt(Potion::getIdFromPotion));
    for (int i = 0, j, iMax = effectsMultiple.size(); i < iMax; ++i) {
      effectsIn.clear();
      effectsMultiple.get(i).getEffects().forEach(effect -> EffectHelper.mergeEffect(effectsIn, effect));
      items.add(setEffects(getDefaultInstance(), effectsIn));
      EffectHelper.mergeEffects(maxLevels, effectsIn);
    }
    items.add(setEffects(getDefaultInstance(), maxLevels));
  }
  
  @Override
  public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (!hasEffects(stack)) {
      tooltip.add(I18n.format(getUnlocalizedNameInefficiently(stack) + ".empty.description"));
      return;
    }
    tooltip.add(I18n.format(getUnlocalizedNameInefficiently(stack) + ".filled.description"));
    Map<Potion, Integer> effects = getEffects(stack);
    effects.forEach((potion, level) -> tooltip.add(EffectHelper.getEffectText(potion, level)));
  }
  
  @Override
  public boolean hasEffect(@NotNull ItemStack stack) { return super.hasEffect(stack) || hasEffects(stack); }
  
  // PotionRing's special EffectControlling
  public static final String EFFECT_TAG   = "effects";
  public static final String EFFECT_ID    = "id";
  public static final String EFFECT_LEVEL = "lvl";
  
  @Contract ("_ -> param1")
  public static @NotNull ItemStack resetEffect(@NotNull ItemStack stack) {
    if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); } else {
      NullHelper.checkNull(stack.getTagCompound()).removeTag(EFFECT_TAG);
    }
    return stack;
  }
  
  @Contract ("_ -> new")
  public static @NotNull Map<Potion, Integer> getEffects(@NotNull ItemStack stack) {
    final Map<Potion, Integer> effects = new HashMap<>();
    
    if (!(stack.getItem() instanceof ItemPotionRing) || !stack.hasTagCompound()) { return effects; }
    final NBTTagCompound nbt = NullHelper.getStackCompound(stack);
    
    if (!nbt.hasKey(EFFECT_TAG, 9)) { return effects; }
    final NBTTagList tagList = nbt.getTagList(EFFECT_TAG, 10);
    
    if (tagList.isEmpty()) { return effects; }
    NBTTagCompound tagCompound;
    
    for (int i = 0, iMax = tagList.tagCount(); i < iMax; ++i) {
      tagCompound = tagList.getCompoundTagAt(i);
      
      if (tagCompound.hasKey(EFFECT_ID, 8) &&
          tagCompound.hasKey(EFFECT_LEVEL, 3)) {
        Potion potion = Potion.getPotionFromResourceLocation(tagCompound.getString(EFFECT_ID));
        int    level  = tagCompound.getInteger(EFFECT_LEVEL);
        
        if (potion == null) { continue; }
        effects.put(potion, level);
      }
    }
    return EffectHelper.sortEffects(effects);
  }
  
  @Contract ("_, _ -> param1")
  public static @NotNull ItemStack setEffects(@NotNull ItemStack stack, @NotNull Map<Potion, Integer> effects) {
    resetEffect(stack);
    if (!(stack.getItem() instanceof ItemPotionRing)) { return stack; }
    final NBTTagList     tagList  = new NBTTagList();
    final NBTTagCompound compound = new NBTTagCompound();
    
    effects.forEach((potion, level) -> {
      compound.setString(EFFECT_ID, NullHelper.getRegistryNameString(potion));
      compound.setInteger(EFFECT_LEVEL, level);
      tagList.appendTag(compound.copy());
    });
    NullHelper.getStackCompound(stack).setTag(EFFECT_TAG, tagList);
    return stack;
  }
  
  public static @NotNull ItemStack setEffect(@NotNull ItemStack stack, Potion potion, int level) {
    Map<Potion, Integer> effects = Maps.newHashMap();
    effects.put(potion, level);
    return setEffects(stack, effects);
  }
  
  @Contract (pure = true)
  public static boolean hasEffects(@NotNull ItemStack itemStack) { return itemStack.hasTagCompound() && NullHelper.checkNull(itemStack.getTagCompound()).hasKey(EFFECT_TAG, 9); }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    private static final List<PotionEffect> effectList = Lists.newArrayList();
    
    @SubscribeEvent
    public static void onEvent(@NotNull ColorHandlerEvent.Item event) {
      event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
        if (tintIndex != 0) { return -1; }
        effectList.clear();
        getEffects(stack).forEach((potion, level) -> effectList.add(new PotionEffect(potion, 1, level)));
        return effectList.isEmpty() ? -1 : PotionUtils.getPotionColorFromEffectList(effectList);
      }, EnderORItemHandler.ITEM_POTION_RING);
    }
  }
}
