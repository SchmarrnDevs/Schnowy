package dev.schmarrn.schnowy.common;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public record Replacement(Block withoutSnow, Block withSnow, Predicate<BlockState> isSnowable, boolean moveDown) {

	public static Replacement of(Block withoutSnow, Block withSnow, boolean moveDown) {
		return new Replacement(withoutSnow, withSnow, state -> true, moveDown);
	}
}
