package dev.schmarrn.schnowy.common;

import dev.schmarrn.schnowy.common.blocks.SchnowyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
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
	public static final int SNOW_HEIGHT = 4;
	public static final float BLIZZARD_MULTIPLIER = 5f;
	public static final float DAYTIME_MULTIPLIER = 0.75f;
	public static final float NIGHTTIME_MULTIPLIER = 2f;
	public static final float RANDOM_SNOW_SPEED_WEIGHT = 5f; // The higher the Value, the lesser the randomness

	public static SnowPlacementInfo getNewSnow(ServerLevel level, BlockPos pos, BlockState state) {
		// for now: don't enable, no idea what kind of block to put there
		// if (level.getBlockState(pos.below(4)).is(Blocks.SNOW_BLOCK)) {
		//	 return new SnowPlacementInfo(Blocks.PACKED_ICE.defaultBlockState(), pos.below(4), true);
		// }

		// stacking snow
		if (state.hasProperty(SnowLayerBlock.LAYERS)) {
			// Normal Snow Layers, Snowed Flowers, Snowed Grass
			// If we got Layers, increment them - if full, make it a snow block or powder snow if high enough
			int newLayerCount = state.getValue(SnowLayerBlock.LAYERS) + 1;
			if (newLayerCount < 8) {
				return new SnowPlacementInfo(state.setValue(SnowLayerBlock.LAYERS, newLayerCount), pos);
			} else {
				if (level.getBlockState(pos.below(SNOW_HEIGHT - 1)).is(Blocks.SNOW_BLOCK) || level.getBlockState(pos.below()).is(BlockTags.LEAVES)) {
					return new SnowPlacementInfo(Blocks.POWDER_SNOW.defaultBlockState(), pos);
				}
				return new SnowPlacementInfo(Blocks.SNOW_BLOCK.defaultBlockState(), pos);
			}
		} else if (state.hasProperty(SchnowyProperties.HALF_LAYERS)) {
			// Snowed Slabs
			// If we got Layers, increment them
			int newLayerCount = state.getValue(SchnowyProperties.HALF_LAYERS) + 1;
			if (newLayerCount <= 4) {
				return new SnowPlacementInfo(state.setValue(SchnowyProperties.HALF_LAYERS, newLayerCount), pos);
			}
		} else {
			// Get the snowed equivalent of the block
			BlockState newState = ReplaceableBlocks.withSnow(state);
			if (newState != null) {
				return new SnowPlacementInfo(newState, pos);
			}
		}

		// if we can accumulate snow (this block isn't powder snow), then put a snow layer on top of it
		boolean canAccumulate = !level.getBlockState(pos).is(Blocks.POWDER_SNOW);
		if (canAccumulate && Blocks.SNOW.canSurvive(Blocks.SNOW.defaultBlockState(), level, pos.above())) {
			return new SnowPlacementInfo(Blocks.SNOW.defaultBlockState(), pos.above());
		}

		// if nothing can be done, return the same blockstate
		return new SnowPlacementInfo(state, pos);
	}

	public static float snowSpeed(ServerLevel level) {
		// Consider: RANDOM_SNOW_SPEED_WEIGHT = 5f => Range: [0.9f, 1.1f]
		float randomMultiplier = (level.random.nextFloat() - 0.5f) / RANDOM_SNOW_SPEED_WEIGHT + 1f;
		if (blizzard.isActive()) {
			return BLIZZARD_MULTIPLIER * randomMultiplier;
		}
		if (level.isNight()) {
			float moonPhaseModifier = (1f - level.getMoonBrightness())/2f + 0.5f; // Ranges from [0.5f, 1f]
			// Consider: NIGHTTIME_MULTIPLIER = 2f => return ranges from [1f, 2f]
			return NIGHTTIME_MULTIPLIER * moonPhaseModifier * randomMultiplier;
		} else {
			return DAYTIME_MULTIPLIER * randomMultiplier;
		}
	}

	public static void tickChunk(ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
		ChunkPos chunkPos = chunk.getPos();

		if (level.random.nextFloat() * snowSpeed(level) > 0.5f) {
			BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, level.getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15)).below();
			Biome biome = level.getBiome(pos).value();

			BlockState state = level.getBlockState(pos);

			// snow gen
			if (!biome.warmEnoughToRain(pos) && level.isRaining() && level.getBrightness(LightLayer.BLOCK, pos.above()) < 10) {
				SnowPlacementInfo info = getNewSnow(level, pos, state);
				level.setBlockAndUpdate(info.pos, info.state);
			}

			// ice gen
			if (biome.shouldFreeze(level, pos)) {
				level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
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
