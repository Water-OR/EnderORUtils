package io.github.enderor.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.enderor.EnderORUtils;
import io.github.enderor.client.utils.EnchantsHelperClient;
import io.github.enderor.config.EnchantsMaxLevel;
import io.github.enderor.recipes.EnderORRecipesHandler;
import io.github.enderor.recipes.IHasRecipe;
import io.github.enderor.recipes.ShapedRecipe;
import io.github.enderor.recipes.ShapelessRecipe;
import io.github.enderor.utils.EnchantsHelper;
import io.github.enderor.utils.NullHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreIngredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ItemEnchantedPaper extends Item implements IHasRecipe {
  public ItemEnchantedPaper() {
    setMaxDamage(0);
    setMaxStackSize(1);
    EnderORItemHandler.addModel(this, 0, "inventory");
    EnchantsMaxLevel.addListener(this::calcSubItems);
  }
  
  @Override
  public void makeRecipe(@NotNull EnderORRecipesHandler handler) {
    Ingredient string = new OreIngredient("string");
    handler.addRecipe(new ShapedRecipe(
      "enchanted_paper_blank_0", 3, 3, getDefaultInstance(),
      Ingredient.EMPTY, string, Ingredient.EMPTY,
      string, new OreIngredient("paper"), string,
      Ingredient.EMPTY, string, Ingredient.EMPTY
    ), new ShapelessRecipe("enchanted_paper_clear", getDefaultInstance(), Ingredient.fromStacks(new ItemStack(this, 1, Short.MAX_VALUE))) {
      @Override
      public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        return resetEnchant(super.getCraftingResult(inv));
      }
    });
  }
  
  @Override
  public @NotNull ItemStack getDefaultInstance() { return resetEnchant(new ItemStack(this, 1, 0)); }
  
  private final List<ItemStack> subItemsAll     = Lists.newArrayList();
  private final List<ItemStack> subItemsMax     = Lists.newArrayList();
  private       boolean         isSubItemsReady = false;
  
  public void calcSubItems() {
    if (!(EnchantsHelper.hasEnchantsInit() && EnchantsMaxLevel.hasConfigPrepared())) { return; }
    subItemsMax.clear();
    subItemsAll.clear();
    subItemsAll.add(getDefaultInstance());
    final Map<Enchantment, Integer> all_max_enchantment = Maps.newHashMap();
    EnchantsHelper.getEnchantsAppeared().forEach(enchantment -> IntStream.rangeClosed(1, EnchantsMaxLevel.getMaxLevel(enchantment)).mapToObj(i -> setEnchant(getDefaultInstance(), enchantment, i)).forEachOrdered(subItemsAll::add));
    EnchantsHelper.getEnchantsAppeared().forEach(enchantment -> subItemsMax.add(setEnchant(getDefaultInstance(), enchantment, EnchantsMaxLevel.getMaxLevel(enchantment))));
    EnchantsHelper.getEnchantsAppeared().forEach(enchantment -> all_max_enchantment.put(enchantment, EnchantsMaxLevel.getMaxLevel(enchantment)));
    subItemsAll.add(setEnchants(getDefaultInstance(), all_max_enchantment));
    subItemsMax.add(setEnchants(getDefaultInstance(), all_max_enchantment));
    isSubItemsReady = true;
  }
  
  @Override
  public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
    if (!isSubItemsReady) { calcSubItems(); }
    if (tab.equals(EnderORUtils.MOD_TAB)) {
      items.addAll(subItemsMax);
    } else if (tab.equals(CreativeTabs.SEARCH)) {
      items.addAll(subItemsAll);
    }
  }
  
  @Override
  public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (!hasEnchants(stack)) {
      tooltip.add(I18n.format(getTranslationKey() + ".empty.description").trim());
      return;
    }
    Map<Enchantment, Integer> enchants = getEnchants(stack);
    enchants.forEach((enchant, level) -> tooltip.add(EnchantsHelperClient.getEnchantText(enchant, level)));
  }
  
  @Override
  public boolean hasEffect(@NotNull ItemStack stack) { return super.hasEffect(stack) || hasEnchants(stack); }
  
  // EnchantedPaper's special Enchanting
  public static final String ENCHANT_TAG       = "enchants";
  public static final String ENCHANT_TAG_ID    = "id";
  public static final String ENCHANT_TAG_LEVEL = "lvl";
  
  public static @NotNull ItemStack resetEnchant(@NotNull ItemStack stack) {
    if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); } else {
      NullHelper.checkNull(stack.getTagCompound()).removeTag(ENCHANT_TAG);
    }
    return stack;
  }
  
  @Contract ("_ -> new")
  public static @NotNull Map<Enchantment, Integer> getEnchants(@NotNull ItemStack itemStack) {
    final Map<Enchantment, Integer> enchants = new HashMap<>();
    
    if (!itemStack.hasTagCompound()) { return enchants; }
    final NBTTagCompound nbt = NullHelper.checkNull(itemStack.getTagCompound());
    
    if (!nbt.hasKey(ENCHANT_TAG, 9)) { return enchants; }
    final NBTTagList tagList = nbt.getTagList(ENCHANT_TAG, 10);
    
    if (tagList.isEmpty()) { return enchants; }
    NBTTagCompound tagCompound;
    
    for (int i = 0, iMax = tagList.tagCount(); i < iMax; ++i) {
      tagCompound = tagList.getCompoundTagAt(i);
      
      if (tagCompound.hasKey(ENCHANT_TAG_ID, 8) &&
          tagCompound.hasKey(ENCHANT_TAG_LEVEL, 3)) {
        Enchantment enchantment = Enchantment.getEnchantmentByLocation(tagCompound.getString(ENCHANT_TAG_ID));
        int         level       = tagCompound.getInteger(ENCHANT_TAG_LEVEL);
        
        if (enchantment == null || level == 0) { continue; }
        enchants.put(enchantment, level);
      }
    }
    return EnchantsHelper.sortEnchants(enchants);
  }
  
  @Contract ("_, _ -> param1")
  public static @NotNull ItemStack setEnchants(@NotNull ItemStack stack, @NotNull Map<Enchantment, Integer> enchants) {
    resetEnchant(stack);
    if (enchants.isEmpty()) { return stack; }
    final NBTTagList     tagList     = new NBTTagList();
    final NBTTagCompound tagCompound = new NBTTagCompound();
    
    enchants.forEach((enchantment, integer) -> {
      tagCompound.setString(ENCHANT_TAG_ID, NullHelper.getRegistryNameString(enchantment));
      tagCompound.setInteger(ENCHANT_TAG_LEVEL, integer);
      tagList.appendTag(tagCompound.copy());
    });
    NullHelper.checkNull(stack.getTagCompound()).setTag(ENCHANT_TAG, tagList);
    return stack;
  }
  
  public static @NotNull ItemStack setEnchant(@NotNull ItemStack stack, @NotNull Enchantment enchant, int level) {
    Map<Enchantment, Integer> enchants = new HashMap<>();
    enchants.put(enchant, level);
    return setEnchants(stack, enchants);
  }
  
  @Contract (pure = true)
  public static boolean hasEnchants(@NotNull ItemStack stack) { return stack.hasTagCompound() && NullHelper.checkNull(stack.getTagCompound()).hasKey(ENCHANT_TAG, 9); }
}
