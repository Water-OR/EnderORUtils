package io.github.enderor;

import io.github.enderor.capabilities.ArrowCapability;
import io.github.enderor.capabilities.IArrowCapability;
import io.github.enderor.command.EnderORCommandHandler;
import io.github.enderor.items.EnderORCreativeTab;
import io.github.enderor.network.EnderORGuiHandler;
import io.github.enderor.proxy.CommonProxy;
import io.github.enderor.utils.EffectHelper;
import io.github.enderor.utils.EnchantsHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod (
  modid = EnderORUtils.MOD_ID,
  name = EnderORUtils.MOD_NAME,
  version = EnderORUtils.MOD_VERSION,
  dependencies = EnderORUtils.MOD_DEPENDENCIES,
  acceptedMinecraftVersions = EnderORUtils.MOD_ACCEPT_VERSION
)
public class EnderORUtils {
  public final static String       MOD_ID             = "enderor";
  public final static String       MOD_NAME           = "Ender OR Utils";
  public final static String       MOD_VERSION        = "1.2";
  public final static String       MOD_DEPENDENCIES   = "required-after:forge@[14.21.1.2395,);required-after:baubles;required-after:jei;required-after:enchdesc;";
  public final static String       MOD_ACCEPT_VERSION = "[1.12.2]";
  public static final CreativeTabs MOD_TAB            = new EnderORCreativeTab();
  
  @SidedProxy (serverSide = "io.github.enderor.proxy.CommonProxy", clientSide = "io.github.enderor.proxy.ClientProxy")
  public static CommonProxy proxy;
  
  @Mod.Instance (EnderORUtils.MOD_ID)
  public static EnderORUtils instance;
  
  public EnderORUtils() { }
  
  private static Logger logger;
  
  @Mod.EventHandler
  public void onPreInit(@NotNull FMLPreInitializationEvent event) {
    logger = event.getModLog();
    log(Level.WARN, "=========Welcome to EnderOR=========");
    log(Level.WARN, "|                                  |");
    log(Level.WARN, "|  [][][][]     ()()     {}{}{}    |");
    log(Level.WARN, "|  []         ()    ()   {}    {}  |");
    log(Level.WARN, "|  [][][][]   ()    ()   {}{}{}    |");
    log(Level.WARN, "|  []         ()    ()   {}    {}  |");
    log(Level.WARN, "|  [][][][]     ()()     {}    {}  |");
    log(Level.WARN, "|                                  |");
    log(Level.WARN, "====================================");
    NetworkRegistry.INSTANCE.registerGuiHandler(this, EnderORGuiHandler.INSTANCE);
    CapabilityManager.INSTANCE.register(IArrowCapability.class, new ArrowCapability.Storage(), ArrowCapability::new);
  }
  
  @Mod.EventHandler
  public void onInit(FMLInitializationEvent event) {
    EffectHelper.initAppearedEffects();
    EnchantsHelper.initAppearedEnchantments();
  }
  
  @Mod.EventHandler
  public void onPostInit(FMLPostInitializationEvent event) { }
  
  @Mod.EventHandler
  public void onServerStarting(FMLServerStartingEvent event) {
    EnderORCommandHandler.INSTANCE.register(event);
  }
  
  public static void log(Level level, Object message)                { logger.log(level, message); }
  
  public static void log(Level level, String format, Object... args) { logger.log(level, String.format(format, args)); }
}
