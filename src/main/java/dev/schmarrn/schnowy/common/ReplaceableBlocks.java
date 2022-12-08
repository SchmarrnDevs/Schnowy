package dev.schmarrn.schnowy.common;

import dev.schmarrn.schnowy.common.blocks.SchnowyBlocks;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReplaceableBlocks {
	public static final List<Replacement> BLOCKS = Util.make(() -> {
		List<Replacement> blocks = new ArrayList<>();
		SchnowyBlocks.SLABS.forEach((emptySlab, snowedSlab) -> blocks.add(new Replacement(emptySlab, snowedSlab)));
		SchnowyBlocks.FLOWERS.forEach((emptySlab, snowedSlab) -> blocks.add(new Replacement(emptySlab, snowedSlab)));
		return blocks;
	});

	@Nullable
	public static BlockState withSnow(BlockState state) {
		Block block = ReplaceableBlocks.BLOCKS.stream().filter(replacement -> state.is(replacement.withoutSnow())).findFirst().map(Replacement::withSnow).orElse(null);
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
		Block block = ReplaceableBlocks.BLOCKS.stream().filter(replacement -> state.is(replacement.withSnow())).findFirst().map(Replacement::withoutSnow).orElse(null);
		if (block != null) {
			BlockState without = block.defaultBlockState();
			for (Property<?> prop: state.getProperties()) {
				without = with(without, state, prop);
			}
			return without;
		}
		return null;
	}

	private static <T extends Comparable<T>> BlockState with(BlockState on, BlockState from, Property<T> prop) {
		if (from.hasProperty(prop) && on.hasProperty(prop)) {
			return on.setValue(prop, from.getValue(prop));
		}
		return on;
	}
}
