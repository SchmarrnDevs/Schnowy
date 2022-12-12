package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.Schnowy;
import dev.schmarrn.schnowy.common.ReplaceableBlocks;
import dev.schmarrn.schnowy.common.blocks.SchnowyProperties;
import dev.schmarrn.schnowy.common.blocks.SnowedSlab;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import dev.schmarrn.schnowy.common.SchnowyEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowLayerBlock.class)
public class SnowLayerBlockMixin {
	@Shadow
	@Final
	public static IntegerProperty LAYERS;

	@Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
	public void schnowy$updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
		if (world instanceof ServerLevel level && !state.canSurvive(world, pos) && direction.equals(Direction.DOWN)) {
			// Find lowest BlockState that isn't air anymore
			BlockPos belowPos = neighborPos;
			while (level.getBlockState(belowPos) == Blocks.AIR.defaultBlockState()) {
				belowPos = belowPos.below();
			}
			BlockState belowState = level.getBlockState(belowPos);

			// Get the Layers of the Block that cannot survive anymore
			int layers = state.getValue(LAYERS);

			// Block snow should fall on block corresponding with belowState
			if (belowState.hasProperty(LAYERS)) {
				int layersToRemove = 8 - belowState.getValue(LAYERS);

				// The Block cannot fit into the lower block
				if (layers > layersToRemove) {
					// Fill up the lower block
					if (belowState.is(Blocks.SNOW)) {
						level.setBlockAndUpdate(belowPos, Blocks.SNOW_BLOCK.defaultBlockState());
					} else {
						level.setBlockAndUpdate(belowPos, belowState.setValue(LAYERS, 8));
					}
					// If there was no air between the blocks
					if (belowState == neighborState) {
						// Just Remove the required layers
						cir.setReturnValue(state.setValue(LAYERS, layers - layersToRemove));
					} else {
						// otherwise, destroy the snow layer block, and set the destroyed block on top
						// of the lower block
						level.setBlockAndUpdate(belowPos.above(), state.setValue(LAYERS, layers - layersToRemove));
						cir.setReturnValue(Blocks.AIR.defaultBlockState());
					}
				} else {
					// If the block can fit into the lower block
					level.setBlockAndUpdate(belowPos, belowState.setValue(LAYERS, belowState.getValue(LAYERS) + layers));
					cir.setReturnValue(Blocks.AIR.defaultBlockState());
				}
			} else if (belowState.hasProperty(SchnowyProperties.HALF_LAYERS)) {
				int layersToRemove = 4 - belowState.getValue(SchnowyProperties.HALF_LAYERS);

				// The Block cannot fit into the lower block
				if (layers > layersToRemove) {
					// Fill up the lower block
					level.setBlockAndUpdate(belowPos, belowState.setValue(SchnowyProperties.HALF_LAYERS, 4));
					// If there was no air between the blocks
					if (belowState == neighborState) {
						// Just Remove the required layers
						cir.setReturnValue(state.setValue(LAYERS, layers - layersToRemove));
					} else {
						// otherwise, destroy the snow layer block, and set the destroyed block on top
						// of the lower block
						level.setBlockAndUpdate(belowPos.above(), state.setValue(LAYERS, layers - layersToRemove));
						cir.setReturnValue(Blocks.AIR.defaultBlockState());
					}
				} else {
					// If the block can fit into the lower block
					level.setBlockAndUpdate(belowPos, belowState.setValue(SchnowyProperties.HALF_LAYERS, belowState.getValue(SchnowyProperties.HALF_LAYERS) + layers));
					cir.setReturnValue(Blocks.AIR.defaultBlockState());
				}
			} else if (ReplaceableBlocks.withSnow(belowState) != null) {
				// If we have a snowloggable block
				BlockState snowlogged = ReplaceableBlocks.withSnow(belowState);

				if (snowlogged.hasProperty(LAYERS)) {
					// We can fit 8 layers into it - the most that can be dropped are technically seven layers, so it can
					// fit into this block no problem
					level.setBlockAndUpdate(belowPos, belowState.setValue(LAYERS, layers));
				} else if (snowlogged.hasProperty(SchnowyProperties.HALF_LAYERS)) {
					// now we could run into problems
					if (layers <= 4) {
						level.setBlockAndUpdate(belowPos, snowlogged.setValue(SchnowyProperties.HALF_LAYERS, layers));
					} else {
						// Here is the problem: we have more layers than we can fit into the block, so we need to
						// put the remaining layers above
						level.setBlockAndUpdate(belowPos, snowlogged.setValue(SchnowyProperties.HALF_LAYERS, 4));

						// If there was no air between the blocks
						if (belowState == neighborState) {
							// Just Remove the required layers
							cir.setReturnValue(state.setValue(LAYERS, layers - 4));
						} else {
							// otherwise, destroy the snow layer block, and set the destroyed block on top
							// of the lower block
							level.setBlockAndUpdate(belowPos.above(), state.setValue(LAYERS, layers - 4));
							cir.setReturnValue(Blocks.AIR.defaultBlockState());
						}
					}
				}

			} else {
				// If we do not have a snowloggable block, place the snow above the lower block
				level.setBlockAndUpdate(belowPos.above(), state);
				cir.setReturnValue(Blocks.AIR.defaultBlockState());
			}
		}
	}

	@Inject(method = "canSurvive", at = @At("TAIL"), cancellable = true)
	public void schnowy$canSurviveOnSnowedBlocks(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		BlockState blockState = world.getBlockState(pos.below());
		if (SchnowyEngine.isFullSnowLogged(blockState))
			cir.setReturnValue(true);
	}
}
