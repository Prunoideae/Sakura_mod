package cn.mcmod.sakura.block;

import cn.mcmod.sakura.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockPlantBamboo extends Block implements IPlantable {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 5);
    protected static final AxisAlignedBB BAMBOO_AABB = new AxisAlignedBB(0.3D, 0.0D, 0.3D, 0.7D, 1.0D, 0.7D);

    public BlockPlantBamboo() {
        super(Material.WOOD, MapColor.GREEN);
        this.setTickRandomly(true);
        this.setCreativeTab(CommonProxy.tab);
        this.setHardness(1.0F);
        this.setResistance(4.0F);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isAreaLoaded(pos, 1))
            return;
        BlockPos blockpos = pos.up();

        if (worldIn.isAirBlock(blockpos)) {
            int i;

            for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {
            }

            if (i < 18) {
                int j = state.getValue(AGE).intValue();

                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockpos, state, true)) {
                    if (j == 5) {
                        worldIn.setBlockState(blockpos, this.getDefaultState());
                        IBlockState iblockstate = state.withProperty(AGE, Integer.valueOf(0));
                        worldIn.setBlockState(pos, iblockstate, 4);
                        iblockstate.neighborChanged(worldIn, blockpos, this, pos);
                    } else {
                        worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 4);
                    }
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
            }
        } else {
            int i;

            for (i = 1; worldIn.getBlockState(pos.up(i)).getBlock() == this; ++i) {
            }

            if (i >= 18) {
                int j = state.getValue(AGE).intValue();

                if (worldIn.isRaining() || worldIn.rand.nextFloat() < 0.15) {
                    BlockPos blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

                    if (worldIn.isAirBlock(blockpos1) && BlockLoader.BAMBOOSHOOT.canBlockStay(worldIn, blockpos1)) {
                        worldIn.setBlockState(blockpos1, BlockLoader.BAMBOOSHOOT.getDefaultState());
                    }
                }
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return BAMBOO_AABB;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BAMBOO_AABB;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return true;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 10;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 2;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canBlockStay(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos) {

        IBlockState state = worldIn.getBlockState(pos.down());
        return state.getBlock().canSustainPlant(state, worldIn, pos.down(), EnumFacing.UP, this) && !worldIn.getBlockState(pos.up()).getMaterial().isLiquid();
    }

    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable) {


        IBlockState plant = plantable.getPlant(world, pos.offset(direction));

        if (plant.getBlock() == BlockLoader.BAMBOO) {
            return this == BlockLoader.BAMBOO;
        } else {
            return super.canSustainPlant(state, world, pos, direction, plantable);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }


    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Plains;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return getDefaultState();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AGE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE).intValue();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

}
