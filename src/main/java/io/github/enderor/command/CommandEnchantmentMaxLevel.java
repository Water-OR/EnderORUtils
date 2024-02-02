package io.github.enderor.command;

import com.google.common.collect.Lists;
import io.github.enderor.config.EnchantsMaxLevel;
import io.github.enderor.network.EnderORNetworkHandler;
import io.github.enderor.network.client.SPacketEnchantMaxLevelChange;
import io.github.enderor.utils.EnchantsHelper;
import io.github.enderor.utils.NullHelper;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandEnchantmentMaxLevel extends EnderORCommandBase {
  @Override
  public @NotNull String getName() {
    return "enchantment_max_level";
  }
  
  @Override
  public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args) throws CommandException {
    if (args.length == 0) {
      StringBuilder builder = new StringBuilder("Enchantments Appeared:\n");
      EnchantsHelper.getEnchantsAppeared().forEach(enchantment -> builder.append(NullHelper.getRegistryNameString(enchantment)).append(' ').append(EnchantsMaxLevel.getMaxLevel(enchantment)).append('\n'));
      sender.sendMessage(new TextComponentString(builder.toString()));
    } else {
      Enchantment enchantment = Enchantment.getEnchantmentByLocation(args[0]);
      if (enchantment == null) {
        throw new WrongUsageException("Enchantment %s doesn't existed!", args[0]);
      }
      if (args.length > 1) {
        int newLevel;
        try {
          newLevel = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
          throw new NumberInvalidException("%s is not a legal number!", args[1]);
        }
        if (!EnchantsMaxLevel.checkLevelLegal(enchantment, newLevel)) {
          throw new NumberInvalidException("%s is not allowed, it should be in range [%s,%s]", args[1], enchantment.getMaxLevel(), Integer.MAX_VALUE);
        }
        EnderORNetworkHandler.INSTANCE.sendToAll(new SPacketEnchantMaxLevelChange(enchantment, newLevel));
      }
    }
  }
  
  @Override
  public @NotNull List<String> getTabCompletions(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args, @Nullable BlockPos targetPos) {
    if (args.length > 1) { return Lists.newArrayList(); }
    Stream<Enchantment> stream = EnchantsHelper.getEnchantsAppeared().stream();
    if (args.length == 1) {
      stream = stream.filter(enchantment -> NullHelper.getRegistryNameString(enchantment).startsWith(args[0]));
    }
    return stream.map(NullHelper::getRegistryNameString).collect(Collectors.toList());
  }
}
