package io.github.enderor.mixins.others;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class MixinOthersLoader implements ILateMixinLoader {
  @Override
  public List<String> getMixinConfigs() {
    return Collections.singletonList("mixins.enderor.others.json");
  }
}
