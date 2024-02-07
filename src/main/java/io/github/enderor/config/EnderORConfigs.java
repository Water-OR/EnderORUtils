package io.github.enderor.config;

import io.github.enderor.EnderORUtils;
import net.minecraftforge.common.config.Config;

@Config (modid = EnderORUtils.MOD_ID, name = EnderORUtils.MOD_NAME, category = "default")
@Config.LangKey ("config." + EnderORUtils.MOD_ID + ".default")
public class EnderORConfigs {
  private static final String PREFIX = "config." + EnderORUtils.MOD_ID + ".default.";
  
  @Config.Comment ("Effect Particles while wearing effect ring.")
  @Config.Name ("Effect Ring effect show particles")
  @Config.LangKey (PREFIX + ".effect_show_particles")
  public static boolean EFFECT_SHOW_PARTICLES = false;
}
