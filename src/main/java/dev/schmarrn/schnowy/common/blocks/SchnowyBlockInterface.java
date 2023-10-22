package dev.schmarrn.schnowy.common.blocks;

import dev.schmarrn.schnowy.mixin.BlockPropertiesMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface SchnowyBlockInterface {
	/**
	 * @return whether the block can currently be snow logged
	 */
	boolean canLog(LevelReader level, BlockPos pos);

	static BlockBehaviour.Properties notReplaceableHack(BlockBehaviour.Properties properties) {
		((BlockPropertiesMixin) properties).setReplaceable(false);
		return properties;
	}
}
