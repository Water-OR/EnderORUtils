package io.github.enderor.mixins.minecraft;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat extends Gui {
  @ModifyConstant(method = "setChatLine", constant = @Constant(intValue = 100))
  public int newValue(int value) {
    return Integer.MAX_VALUE;
  }
}
