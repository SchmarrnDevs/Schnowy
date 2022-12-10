package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.Schnowy;
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
		if (world instanceof ServerLevel level && !state.canSurvive(world, pos)) {
			int layers = state.getValue(LAYERS);
			if (neighborState.hasProperty(LAYERS)) {
				int layersToRemove = 8 - neighborState.getValue(LAYERS);
				if (neighborState.is(Blocks.SNOW)) {
					level.setBlockAndUpdate(neighborPos, Blocks.SNOW_BLOCK.defaultBlockState());
				} else {
					level.setBlockAndUpdate(neighborPos, neighborState.setValue(LAYERS, 8));
				}
				if (layers > layersToRemove) {
					cir.setReturnValue(state.setValue(LAYERS, layers - layersToRemove));
				} else {
					cir.setReturnValue(Blocks.AIR.defaultBlockState());
				}
			} else if (neighborState.hasProperty(SchnowyProperties.HALF_LAYERS)) {
				int layersToRemove = 4 - neighborState.getValue(SchnowyProperties.HALF_LAYERS);
				level.setBlockAndUpdate(neighborPos, neighborState.setValue(SchnowyProperties.HALF_LAYERS, 4));
				if (layers > layersToRemove) {
					cir.setReturnValue(state.setValue(LAYERS, layers - layersToRemove));
				} else {
					cir.setReturnValue(Blocks.AIR.defaultBlockState());
				}
			} else {
				level.setBlockAndUpdate(neighborPos, state);
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
