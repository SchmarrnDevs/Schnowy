package dev.schmarrn.schnowy.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerWorld.class)
public class ChunkSnowLayerAccumulation {
//	@Inject(method = "tickChunk", at = @At(shift = At.Shift.BEFORE, by = 1, value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;canSetSnow(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
//	public void schnowy$tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler, BlockPos blockPos, BlockPos blockPos2, Biome biome) {
//		ServerWorld world = (ServerWorld) (Object) this;
//		BlockState state = world.getBlockState(blockPos);
//		if (state.contains(SnowBlock.LAYERS)) {
//			Schnowy.LOGGER.info("got block {}", state);
//			int layerCount = state.get(SnowBlock.LAYERS) + 1;
//			Schnowy.LOGGER.info("Layer Count: {}", layerCount);
//			if (layerCount < 8) {
//				world.setBlockState(blockPos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, layerCount));
//			}
//		}
//	}

	@ModifyArg(
			method = "tickChunk",
			index = 1, // argument index, starting with 0
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
					ordinal = 1 // the second call to this function
			)
	)
	public BlockState schnowy$tickChunk(BlockPos pos, BlockState par2) {
		ServerWorld world = (ServerWorld) (Object) this;

		BlockState state = world.getBlockState(pos);
		if (state.contains(SnowBlock.LAYERS)) {
			return state.with(SnowBlock.LAYERS, Math.min(state.get(SnowBlock.LAYERS) + 1, SnowBlock.MAX_LAYERS));
		}
		return par2;
	}
}
