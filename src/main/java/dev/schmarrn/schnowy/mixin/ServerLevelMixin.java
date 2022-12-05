package dev.schmarrn.schnowy.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	@ModifyArg(
			method = "tickChunk",
			index = 1, // argument index, starting with 0
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
					ordinal = 1 // the second call to this function
			)
	)
	public BlockState schnowy$tickChunk(BlockPos pos, BlockState par2) {
		ServerLevel world = (ServerLevel) (Object) this;

		BlockState state = world.getBlockState(pos);
		if (state.hasProperty(SnowLayerBlock.LAYERS)) {
			return state.setValue(SnowLayerBlock.LAYERS, Math.min(state.getValue(SnowLayerBlock.LAYERS) + 1, SnowLayerBlock.MAX_HEIGHT));
		}
		return par2;
	}
}
