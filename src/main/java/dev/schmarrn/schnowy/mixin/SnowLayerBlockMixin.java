package dev.schmarrn.schnowy.mixin;

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

	@Inject(method = "canSurvive", at = @At("TAIL"), cancellable = true)
	public void schnowy$canSurviveOnSnowedBlocks(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {

		BlockState blockState = world.getBlockState(pos.below());
		if (SchnowyEngine.isFullSnowLogged(blockState))
			cir.setReturnValue(true);
	}
}
