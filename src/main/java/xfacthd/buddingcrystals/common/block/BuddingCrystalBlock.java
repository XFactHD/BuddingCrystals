package xfacthd.buddingcrystals.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import xfacthd.buddingcrystals.common.util.BudSet;

import java.util.Random;

public final class BuddingCrystalBlock extends BuddingAmethystBlock
{
    private static final Direction[] DIRECTIONS = Direction.values();

    private final BudSet budSet;
    private final int growthChance;

    public BuddingCrystalBlock(BudSet budSet, int growthChance, Properties properties)
    {
        super(properties);
        this.budSet = budSet;
        this.growthChance = growthChance;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        if (random.nextInt(growthChance) == 0)
        {
            Direction side = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos neighborPos = pos.relative(side);
            BlockState neighborState = level.getBlockState(neighborPos);

            Block block = null;
            if (canClusterGrowAtState(neighborState))
            {
                block = budSet.getSmallBud();
            }
            else if (neighborState.is(budSet.getSmallBud()) && sameFacing(neighborState, side))
            {
                block = budSet.getMediumBud();
            }
            else if (neighborState.is(budSet.getMediumBud()) && sameFacing(neighborState, side))
            {
                block = budSet.getLargeBud();
            }
            else if (neighborState.is(budSet.getLargeBud()) && sameFacing(neighborState, side))
            {
                block = budSet.getCluster();
            }

            if (block != null)
            {
                BlockState newState = block.defaultBlockState()
                        .setValue(AmethystClusterBlock.FACING, side)
                        .setValue(AmethystClusterBlock.WATERLOGGED, neighborState.getFluidState().getType() == Fluids.WATER);

                level.setBlockAndUpdate(neighborPos, newState);
            }
        }
    }

    private static boolean sameFacing(BlockState state, Direction side)
    {
        return state.getValue(AmethystClusterBlock.FACING) == side;
    }
}
