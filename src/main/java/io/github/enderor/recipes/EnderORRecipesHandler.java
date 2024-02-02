package io.github.enderor.recipes;

import io.github.enderor.EnderORUtils;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnderORRecipesHandler {
  public static final EnderORRecipesHandler INSTANCE = new EnderORRecipesHandler();
  public static final List<EnderORRecipe>   RECIPES  = new ArrayList<>();
  
  public void addRecipe(EnderORRecipe... recipes)          { EnderORRecipesHandler.addRecipe0(recipes); }
  
  private static void addRecipe0(EnderORRecipe... recipes) { RECIPES.addAll(Arrays.asList(recipes)); }
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(RegistryEvent.@NotNull Register<IRecipe> event) {
      IForgeRegistry<IRecipe> registry = event.getRegistry();
      RECIPES.forEach(recipe -> {
        EnderORUtils.log(Level.INFO, String.format("Register %s recipe", recipe.getRegistryName()));
        registry.register(recipe);
      });
    }
  }
}
