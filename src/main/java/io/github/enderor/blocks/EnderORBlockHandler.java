package io.github.enderor.blocks;

import io.github.enderor.EnderORUtils;
import io.github.enderor.blocks.tileEntities.IHasTileEntity;
import io.github.enderor.blocks.tileEntities.renderer.IHasSpecialRenderer;
import io.github.enderor.items.EnderORItemHandler;
import io.github.enderor.recipes.EnderORRecipesHandler;
import io.github.enderor.recipes.IHasRecipe;
import io.github.enderor.utils.NullHelper;
import io.github.enderor.utils.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EnderORBlockHandler {
  private static final List<Block>          BLOCKS         = new ArrayList<>();
  private static final HashMap<Block, Item> BLOCK_ITEM_MAP = new HashMap<>();
  private static final HashMap<Pair<Block, Integer>, String> MODEL_MAP = new HashMap<>();
  
  public static void addBlock(@NotNull Block block, String registerName) {
    BLOCKS.add(block.setRegistryName(new ResourceLocation(EnderORUtils.MOD_ID, registerName)).setTranslationKey(registerName).setCreativeTab(EnderORUtils.MOD_TAB));
    Item blockItem = block instanceof IHasItemBlock ? ((IHasItemBlock) block).getItemBlock() : new ItemBlock(block);
    BLOCK_ITEM_MAP.put(block, blockItem);
    EnderORItemHandler.addItem(blockItem, registerName);
    
    if (block instanceof IHasRecipe) { ((IHasRecipe) block).makeRecipe(EnderORRecipesHandler.INSTANCE); }
  }
  
  public static void addModel(Block block, int meta, String blockIn) {
    MODEL_MAP.put(new Pair<>(block, meta), blockIn);
  }
  
  public static Item getBlockItem(Block block) { return BLOCK_ITEM_MAP.get(block); }
  
  public static void registerModel() {
    MODEL_MAP.forEach((block, blockIn) -> {
      ModelLoader.setCustomModelResourceLocation(getBlockItem(block.getKey()), block.getValue(), new ModelResourceLocation(NullHelper.getRegistryName(block.getKey()), blockIn));
      EnderORUtils.log(Level.INFO, "Register model for block %s:%d", NullHelper.getRegistryNameString(block.getKey()), block.getValue());
    });
  }
  
  static {
    addBlock(BLOCK_ENCHANT_MOVER = new BlockEnchantMover(), "enchant_mover");
  }
  
  public static final BlockEnchantMover BLOCK_ENCHANT_MOVER;
  
  @Mod.EventBusSubscriber
  public static class EventHandler {
    @SubscribeEvent
    public static void onEvent(RegistryEvent.@NotNull Register<Block> event) {
      IForgeRegistry<Block> registry = event.getRegistry();
      for (Block block : BLOCKS) {
        EnderORUtils.log(Level.INFO, String.format("Register %s block", NullHelper.getRegistryName(block)));
        registry.register(block);
        
        if (!(block instanceof IHasTileEntity)) { continue; }
        EnderORUtils.log(Level.INFO, String.format("+ Register tile entity of %s block", NullHelper.getRegistryName(block)));
        GameRegistry.registerTileEntity(((IHasTileEntity) block).getTileClass(), NullHelper.getRegistryName(block));
        
        if (!(block instanceof IHasSpecialRenderer)) { continue; }
        EnderORUtils.log(Level.INFO, String.format("+ Register tile entity special renderer of %s block", NullHelper.getRegistryName(block)));
        ((IHasSpecialRenderer) block).register();
      }
    }
  }
}
