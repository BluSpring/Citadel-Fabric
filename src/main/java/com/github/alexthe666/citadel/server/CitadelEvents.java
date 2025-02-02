package com.github.alexthe666.citadel.server;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CitadelEvents {

    private int updateTimer;

    public CitadelEvents() {
        LivingEntityEvents.TICK.register(this::onEntityUpdateDebug);
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> {
            onRightClickBlock(player.level, player, hand, player.getItemInHand(hand), pos, face);
            return EventResult.pass();
        });
        PlayerEvent.PLAYER_CLONE.register((oldPlayer, newPlayer, wonGame) -> {
            onPlayerClone(oldPlayer, newPlayer);
        });
    }

    public void onEntityUpdateDebug(LivingEntity entity) {
        if (CitadelConstants.DEBUG) {
            if ((entity instanceof Player)) {
                CompoundTag tag = CitadelEntityData.getCitadelTag(entity);
                tag.putInt("CitadelInt", tag.getInt("CitadelInt") + 1);
                Citadel.LOGGER.debug("Citadel Data Tag tracker example: " + tag.getInt("CitadelInt"));
            }
        }
    }

    public void onRightClickBlock(Level level, Player player, InteractionHand hand, ItemStack itemStack, BlockPos pos, Direction face) {
        if(level.getBlockState(pos).is(Blocks.LECTERN) && LecternBooks.isLecternBook(itemStack)){
            player.getCooldowns().addCooldown(itemStack.getItem(), 1);
            BlockState oldLectern = level.getBlockState(pos);
            if(level.getBlockEntity(pos) instanceof LecternBlockEntity oldBe && !oldBe.hasBook()){
                BlockState newLectern = Citadel.LECTERN.get().defaultBlockState().setValue(CitadelLecternBlock.FACING, oldLectern.getValue(LecternBlock.FACING)).setValue(CitadelLecternBlock.POWERED, oldLectern.getValue(LecternBlock.POWERED)).setValue(CitadelLecternBlock.HAS_BOOK, true);
                level.setBlockAndUpdate(pos, newLectern);
                CitadelLecternBlockEntity newBe = new CitadelLecternBlockEntity(pos, newLectern);
                ItemStack bookCopy = itemStack.copy();
                bookCopy.setCount(1);
                newBe.setBook(bookCopy);
                if(!player.isCreative()){
                    itemStack.shrink(1);
                }
                level.setBlockEntity(newBe);
                player.swing(hand, true);
                level.playSound(null, pos, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public void onPlayerClone(Player original, Player current) {
        if (original != null && CitadelEntityData.getCitadelTag(original) != null) {
            CitadelEntityData.setCitadelTag(current, CitadelEntityData.getCitadelTag(original));
        }
    }
}
