package io.github.enderor.command;

import io.github.enderor.EnderORUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

public abstract class EnderORCommandBase extends CommandBase {
  @Override
  public @NotNull String getUsage(@NotNull ICommandSender sender) {
    String        rawResult = I18n.format(EnderORUtils.MOD_ID + ".command." + getName() + ".usage");
    return StringEscapeUtils.unescapeJava(rawResult);
  }
}
