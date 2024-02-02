package io.github.enderor.blocks.tileEntities.renderer;

import io.github.enderor.blocks.tileEntities.TileEntityEnchantMover;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

//@SideOnly(Side.CLIENT)
public class TileEntityEnchantMoverRender extends TileEntitySpecialRenderer<TileEntityEnchantMover> {
  protected static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
  protected final        ModelBook        modelBook    = new ModelBook();
  
  @Override
  public void render(@NotNull TileEntityEnchantMover tileEnchantMover, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    GL11.glPushMatrix();
    GL11.glTranslated(x + .5, y + .75, z + .5);
    double f = (float) tileEnchantMover.tickCount + partialTicks;
    GL11.glTranslated(0, .1 + MathHelper.sin((float) (f * .1)) * .01, 0);
    double f1 = tileEnchantMover.bookRotation - tileEnchantMover.bookRotationPrev;
    
    while (f1 >= (float) Math.PI) { f1 -= Math.PI * 2; }
    while (f1 < -(float) Math.PI) { f1 += Math.PI * 2; }
    
    double f2 = tileEnchantMover.bookRotationPrev + f1 * partialTicks;
    GL11.glRotated(-f2 * (180D / Math.PI), 0, 1, 0);
    GL11.glRotated(80, 0, 0, 1);
    bindTexture(TEXTURE_BOOK);
    double f3 = tileEnchantMover.pageFlipPrev + (tileEnchantMover.pageFlip - tileEnchantMover.pageFlipPrev) * partialTicks + .25;
    double f4 = tileEnchantMover.pageFlipPrev + (tileEnchantMover.pageFlip - tileEnchantMover.pageFlipPrev) * partialTicks + .75;
    f3 = (f3 - MathHelper.fastFloor(f3)) * 1.6 - .3;
    f4 = (f4 - MathHelper.fastFloor(f4)) * 1.6 - .3;
    
    if (f3 < 0) { f3 = 0; }
    if (f4 < 0) { f4 = 0; }
    if (f3 > 1) { f3 = 1; }
    if (f4 > 1) { f4 = 1; }
    
    double f5 = tileEnchantMover.bookSpreadPrev + (tileEnchantMover.bookSpread - tileEnchantMover.bookSpreadPrev) * partialTicks;
    GL11.glEnable(GL11.GL_CULL_FACE);
    modelBook.render(null, (float) f, (float) f3, (float) f4, (float) f5, .0F, .0625F);
    // I don't think it could be null here, but minecraft wrote "null", I have no choice, so I wrote "null" either
    GL11.glPopMatrix();
  }
}
