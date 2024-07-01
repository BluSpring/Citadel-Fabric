package com.github.alexthe666.citadel.server.entity.collision;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class CustomCollisionsNodeProcessor extends WalkNodeEvaluator {

    public CustomCollisionsNodeProcessor() {
    }

    public static PathType getPathTypetatic(BlockGetter p_237231_0_, BlockPos.MutableBlockPos p_237231_1_) {
        int i = p_237231_1_.getX();
        int j = p_237231_1_.getY();
        int k = p_237231_1_.getZ();
        PathType pathnodetype = getNodes(p_237231_0_, p_237231_1_);
        if (pathnodetype == PathType.OPEN && j >= 1) {
            PathType pathnodetype1 = getNodes(p_237231_0_, p_237231_1_.set(i, j - 1, k));
            pathnodetype = pathnodetype1 != PathType.WALKABLE && pathnodetype1 != PathType.OPEN && pathnodetype1 != PathType.WATER && pathnodetype1 != PathType.LAVA ? PathType.WALKABLE : PathType.OPEN;
            if (pathnodetype1 == PathType.DAMAGE_FIRE) {
                pathnodetype = PathType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathType.DAMAGE_OTHER) {
                pathnodetype = PathType.DAMAGE_OTHER;
            }

            if (pathnodetype1 == PathType.STICKY_HONEY) {
                pathnodetype = PathType.STICKY_HONEY;
            }
        }

        /*if (pathnodetype == PathType.WALKABLE) {
            pathnodetype = checkNeighbourBlocks(new PathfindingContext((CollisionGetter) p_237231_0_, null), i, j, k, pathnodetype);
        }*/

        return pathnodetype;
    }


    protected static PathType getNodes(BlockGetter p_237238_0_, BlockPos p_237238_1_) {
        BlockState blockstate = p_237238_0_.getBlockState(p_237238_1_);

        if (blockstate.isAir()) {
            return PathType.OPEN;
        } else if (blockstate.getBlock() == Blocks.BAMBOO) {
            return PathType.OPEN;
        } else {
            return getPathTypeFromState(p_237238_0_, p_237238_1_);
        }
    }

    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        return getPathTypetatic(context.level(), new BlockPos.MutableBlockPos(x, y, z));
    }

    /*@Override
    protected PathType evaluateBlockPathType(BlockGetter world, BlockPos pos, PathType nodeType) {
        BlockState state = world.getBlockState(pos);
        return ((ICustomCollisions) this.mob).canPassThrough(pos, state, state.getBlockSupportShape(world, pos)) ? PathType.OPEN : super.evaluateBlockPathType(world, pos, nodeType);
    }*/
}