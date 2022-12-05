package dev.schmarrn.schnowy.common;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReplaceableBlocks {
	public static final List<Replacement> BLOCKS = Util.make(() -> {
		List<Replacement> blocks = new ArrayList<>();
		//TODO: Fill with blocks;
		return blocks;
	});

	@Nullable
	public static BlockState withSnow(BlockState state) {
		Block block = ReplaceableBlocks.BLOCKS.stream().filter(replacement -> state.isOf(replacement.withSnow())).findFirst().map(Replacement::withSnow).orElse(null);
		if (block != null) {
			BlockState without = block.getDefaultState();
			for (Property<?> prop: state.getProperties()) {
				without = with(without, state, prop);
			}
			return without;
		}
		return null;
	}

	@Nullable
	public static BlockState withoutSnow(BlockState state) {
		Block block = ReplaceableBlocks.BLOCKS.stream().filter(replacement -> state.isOf(replacement.withSnow())).findFirst().map(Replacement::withoutSnow).orElse(null);
		if (block != null) {
			BlockState without = block.getDefaultState();
			for (Property<?> prop: state.getProperties()) {
				without = with(without, state, prop);
			}
			return without;
		}
		return null;
	}

	private static <T extends Comparable<T>> BlockState with(BlockState on, BlockState from, Property<T> prop) {
		if (from.contains(prop) && on.contains(prop)) {
			return on.with(prop, from.get(prop));
		}
		return on;
	}
}
