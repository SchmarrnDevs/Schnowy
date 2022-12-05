package dev.schmarrn.schnowy.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class InfiniteSnow {
	@Inject(method = "canSetSnow", at = @At("HEAD"), cancellable = true)
	public void schnowy$canSetSnow(WorldView world, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
		Biome biome = (Biome) (Object) this;

		if (biome.doesNotSnow(blockPos)) {
			cir.setReturnValue(false);
		} else {
			if (blockPos.getY() >= world.getBottomY() && blockPos.getY() < world.getTopY() && world.getLightLevel(LightType.BLOCK, blockPos) < 10) {
				BlockState blockState = world.getBlockState(blockPos);
				// TODO: replace with simpler mixin v-- the or-ed boolean here is the important part, everything else is the same in the original fn.
				if ((blockState.isAir() || blockState.contains(SnowBlock.LAYERS)) && Blocks.SNOW.getDefaultState().canPlaceAt(world, blockPos)) {
					cir.setReturnValue(true);
				} else {
					cir.setReturnValue(false);
				}
			} else {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "doesNotSnow", at = @At("HEAD"), cancellable = true)
	public void schnowy$doesNotSnow(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		// Let there be snow in ALL BIOMES.
		cir.setReturnValue(false);
	}

	@Inject(method = "getPrecipitation", at = @At("HEAD"), cancellable = true)
	public void schnowy$getPrecipitation(CallbackInfoReturnable<Biome.Precipitation> cir) {
		// Snow should also be rendered in biomes where there wouldn't be snow normally (desert, savanna)
		cir.setReturnValue(Biome.Precipitation.SNOW);
	}
}
