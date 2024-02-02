package io.github.enderor.command;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderORCommandHandler {
  public static final  EnderORCommandHandler INSTANCE = new EnderORCommandHandler();
  private static final List<ICommand>        COMMANDS;
  
  public void register(@NotNull FMLServerStartingEvent event) { COMMANDS.forEach(event::registerServerCommand); }
  
  static {
    COMMANDS = Lists.newArrayList();
    COMMANDS.add(COMMAND_ORE_DICT = new CommandOreDict());
    COMMANDS.add(COMMAND_SHOW_HAND = new CommandShowHand());
    COMMANDS.add(COMMAND_ENCHANTMENT_MAX_LEVEL = new CommandEnchantmentMaxLevel());
  }
  
  public static final CommandOreDict COMMAND_ORE_DICT;
  public static final CommandShowHand COMMAND_SHOW_HAND;
  public static final CommandEnchantmentMaxLevel COMMAND_ENCHANTMENT_MAX_LEVEL;
}
