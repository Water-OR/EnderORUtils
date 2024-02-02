package io.github.enderor.config;

import io.github.enderor.EnderORUtils;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config (modid = EnderORUtils.MOD_ID, name = EnderORUtils.MOD_NAME, category = "client")
@Config.LangKey ("config." + EnderORUtils.MOD_ID + ".client")
@SideOnly(Side.CLIENT)
public class EnderORClientConfigs {
  private static final String PREFIX = "config." + EnderORUtils.MOD_ID + ".client.";
  
  @Config.Comment ("Enable hit box render if need")
  @Config.Name ("Enable hit box")
  @Config.LangKey (PREFIX + "enable_hit_box")
  public static boolean ENABLE_HIT_BOX_DISPLAY = false;
  
  @Config.Comment ("The color of hit box")
  @Config.Name ("Hit box color")
  @Config.LangKey (PREFIX + "hit_box_color")
  public static int HIT_BOX_COLOR = -1;
  
  @Config.Comment ("The width of hit box")
  @Config.Name ("Hit box width")
  @Config.LangKey (PREFIX + "hit_box_width")
  @Config.RangeDouble (min = Double.MIN_NORMAL)
  public static double HIT_BOX_WIDTH = 2.0;
  
  @Config.Comment ("Enable playing sound when arrow hurt entity")
  @Config.Name ("Enable Arrow Hurt Entity Sound")
  @Config.LangKey (PREFIX + "enable_arrow_hurt_entity_sound")
  public static boolean ENABLE_ARROW_HURT_ENTITY_SOUND = false;
  
  @Config.Comment ("Enable swing range render if need")
  @Config.Name ("Enable swing range")
  @Config.LangKey (PREFIX + "enable_swing_range")
  public static boolean ENABLE_SWING_RANGE_DISPLAY = false;
  
  @Config.Comment ("The border color of swing range")
  @Config.Name ("Swing range border color")
  @Config.LangKey (PREFIX + "swing_range_border_color")
  public static int SWING_RANGE_BORDER_COLOR = -1;
  
  @Config.Comment ("The border width swing range")
  @Config.Name ("Swing range border width")
  @Config.LangKey (PREFIX + "swing_range_border_width")
  @Config.RangeDouble (min = Double.MIN_NORMAL)
  public static double SWING_RANGE_BORDER_WIDTH = 2.0;
  
  @Config.Comment ("The face color of swing range")
  @Config.Name ("Swing range face color")
  @Config.LangKey (PREFIX + "swing_range_face_color")
  public static int SWING_RANGE_FACE_COLOR = -1;
}
