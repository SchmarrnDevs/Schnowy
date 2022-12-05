package dev.schmarrn.schnowy.common;

import dev.schmarrn.schnowy.common.enchantments.Enchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;


public class SnowLayerBreaking implements PlayerBlockBreakEvents.After {
	@Override
	public void afterBlockBreak(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		int layers = 0;
		if (state.is(Blocks.SNOW_BLOCK)) {
			layers = 8;
		}
		if (state.hasProperty(BlockStateProperties.LAYERS)) {
			layers = state.getValue(BlockStateProperties.LAYERS);
		}
		//TODO: add our blocks
		if (layers > 0) {
			@Nullable
			BlockState placedState;
			int snowClearingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SNOW_CLEARING, player.getMainHandItem());
			if (layers <= snowClearingLevel + 1) {
				placedState = ReplaceableBlocks.withoutSnow(state);
			} else {
				placedState = Blocks.SNOW.defaultBlockState().setValue(BlockStateProperties.LAYERS, layers - 1 - snowClearingLevel);
			}
			if (placedState != null) {
				world.setBlock(pos, placedState, Block.UPDATE_ALL);
			}
		}
	}
}
