package io.github.enderor.mixins.minecraft;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("Ender OR Mixins!")
public class MixinMinecraftLoader implements IEarlyMixinLoader, IFMLLoadingPlugin {
  @Override
  public List<String> getMixinConfigs() { return Collections.singletonList("mixins.enderor.json"); }
  
  @Override
  public String[] getASMTransformerClass() { return new String[0]; }
  
  @Override
  public String getModContainerClass() { return null; }
  
  @Nullable
  @Override
  public String getSetupClass() { return null; }
  
  @Override
  public void injectData(Map<String, Object> data) { }
  
  @Override
  public String getAccessTransformerClass() { return null; }
}
