package com.github.alexthe666.citadel.fabric;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomLadderBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.SupportsClimbableOpenTrapdoorBlock;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

public class CitadelFabricHooks {
    public static PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return state.getBlock() == Blocks.LAVA ? PathType.LAVA : isBurning(state, level, pos) ? PathType.DAMAGE_FIRE : null;
    }

    public static boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() == Blocks.FIRE || state.getBlock() == Blocks.LAVA;
    }

    public static boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        if (state.getBlock() instanceof CustomLadderBlock ladderBlock) {
            return ladderBlock.isLadder(state, level, pos, entity);
        } else if (state.getBlock() instanceof TrapDoorBlock) {
            if (state.getValue(TrapDoorBlock.OPEN)) {
                BlockPos downPos = pos.below();
                BlockState down = level.getBlockState(downPos);

                if (down.getBlock() instanceof SupportsClimbableOpenTrapdoorBlock climbableOpenTrapdoorBlock) {
                    return climbableOpenTrapdoorBlock.makesOpenTrapdoorAboveClimbable(down, level, downPos, state);
                } else {
                    return down.getBlock() instanceof LadderBlock && down.getValue(LadderBlock.FACING) == state.getValue(TrapDoorBlock.FACING);
                }
            }

            return false;
        }

        return state.is(BlockTags.CLIMBABLE);
    }
}
