package cn.mcmod.sakura.block.slab;

import java.util.Random;

import cn.mcmod.sakura.block.BlockLoader;
import cn.mcmod.sakura.block.slab.BlockSlabBase.EnumBlockFrostHalf;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHalfTatami extends BlockHalfFacing{
	public final boolean isNS;
	public BlockHalfTatami(boolean ns) {
		super(Material.GRASS);
		this.setTickRandomly(true);
		this.setSoundType(SoundType.PLANT);
		setHardness(0.25F).setResistance(0.5F);
		isNS = ns;
	}
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(worldIn.canBlockSeeSky(pos)&&worldIn.isDaytime()){
				worldIn.setBlockState(pos, (isNS?BlockLoader.TATAMI_TAN_NS_HALF:BlockLoader.TATAMI_TAN_HALF).getDefaultState().withProperty(FACING, state.getValue(FACING)));
		}
		super.updateTick(worldIn, pos, state, rand);
	}
}
