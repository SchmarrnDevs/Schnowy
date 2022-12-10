package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.ReplaceableBlocks;
import dev.schmarrn.schnowy.common.blocks.SchnowyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeMixin {

	@Inject(method = "shouldSnow", at = @At("HEAD"), cancellable = true)
	public void schnowy$shouldSnow(LevelReader world, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
		Biome biome = (Biome) (Object) this;

		if (biome.warmEnoughToRain(blockPos)) {
			cir.setReturnValue(false);
		} else {
			if (blockPos.getY() >= world.getMinBuildHeight() && blockPos.getY() < world.getMaxBuildHeight() && world.getBrightness(LightLayer.BLOCK, blockPos) < 10) {
				BlockState blockState = world.getBlockState(blockPos);
				// TODO: replace with simpler mixin v-- the or-ed boolean here is the important part, everything else is the same in the original fn.
				if ((blockState.isAir() || blockState.hasProperty(SnowLayerBlock.LAYERS) || blockState.hasProperty(SchnowyProperties.HALF_LAYERS) || ReplaceableBlocks.withSnow(blockState) != null) && Blocks.SNOW.defaultBlockState().canSurvive(world, blockPos)) {
					cir.setReturnValue(true);
					return;
				}
				blockState = world.getBlockState(blockPos.below());
				if (blockState.hasProperty(SnowLayerBlock.LAYERS) || blockState.hasProperty(SchnowyProperties.HALF_LAYERS) || ReplaceableBlocks.withSnow(blockState) != null) {
					cir.setReturnValue(true);
				} else {
					cir.setReturnValue(false);
				}
			} else {
				cir.setReturnValue(false);
			}
		}
	}
}
