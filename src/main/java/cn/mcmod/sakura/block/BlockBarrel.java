package cn.mcmod.sakura.block;

import cn.mcmod.sakura.CommonProxy;
import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.gui.SakuraGuiHandler;
import cn.mcmod.sakura.tileentity.TileEntityBarrel;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class BlockBarrel extends BlockContainer implements ITileEntityProvider {
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);


    protected BlockBarrel() {
        super(Material.WOOD);
        this.setHardness(2.5F);
        this.setResistance(8.0F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(CommonProxy.tab);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            ItemStack stack = player.getHeldItem(hand);
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityBarrel) {
                IFluidHandlerItem handler = FluidUtil.getFluidHandler(ItemHandlerHelper.copyStackWithSize(stack, 1));

                if (handler != null) {

                    FluidUtil.interactWithFluidHandler(player, hand, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side));

                    return true;
                }

                player.openGui(SakuraMain.instance, SakuraGuiHandler.ID_BARREL, world, pos.getX(), pos.getY(), pos.getZ());

                return true;
            }
            return true;
        }
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityBarrel();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return AABB;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canPlaceFullBlock(worldIn, pos);
    }

    //Only on top of FullBlock can place
    private boolean canPlaceFullBlock(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.getBlockState(pos.down());

        return downState.isTopSolid() && downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
    }

}
