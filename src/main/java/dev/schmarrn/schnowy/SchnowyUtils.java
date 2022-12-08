package dev.schmarrn.schnowy;

import dev.schmarrn.schnowy.common.ReplaceableBlocks;
import dev.schmarrn.schnowy.common.blocks.SchnowyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SchnowyUtils {
	public static SnowPlacementInfo getNewSnow(ServerLevel level, BlockPos pos, BlockState state) {
		// snow should only accumulate if the block below is no powder snow
		boolean canAccumulate = !level.getBlockState(pos.below()).is(Blocks.POWDER_SNOW);

		// for now: doesn't work because powder snow doesn't trigger snow accumulation
		//if (level.getBlockState(pos.below(4)).is(Blocks.SNOW_BLOCK)) {
		//	return new SnowPlacementInfo(Blocks.PACKED_ICE.defaultBlockState(), pos.below(4), true);
		//}
		// stacking snow
		if (canAccumulate) {
			if (state.hasProperty(SnowLayerBlock.LAYERS)) {
				// If we got Layers, increment them - if full, make it a snow block or powder snow if high enough
				int newLayerCount = state.getValue(SnowLayerBlock.LAYERS) + 1;
				if (newLayerCount < 8) {
					return new SnowPlacementInfo(state.setValue(SnowLayerBlock.LAYERS, newLayerCount), pos);
				} else if (level.getBlockState(pos.below(3)).is(Blocks.SNOW_BLOCK) || level.getBlockState(pos.below()).is(BlockTags.LEAVES)) {
					return new SnowPlacementInfo(Blocks.POWDER_SNOW.defaultBlockState(), pos);
				}
			}
			if (state.hasProperty(SchnowyProperties.HALF_LAYERS)) {
				// If we got Layers, increment them - if full, make it a snow block or powder snow if high enough
				int newLayerCount = state.getValue(SchnowyProperties.HALF_LAYERS) + 1;
				if (newLayerCount < 4) {
					return new SnowPlacementInfo(state.setValue(SchnowyProperties.HALF_LAYERS, newLayerCount), pos);
				}
			}
			BlockState newState = ReplaceableBlocks.withSnow(state);
			if (newState != null) {
				return new SnowPlacementInfo(newState, pos);
			}
			state = level.getBlockState(pos.below());
			if (state.hasProperty(SchnowyProperties.HALF_LAYERS)) {
				// If we got Layers, increment them - if full, make it a snow block or powder snow if high enough
				int newLayerCount = state.getValue(SchnowyProperties.HALF_LAYERS) + 1;
				if (newLayerCount < 4) {
					return new SnowPlacementInfo(state.setValue(SchnowyProperties.HALF_LAYERS, newLayerCount), pos.below());
				}
			}
			if (state.hasProperty(SnowLayerBlock.LAYERS)) {
				// If we got Layers, increment them - if full, make it a snow block or powder snow if high enough
				int newLayerCount = state.getValue(SnowLayerBlock.LAYERS) + 1;
				if (newLayerCount < 8) {
					return new SnowPlacementInfo(state.setValue(SnowLayerBlock.LAYERS, newLayerCount), pos.below());
				}
			}
			newState = ReplaceableBlocks.withSnow(state);
			if (newState != null) {
				return new SnowPlacementInfo(newState, pos.below());
			}
		}

		return new SnowPlacementInfo(Blocks.SNOW.defaultBlockState(), pos);
	}
	public record SnowPlacementInfo(BlockState state, BlockPos pos) {
	}
}
