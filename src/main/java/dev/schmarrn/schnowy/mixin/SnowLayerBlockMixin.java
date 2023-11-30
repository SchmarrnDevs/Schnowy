package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.FallingSnowUtil;
import dev.schmarrn.schnowy.common.ReplaceableBlocks;
import dev.schmarrn.schnowy.common.SchnowyEngine;
import dev.schmarrn.schnowy.common.blocks.SchnowyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
		BlockState fallen = FallingSnowUtil.updateShape(state, direction, neighborState, world, pos, neighborPos, Blocks.AIR);

		if (fallen != null) {
			cir.setReturnValue(fallen);
		}
	}

	@Inject(method = "canSurvive", at = @At("TAIL"), cancellable = true)
	public void schnowy$canSurviveOnSnowedBlocks(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		BlockState blockState = world.getBlockState(pos.below());
		if (SchnowyEngine.isFullSnowLogged(blockState))
			cir.setReturnValue(true);
	}
}
