package dev.schmarrn.schnowy.common;

import dev.schmarrn.schnowy.common.blocks.SchnowyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;

import java.util.*;

public class SchnowyEngine {
	private static final List<Direction> horizontalDirections = List.of(Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH);
	public static final int SNOW_HEIGHT = 4;
	public static final float BLIZZARD_MULTIPLIER = 5f;
	public static final float DAYTIME_MULTIPLIER = 0.75f;
	public static final float NIGHTTIME_MULTIPLIER = 2f;
	public static final float RANDOM_SNOW_SPEED_WEIGHT = 5f; // The higher the Value, the lesser the randomness

	public static Optional<SnowPlacementInfo> getNewSnow(ServerLevel level, BlockPos pos, BlockState state) {
		// for now: don't enable, no idea what kind of block to put there
		// if (level.getBlockState(pos.below(4)).is(Blocks.SNOW_BLOCK)) {
		//	 return new SnowPlacementInfo(Blocks.PACKED_ICE.defaultBlockState(), pos.below(4), true);
		// }
		// stacking snow
		if (state.hasProperty(SnowLayerBlock.LAYERS)) {
			// Normal Snow Layers, Snowed Flowers, Snowed Grass
			// If we got Layers, increment them - if full, make it a snow block or powder snow if high enough
			int newLayerCount = state.getValue(SnowLayerBlock.LAYERS) + 1;
			if (state.is(Blocks.SNOW) && newLayerCount >= 8) {
				if (isSnowAtMaxHeight(level, pos) || level.getBlockState(pos.below()).is(BlockTags.LEAVES)) {
					return Optional.of(new SnowPlacementInfo(Blocks.POWDER_SNOW.defaultBlockState(), pos));
				}
				return Optional.of(new SnowPlacementInfo(Blocks.SNOW_BLOCK.defaultBlockState(), pos));
			}
			if (newLayerCount <= 8) {
				return Optional.of(new SnowPlacementInfo(state.setValue(SnowLayerBlock.LAYERS, newLayerCount), pos));
			}
		} else if (state.hasProperty(SchnowyProperties.HALF_LAYERS)) {
			// Snowed Slabs
			// If we got Layers, increment them
			int newLayerCount = state.getValue(SchnowyProperties.HALF_LAYERS) + 1;
			if (newLayerCount <= 4) {
				return Optional.of(new SnowPlacementInfo(state.setValue(SchnowyProperties.HALF_LAYERS, newLayerCount), pos));
			}
		} else {
			// Get the snowed equivalent of the block
			BlockState newState = ReplaceableBlocks.withSnow(state);
			if (newState != null) {
				return Optional.of(new SnowPlacementInfo(newState, pos));
			}
		}

		// if we can accumulate snow (this block isn't powder snow), then put a snow layer on top of it
		boolean canAccumulate = !level.getBlockState(pos).is(Blocks.POWDER_SNOW);
		if (canAccumulate && Blocks.SNOW.canSurvive(Blocks.SNOW.defaultBlockState(), level, pos.above())) {
			return Optional.of(new SnowPlacementInfo(Blocks.SNOW.defaultBlockState(), pos.above()));
		}

		// if nothing can be done, return the same blockstate
		return Optional.empty();
	}

	private static boolean isSnowAtMaxHeight(ServerLevel level, BlockPos pos) {
		for (int i = 1; i < SNOW_HEIGHT; i++) {
			if (!level.getBlockState(pos.below(i)).is(Blocks.SNOW_BLOCK))
				return false;
		}
		return true;
	}

	private static BlockPos findLowestLayerPos(ServerLevel level, BlockPos pos, int depth) {
		if (--depth < 0)
			return pos;

		BlockState blockState = level.getBlockState(pos);
		int height = getHeight(level, pos, true);
		//if this should be in the air, return this pos to set layer
		if (height == 0 && !isSnow(blockState)) {
			return pos.below();
		}
		height = getHeight(level, pos, false);

		//if we are in the air and below us is space, move down
		if (height < 0) {
			return findLowestLayerPos(level, pos.below(heightBelowToBlocks(height)), depth);
		}

		//if we are in the air and below us is space, move down
		List<Direction> shuffledDirs = new ArrayList<>(horizontalDirections);
		Collections.shuffle(shuffledDirs);
		for (Direction dir: shuffledDirs) {
			//if a neighbour is lower by more than 2 layers, move there
			if (getHeight(level, pos.relative(dir), false) + 2 < height) {
				return findLowestLayerPos(level, pos.relative(dir), depth);
			}
		}
		//this is the lowest point
		return pos;
	}

