package dev.schmarrn.schnowy.common;

import dev.schmarrn.schnowy.common.enchantments.Enchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SnowLayerBreaking implements PlayerBlockBreakEvents.After {
	@Override
	public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		int layers = 0;
		if (state.isOf(Blocks.SNOW_BLOCK)) {
			layers = 8;
		}
		if (state.contains(Properties.LAYERS)) {
			layers = state.get(Properties.LAYERS);
		}
		//TODO: add our blocks
		if (layers > 0) {
			@Nullable
			BlockState placedState;
			int snowClearingLevel = EnchantmentHelper.getLevel(Enchantments.SNOW_CLEARING, player.getMainHandStack());
			if (layers <= snowClearingLevel + 1) {
				placedState = ReplaceableBlocks.withoutSnow(state);
			} else {
				placedState = Blocks.SNOW.getDefaultState().with(Properties.LAYERS, layers - 1 - snowClearingLevel);
			}
			if (placedState != null) {
				world.setBlockState(pos, placedState, Block.NOTIFY_ALL);
			}
		}
	}
}
