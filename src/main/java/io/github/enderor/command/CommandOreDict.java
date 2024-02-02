package io.github.enderor.command;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandOreDict extends EnderORCommandBase {
  @Override
  public @NotNull String getName() {
    return "oredict";
  }
  
  @Override
  public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args) throws CommandException {
    if (!(sender instanceof EntityPlayer)) { return; }
    if (args.length == 0) {
      StringBuilder builder  = new StringBuilder("Ore dictionaries\n");
      String[]      oreNames = OreDictionary.getOreNames();
      if (oreNames.length == 0) { return; }
      Arrays.stream(oreNames).sorted(String::compareTo).forEachOrdered(s -> builder.append('\n').append(s));
      sender.sendMessage(new TextComponentString(builder.toString()));
    } else {
      String oreDictName = args[0];
      if (!OreDictionary.doesOreNameExist(oreDictName)) {
        throw new WrongUsageException("Ore dict with name \"" + oreDictName + "\" doesn't existed!");
      }
      StringBuilder builder    = new StringBuilder("Items/Blocks in ore dictionary ").append(oreDictName);
      List<String>  stackNames = OreDictionary.getOres(oreDictName).stream().map(stack -> stack.getItem().getRegistryName()).filter(Objects::nonNull).map(ResourceLocation::toString).collect(Collectors.toList());
      if (stackNames.isEmpty()) { builder.append(" is EMPTY!"); } else {
        stackNames.stream().sorted(String::compareTo).forEachOrdered(s -> builder.append('\n').append(s));
      }
      sender.sendMessage(new TextComponentString(builder.toString()));
    }
  }
  
  @Override
  public @NotNull List<String> getTabCompletions(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args, @Nullable BlockPos targetPos) {
    ArrayList<String> result = Lists.newArrayList();
    if (args.length > 1) { return result; }
    Arrays.stream(OreDictionary.getOreNames()).sorted(String::compareTo).forEachOrdered(result::add);
    if (args.length == 1) {
      result.removeIf(s -> !s.startsWith(args[0]));
    }
    return result;
  }
}
