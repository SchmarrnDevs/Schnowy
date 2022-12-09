package dev.schmarrn.schnowy.common;

import dev.schmarrn.schnowy.common.blocks.SchnowyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;

import javax.swing.text.html.HTMLDocument;
import java.util.Random;

public class SchnowyEngine {
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

	public static float snowSpeed(ServerLevel level) {
		if (blizzard.active) {
			return 5f;
		}
		return level.isNight() ? 2f : 0.75f;
	}

	public static void tickChunk(ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
		ChunkPos chunkPos = chunk.getPos();
		BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, level.getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15));
		Biome biome = level.getBiome(pos).value();
		// biome check because of the nether
		if (biome.shouldSnow(level, pos) && level.random.nextFloat() * snowSpeed(level) > 0.5f) {
			BlockState state = level.getBlockState(pos);

			// snow gen
			SnowPlacementInfo info = getNewSnow(level, pos, state);
			level.setBlockAndUpdate(info.pos, info.state);

			// ice gen
			if (biome.shouldFreeze(level, pos.below())) {
				level.setBlockAndUpdate(pos.below(), Blocks.ICE.defaultBlockState());
			}

			// cauldron filling
			state.getBlock().handlePrecipitation(state, level, pos, biome.getPrecipitation());
		}
	}

	private static final Blizzard blizzard = new Blizzard();
	public static void tick(MinecraftServer server) {
		blizzard.tick(server);
	}

	public static void initialize() {
		ServerTickEvents.END.register(SchnowyEngine::tick);
	}
	public record SnowPlacementInfo(BlockState state, BlockPos pos) {
	}
	private static class Blizzard {
		boolean active;
		int time;
		Random random = new Random();
		private Blizzard() {
			this.active = false;
			this.time = random.nextInt(40*60*20) * 100*60*20;
		}
		private void tick(MinecraftServer server) {
			time--;
			if (time == 0) {
				if (active) {
					active = false;
					this.time = random.nextInt(40*60*20) * 100*60*20;
					//TODO: Lang file
					server.sendSystemMessage(Component.literal("Blizzard has ended"));
				} else {
					active = true;
					this.time = random.nextInt(10*60*20) + 15*60*20;
					//TODO: Lang file
					server.sendSystemMessage(Component.literal("Blizzard has begin, seek shelter"));
				}
			}
		}
		private boolean isActive() {
			return active;
		}
	}
}
