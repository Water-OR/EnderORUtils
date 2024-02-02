package io.github.enderor.mixins.minecraft;

import io.github.enderor.utils.EnchantsHelper;
import io.github.enderor.utils.NullHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin (value = ItemEnchantedBook.class, priority = 1001)
public abstract class MixinItemEnchantedBook extends Item {
  @Unique
  private static @Nullable Enchantment endeORUtils$enchant0;
  
  @Unique
  private static @Nullable Enchantment endeORUtils$enchant1;
  
  @Unique
  private static @NotNull String enderORUtils$enchantName0 = "";
  
  @SideOnly (Side.CLIENT)
  @Redirect (method = "addInformation", at = @At (value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;getCompoundTagAt(I)Lnet/minecraft/nbt/NBTTagCompound;", ordinal = 0))
  private @NotNull NBTTagCompound getEnchant0(@NotNull NBTTagList tagList, int index) {
    endeORUtils$enchant0 = EnchantsHelper.getEnchantInNBT(tagList.getCompoundTagAt(index));
    return tagList.getCompoundTagAt(index);
  }
  
  @SideOnly (Side.CLIENT)
  @Redirect (method = "addInformation", at = @At (value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;", ordinal = 0))
  private Enchantment returnEnchant0(int id) { return endeORUtils$enchant0; }
  
  @Redirect (method = "addEnchantment", at = @At (value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;getCompoundTagAt(I)Lnet/minecraft/nbt/NBTTagCompound;", ordinal = 0))
  private static @NotNull NBTTagCompound getEnchant1(@NotNull NBTTagList tagList, int index) {
    endeORUtils$enchant1 = EnchantsHelper.getEnchantInNBT(tagList.getCompoundTagAt(index));
    return tagList.getCompoundTagAt(index);
  }
  
  @Redirect (method = "addEnchantment", at = @At (value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;", ordinal = 0))
  private static Enchantment returnEnchant1(int id) { return endeORUtils$enchant1; }
  
  @Redirect (method = "addEnchantment", at = @At (value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentID(Lnet/minecraft/enchantment/Enchantment;)I", ordinal = 0))
  private static int getEnchantName0(@NotNull Enchantment enchantment) {
    enderORUtils$enchantName0 = NullHelper.getRegistryNameString(enchantment);
    return Enchantment.getEnchantmentID(enchantment);
  }
  
  @ModifyArg (method = "addEnchantment", at = @At (value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;appendTag(Lnet/minecraft/nbt/NBTBase;)V", ordinal = 0), index = 0)
  private static NBTBase setEnchantName0(@NotNull NBTBase compound) {
    if (compound instanceof NBTTagCompound) {
      ((NBTTagCompound) compound).setString(EnchantsHelper.TAG_ID, enderORUtils$enchantName0);
    }
    return compound;
  }
}
