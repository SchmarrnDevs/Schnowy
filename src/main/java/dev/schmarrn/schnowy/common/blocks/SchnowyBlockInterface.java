package dev.schmarrn.schnowy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public interface SchnowyBlockInterface {
	/**
	 * @return whether the block can currently be snow logged
	 */
	boolean canLog(LevelReader level, BlockPos pos);
}
