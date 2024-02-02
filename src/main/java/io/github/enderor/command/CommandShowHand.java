package io.github.enderor.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandShowHand extends EnderORCommandBase {
  @Override
  public @NotNull String getName() {
    return "showhand";
  }
  
  @Override
  public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args) throws CommandException {
    if (!(sender instanceof EntityPlayer)) { return; }
    StringBuilder builder = new StringBuilder();
    String nbt = ((EntityPlayer) sender).getHeldItemMainhand().serializeNBT().toString()
                                        .replace("\n", "\\n")
                                        .replace("\r", "\\r")
                                        .replace("§", "§§");
    final @NotNull String prefixSpace = getPrefixSpace(args.length > 0 ? args[0] : null);
    int                   level       = 0;
    boolean               isInString  = false;
    for (int i = 0, iMax = nbt.length(); i < iMax; ++i) {
      char thisC = nbt.charAt(i);
      if (thisC == '\"' || thisC == '\'') { isInString = !isInString; }
      if (!isInString) {
        if (thisC == ':') {
          builder.append(": ");
          continue;
        } else if (thisC == ']' || thisC == '}') {
          --level;
          endLine(builder, level, prefixSpace);
          builder.append(thisC);
          continue;
        }
      }
      builder.append(thisC);
      if (isInString) { continue; }
      char nextC = nbt.charAt(i + 1);
      if (thisC == '{' || thisC == '[') {
        if ((thisC == '{' && nextC == '}') ||
            (thisC == '[' && nextC == ']')) {
          builder.append(' ').append(nextC);
          ++i;
        } else {
          ++level;
          endLine(builder, level, prefixSpace);
        }
      } else if (thisC == ',') {
        builder.append(' ');
      }
    }
    sender.sendMessage(new TextComponentString(builder.toString()));
  }
  
  private static void endLine(@NotNull StringBuilder builder, int level, String prefixSpace) {
    builder.append('\n');
    for (int j = 0; j < level; ++j) { builder.append(prefixSpace); }
  }
  
  public @NotNull String getPrefixSpace(@Nullable String arg0) throws CommandException {
    int prefixSpaceCount = 4;
    if (arg0 != null) {
      try {
        prefixSpaceCount = Integer.parseInt(arg0);
      } catch (NumberFormatException exception) {
        throw new WrongUsageException("arg[0] should be a integer, but we have " + arg0);
      }
    }
    StringBuilder prefixSpaceBuilder = new StringBuilder();
    for (; prefixSpaceCount > 0; --prefixSpaceCount) { prefixSpaceBuilder.append(' '); }
    return prefixSpaceBuilder.toString();
  }
  
  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }
}
