package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.FallingSnowUtil;
import dev.schmarrn.schnowy.common.SchnowyEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class SnowBlockMixin {
	@Inject(
			method = "canSurvive",
			at = @At(
					value = "HEAD"
			),
			cancellable = true
	)
	public void schnowy$canSurvive(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		// Why? Because falling snow works when canSurvive and updateShape are changed.
		// Normal Snow Blocks don't have their own class, so I think I have to mixin into
		// BlockBehaviour. I must agree, not the best solution, but the best I can do right now.

		if (state.getBlock() == Blocks.SNOW_BLOCK) {
			BlockState belowState = world.getBlockState(pos.below());

			// If the block below is a snow block, we can survive
			if (belowState.getBlock() == Blocks.SNOW_BLOCK) {
				return;
			}

			// If the block below is snow, and it isn't fully logged, we cannot survive
			// the state.getValue is needed because SchnowyEngine.isFullSnowLogged doesn't check normal snowlayers
			if (SchnowyEngine.isSnow(belowState) && !(SchnowyEngine.isFullSnowLogged(belowState) || belowState.getValue(SnowLayerBlock.LAYERS) == 8)) {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
	public void schnowy$updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
		if (state.getBlock() == Blocks.SNOW_BLOCK) {
			BlockState fallen = FallingSnowUtil.updateShape(Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 8), direction, neighborState, world, pos, neighborPos, Blocks.AIR);

			if (fallen != null) {
				cir.setReturnValue(fallen);
			}
		}
	}
}
