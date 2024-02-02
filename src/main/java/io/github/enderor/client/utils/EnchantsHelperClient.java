package io.github.enderor.client.utils;

import io.github.enderor.config.EnchantsMaxLevel;
import net.darkhax.enchdesc.client.TooltipHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly (Side.CLIENT)
public class EnchantsHelperClient {
  public static @NotNull String getEnchantText(@NotNull Enchantment enchant, int level) {
    String prefix = "\u00a79";
    if (enchant.isCurse()) { prefix = TextFormatting.RED.toString(); }
    else if (level >= EnchantsMaxLevel.getMaxLevel(enchant)) { prefix = "\u00a7d"; }
    else if (level < enchant.getMaxLevel()) { prefix = "\u00a77"; }
    else if (level > enchant.getMaxLevel()) { prefix = "\u00a76"; }
    return prefix.concat(I18n.format(enchant.getName()).trim()).concat(" ")
                 .concat(I18n.format("enchantment.level." + level).trim());
  }
  
  public static @NotNull String getDescription(Enchantment enchantment) {
    final String key         = TooltipHandler.getTranslationKey(enchantment);
    String       description = I18n.format(key);
    
    if (description.startsWith("enchantment.")) {
      description = I18n.format("tooltip.enchdesc.missing", TooltipHandler.getModName(enchantment), key);
    }
    return description;
  }
}
