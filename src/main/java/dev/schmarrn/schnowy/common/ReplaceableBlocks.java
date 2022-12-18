package dev.schmarrn.schnowy.common;

import dev.schmarrn.schnowy.common.blocks.SchnowyBlocks;
import net.minecraft.Util;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReplaceableBlocks {
	public static final List<Replacement> BLOCKS = Util.make(() -> {
		List<Replacement> blocks = new ArrayList<>();
		SchnowyBlocks.SLABS.forEach((base, snowed) -> blocks.add(new Replacement(base, snowed, state-> state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM, true)));
		SchnowyBlocks.STAIRS.forEach((base, snowed) -> blocks.add(new Replacement(base, snowed, state-> state.getValue(StairBlock.HALF) == Half.BOTTOM, true)));
		SchnowyBlocks.FENCES.forEach((base, snowedFence) -> blocks.add(Replacement.of(base, snowedFence, true)));
		SchnowyBlocks.FLOWERS.forEach((emptySlab, snowedSlab) -> blocks.add(Replacement.of(emptySlab, snowedSlab, false)));
		SchnowyBlocks.SNOWED_GRASS.forEach((base, snowed) -> blocks.add(Replacement.of(base, snowed, false)));
		blocks.add(Replacement.of(Blocks.DEAD_BUSH, SchnowyBlocks.SNOWED_DEAD_BUSH, false));
		return blocks;
	});

	@Nullable
	public static BlockState withSnow(BlockState state) {
		@Nullable
		Block block = null;
		for (Replacement replacement: BLOCKS) {
			if (state.is(replacement.withoutSnow())) {
				if (replacement.isSnowable().test(state))
					return null;
				block = replacement.withSnow();
				break;
			}
		}
		if (block != null) {
			BlockState without = block.defaultBlockState();
			for (Property<?> prop: state.getProperties()) {
				without = with(without, state, prop);
			}
			return without;
		}
		return null;
	}

	@Nullable
	public static BlockState withoutSnow(BlockState state) {
		@Nullable
		Block block = null;
		for (Replacement replacement: BLOCKS) {
			if (state.is(replacement.withSnow())) {
				block = replacement.withoutSnow();
				break;
			}
		}
		if (block != null) {
			BlockState without = block.defaultBlockState();
			for (Property<?> prop: state.getProperties()) {
				without = with(without, state, prop);
			}
			return without;
		}
		return null;
	}

	public static boolean shouldMoveDown(BlockState state) {
		for (Replacement replacement: BLOCKS) {
			if (state.is(replacement.withoutSnow())) {
				return !replacement.moveDown();
			}
		}
		return false;
	}

	public static boolean isSchnowySnow(BlockState state) {
		for (Replacement replacement: BLOCKS) {
			if (state.is(replacement.withSnow())) {
				return true;
			}
		}
		return false;
	}

	private static <T extends Comparable<T>> BlockState with(BlockState on, BlockState from, Property<T> prop) {
		if (from.hasProperty(prop) && on.hasProperty(prop)) {
			return on.setValue(prop, from.getValue(prop));
		}
		return on;
	}
}
