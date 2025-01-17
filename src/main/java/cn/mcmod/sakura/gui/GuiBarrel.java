package cn.mcmod.sakura.gui;

import cn.mcmod.sakura.inventory.ContainerBarrel;
import cn.mcmod.sakura.tileentity.TileEntityBarrel;
import cn.mcmod.sakura.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiBarrel extends GuiContainer {

    private static final ResourceLocation mortarGuiTextures = new ResourceLocation("sakura:textures/gui/barrel.png");

    private TileEntityBarrel tilePot;
    private final IInventory playerInventory;

    public GuiBarrel(InventoryPlayer inventory, TileEntityBarrel tile) {

        super(new ContainerBarrel(inventory, tile));

        this.tilePot = tile;
        this.playerInventory = inventory;

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(mortarGuiTextures);

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        int var7;


        int l2 = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(k + 93, l + 45, 176, 14, l2 + 1, 16);

        if (this.tilePot.getTank().getFluid() != null) {
            FluidTank fluidTank = this.tilePot.getTank();
            int heightInd = (int) (68 * ((float) fluidTank.getFluidAmount() / (float) fluidTank.getCapacity()));
           /* if (heightInd > 0) {
                ClientUtils.drawRepeatedFluidSprite(fluidTank.getFluid(), k + 167 - heightInd, l + 11, heightInd, 16f);
            }*/

            if (heightInd > 0) {
                ClientUtils.drawRepeatedFluidSprite(fluidTank.getFluid(), k + 18, l + 78 - heightInd, 16f, heightInd);
            }

        }

        if (this.tilePot.getResultTank().getFluid() != null) {
            FluidTank fluidTank = this.tilePot.getResultTank();
            int heightInd = (int) (68 * ((float) fluidTank.getFluidAmount() / (float) fluidTank.getCapacity()));
           /* if (heightInd > 0) {
                ClientUtils.drawRepeatedFluidSprite(fluidTank.getFluid(), k + 167 - heightInd, l + 11, heightInd, 16f);
            }*/

            if (heightInd > 0) {
                ClientUtils.drawRepeatedFluidSprite(fluidTank.getFluid(), k + 62, l + 78 - heightInd, 16f, heightInd);
            }

        }
    }

    private int getCookProgressScaled(int pixels) {
        int i = this.tilePot.getField(0);
        int j = this.tilePot.getField(1);
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

    }

}