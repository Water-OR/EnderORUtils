package io.github.enderor.items;

import io.github.enderor.EnderORUtils;
import io.github.enderor.items.baubles.ring.ItemMagneticRing;
import io.github.enderor.items.baubles.ring.ItemPotionRing;
import io.github.enderor.items.baubles.trinket.ItemSlimeLink;
import io.github.enderor.recipes.EnderORRecipesHandler;
import io.github.enderor.recipes.IHasRecipe;
import io.github.enderor.utils.NullHelper;
import io.github.enderor.utils.Pair;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnderORItemHandler {
  private static final List<Item> itemList = new ArrayList<>();
  
  private static final HashMap<Pair<Item, Integer>, String> MODEL_MAP = new HashMap<>();
  
  public static void addItem(@NotNull Item item, String registerName) {
    itemList.add(item.setRegistryName(new ResourceLocation(EnderORUtils.MOD_ID, registerName)).setTranslationKey(registerName).setCreativeTab(EnderORUtils.MOD_TAB));
    
    if (item instanceof IHasRecipe) {
      ((IHasRecipe) item).makeRecipe(EnderORRecipesHandler.INSTANCE);
    }
  }
  
  public static void addModel(Item item, int meta, String itemIn) {
    MODEL_MAP.put(new Pair<>(item, meta), itemIn);
  }
  
  public static void registerModel() {
    MODEL_MAP.forEach((item, itemIn) -> {
      ModelLoader.setCustomModelResourceLocation(item.getKey(), item.getValue(), new ModelResourceLocation(NullHelper.getRegistryName(item.getKey()), itemIn));
      EnderORUtils.log(Level.INFO, "Register model for item %s:%d", NullHelper.getRegistryNameString(item.getKey()), item.getValue());
    });
  }
  
  static {
    addItem(ITEM_MOD = new ItemMod(), "item_mod");
    addItem(ITEM_SLIME_LINK = new ItemSlimeLink(), "slime_link");
    addItem(ITEM_POTION_RING = new ItemPotionRing(), "potion_ring");
    addItem(ITEM_MAGNETIC_RING = new ItemMagneticRing(), "magnetic_ring");
    addItem(ITEM_ENCHANTED_PAPER = new ItemEnchantedPaper(), "enchanted_paper");
  }
  
  public static final ItemMod            ITEM_MOD;
  public static final ItemSlimeLink      ITEM_SLIME_LINK;
  public static final ItemPotionRing     ITEM_POTION_RING;
  public static final ItemMagneticRing   ITEM_MAGNETIC_RING;
  public static final ItemEnchantedPaper ITEM_ENCHANTED_PAPER;
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(RegistryEvent.@NotNull Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();
      itemList.forEach(item -> {
        EnderORUtils.log(Level.INFO, String.format("Register %s item", NullHelper.getRegistryName(item)));
        registry.register(item);
      });
    }
  }
}
