package dev.schmarrn.schnowy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SchnowyUtils {
	public static SnowPlacementInfo getSnowAbove(ServerLevel level, BlockPos pos, BlockState state, int iteration) {
		// TODO: Get the blocks below it to determine whether it should accumulate more snow or not
		//       also: Put powder snow and packed ice generation in here.
		//       Maybe: Remove the boolean and make it an option, or just keep it
		if (state.hasProperty(SnowLayerBlock.LAYERS) && state.getValue(SnowLayerBlock.LAYERS) < 8) {
			// If we got Layers, but aren't full, increment them
			int newLayerCount = state.getValue(SnowLayerBlock.LAYERS) + 1;
			return new SnowPlacementInfo(state.setValue(SnowLayerBlock.LAYERS, newLayerCount), pos, true);
		} else {
			return new SnowPlacementInfo(Blocks.SNOW.defaultBlockState(), pos, true);
		}
	}
	public record SnowPlacementInfo(BlockState state, BlockPos pos, boolean valid) {
	}
}
