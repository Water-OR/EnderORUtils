package io.github.enderor.utils;

import io.github.enderor.utils.actions.ActionNoI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class NullHelper {
  @Contract (value = "null -> fail; !null -> param1", pure = true)
  public static <T> @NotNull T checkNull(T value) {
    if (value == null) { throw new NullPointerException("Value is null!"); }
    return value;
  }
  
  public static <T> @Nullable T notNullOrElse(@Nullable T value, @NotNull ActionNoI<T> action) {
    return value == null ? action.accept() : value;
  }
  
  public static <T> boolean hasRegistryName(@NotNull IForgeRegistryEntry<T> entry) {
    return entry.getRegistryName() != null;
  }
  
  public static <T> void checkRegistryName(@NotNull IForgeRegistryEntry<T> entry) {
    if (!hasRegistryName(entry)) {
      throw new NullPointerException(String.format("Registry name of entry \"%s\" is null!", entry));
    }
  }
  
  public static <T> @NotNull ResourceLocation getRegistryName(@NotNull IForgeRegistryEntry<T> entry) {
    checkRegistryName(entry);
    assert entry.getRegistryName() != null;
    return entry.getRegistryName();
  }
  
  public static <T> @NotNull String getRegistryNameString(@NotNull IForgeRegistryEntry<T> entry) { return getRegistryName(entry).toString(); }
  
  @Contract ("null -> new; !null -> param1")
  public static @NotNull NBTTagCompound notNullCompound(@Nullable NBTTagCompound compound) {
    return compound == null ? new NBTTagCompound() : compound;
  }
  
  public static @NotNull NBTTagCompound getStackCompound(@NotNull ItemStack stack) {
    if (stack.getTagCompound() == null) { stack.setTagCompound(new NBTTagCompound()); }
    return stack.getTagCompound();
  }
}
