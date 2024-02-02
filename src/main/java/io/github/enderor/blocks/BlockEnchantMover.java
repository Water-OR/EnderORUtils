package io.github.enderor.blocks;

import io.github.enderor.EnderORUtils;
import io.github.enderor.blocks.tileEntities.TileEntityEnchantMover;
import io.github.enderor.blocks.tileEntities.renderer.IHasSpecialRenderer;
import io.github.enderor.blocks.tileEntities.renderer.TileEntityEnchantMoverRender;
import io.github.enderor.recipes.CompactIngredient;
import io.github.enderor.recipes.EnderORRecipesHandler;
import io.github.enderor.recipes.IHasRecipe;
import io.github.enderor.recipes.ShapedRecipe;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockEnchantMover extends BlockContainer implements IHasSpecialRenderer, IHasRecipe {
  public BlockEnchantMover() {
    super(Material.ROCK, MapColor.GRAY);
    setLightOpacity(0);
    setHardness(5F);
    setResistance(2000F);
    EnderORBlockHandler.addModel(this, 0, "inventory");
  }
  
  public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
  
  @Override
  public @NotNull AxisAlignedBB getBoundingBox(@NotNull IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos) { return BOUNDING_BOX; }
  
  @Override
  public boolean isFullCube(@NotNull IBlockState state) { return false; }
  
  @Override
  public boolean isOpaqueCube(@NotNull IBlockState state) { return false; }
  
  @Override
  public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) { return EnumBlockRenderType.MODEL; }
  
  @Override
  public boolean hasTileEntity(@NotNull IBlockState state) { return true; }
  
  @Override
  public boolean onBlockActivated(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer playerIn, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (worldIn.isRemote) { return true; }
    TileEntity tile0 = worldIn.getTileEntity(pos);
    
    if (!(tile0 instanceof TileEntityEnchantMover)) {
      return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
    
    playerIn.openGui(EnderORUtils.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
    
    return true;
  }
  
  @Override
  public void onBlockPlacedBy(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityLivingBase placer, @NotNull ItemStack stack) {
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    
    if (stack.hasDisplayName()) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      
      if (tileEntity instanceof TileEntityEnchantMover) {
        ((TileEntityEnchantMover) tileEntity).setCustomName(stack.getDisplayName());
      }
    }
  }
  
  @Nullable
  @Override
  public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) { return new TileEntityEnchantMover(); }
  
  @Override
  public void breakBlock(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
    TileEntity tileEntity = worldIn.getTileEntity(pos);
    if (tileEntity instanceof TileEntityEnchantMover) {
      InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileEntity);
      worldIn.updateComparatorOutputLevel(pos, this);
    }
    super.breakBlock(worldIn, pos, state);
  }
  
  @Override
  public Class<TileEntityEnchantMover> getTileClass() { return TileEntityEnchantMover.class; }
  
  @Override
  public void register() {
    register_(getTileClass(), new TileEntityEnchantMoverRender());
  }
  
  @Override
  public void makeRecipe(@NotNull EnderORRecipesHandler handler) {
    final Ingredient ingotIron = new OreIngredient("ingotIron");
    final Ingredient stone     = new CompactIngredient(new OreIngredient("stone"), new OreIngredient("cobblestone"));
    handler.addRecipe(new ShapedRecipe(
      "enchant_mover_add", 3, 3, new ItemStack(EnderORBlockHandler.getBlockItem(this)),
      ingotIron, Ingredient.fromItem(Items.BOOK), ingotIron,
      stone, Ingredient.fromStacks(new ItemStack(Blocks.ENCHANTING_TABLE)), stone,
      ingotIron, new OreIngredient("chest"), ingotIron
    ));
  }
}