	private static int getHeight(ServerLevel level, BlockPos pos, boolean realSnowHeight) {
		BlockState blockState = level.getBlockState(pos);
		if (level.isOutsideBuildHeight(pos)) {
			return 0;
		}
		if (blockState.isAir()) {
			return getHeight(level, pos.below(), realSnowHeight) - 8;
		}
		if (blockState.hasProperty(SnowLayerBlock.LAYERS)) {
			return blockState.getValue(SnowLayerBlock.LAYERS);
		} else if (blockState.hasProperty(SchnowyProperties.HALF_LAYERS)) {
			return blockState.getValue(SchnowyProperties.HALF_LAYERS) + (realSnowHeight ? 0 : 4);
		} else if (blockState.is(Blocks.SNOW_BLOCK)) {
			return 8;
		}
		@Nullable
		BlockState withSnow = ReplaceableBlocks.withSnow(blockState);
		if (withSnow != null) {
			if (withSnow.hasProperty(SnowLayerBlock.LAYERS)) {
				return 0;
			}
			if (withSnow.hasProperty(SchnowyProperties.HALF_LAYERS)) {
				return 4;
			}
		}
		return 8;
	}

	private static int heightBelowToBlocks(int height) {
		return Math.floorDiv(-height -1, 8) + 1;
	}

	private static boolean isSnow(BlockState state) {
		return state.is(Blocks.SNOW_BLOCK)
				|| state.hasProperty(SnowLayerBlock.LAYERS)
				|| state.hasProperty(SchnowyProperties.HALF_LAYERS)
				|| ReplaceableBlocks.BLOCKS.stream()
					.filter(replacement -> state.is(replacement.withoutSnow()))
					.findFirst()
					.map(replacement -> !replacement.moveDown())
					.orElse(false);
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
		if (level.random.nextFloat() * snowSpeed(level) > 0.5f && level.isRaining()) {
			BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, level.getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15)).below();
			Biome biome = level.getBiome(pos).value();
			pos = findLowestLayerPos(level, pos, 512);
			if (level.getBlockState(pos).isAir() && isFullSnowLogged(level.getBlockState(pos.below())))
				pos = pos.below();
			// snow gen
			BlockState state = level.getBlockState(pos);
			if (!biome.warmEnoughToRain(pos) && level.getBrightness(LightLayer.BLOCK, pos.above()) < 10) {
				getNewSnow(level, pos, state)
						.ifPresent(info -> level.setBlockAndUpdate(info.pos, info.state));
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

	public static boolean isFullSnowLogged(BlockState state) {
		if (ReplaceableBlocks.BLOCKS.stream().map(Replacement::withSnow).toList().contains(state.getBlock())) {
			if (state.hasProperty(SchnowyProperties.HALF_LAYERS) && state.getValue(SchnowyProperties.HALF_LAYERS) == 4)
				return true;
			if (state.hasProperty(SnowLayerBlock.LAYERS) && state.getValue(SnowLayerBlock.LAYERS) == 8)
				return true;
		}
		return false;
	}

	public static void initialize() {
		ServerTickEvents.END.register(SchnowyEngine::tick);
	}
	public record SnowPlacementInfo(BlockState state, BlockPos pos) {
	}
	public static class Blizzard {
		boolean active;
		int time;
		Random random = new Random();

		private void setTimeUntilNextBlizzard() {
			this.time = random.nextInt(40*60*20) + 100*60*20;
		}

		private void setTimeDuration() {
			this.time = random.nextInt(10*60*20) + 15*60*20;
		}

		private Blizzard() {
			this.active = false;
			setTimeUntilNextBlizzard();
		}

		private void tick(MinecraftServer server) {
			time--;
			if (time == 0) {
				if (active) {
					active = false;
					setTimeUntilNextBlizzard();
					server.sendSystemMessage(Component.translatable("announcement.schnowy.blizzard.stop"));
				} else {
					active = true;
					setTimeDuration();
					server.sendSystemMessage(Component.translatable("announcement.schnowy.blizzard.start"));
				}
			}
		}
		public boolean isActive() {
			return active;
		}
	}

	public static Blizzard getBlizzard() {
		return blizzard;
	}
}
