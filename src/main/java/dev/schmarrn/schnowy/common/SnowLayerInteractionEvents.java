package dev.schmarrn.schnowy.common;

import dev.schmarrn.schnowy.common.blocks.SchnowyProperties;
import dev.schmarrn.schnowy.common.enchantments.Enchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class SnowLayerInteractionEvents implements PlayerBlockBreakEvents.After, UseBlockCallback {

	public SnowLayerInteractionEvents() {
		PlayerBlockBreakEvents.AFTER.register(this);
		UseBlockCallback.EVENT.register(this);
	}
	@Override
	public void afterBlockBreak(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		int layers = 0;
		IntegerProperty layerProperty = null;
		if (state.hasProperty(BlockStateProperties.LAYERS)) {
			layers = state.getValue(BlockStateProperties.LAYERS);
			layerProperty = BlockStateProperties.LAYERS;
		}
		if (state.is(Blocks.SNOW_BLOCK)) {
			layers = 8;
			state = Blocks.SNOW.defaultBlockState();
			layerProperty = BlockStateProperties.LAYERS;
		}
		if (state.hasProperty(SchnowyProperties.HALF_LAYERS)) {
			layers = state.getValue(SchnowyProperties.HALF_LAYERS);
			layerProperty = SchnowyProperties.HALF_LAYERS;
		}
		if (layers > 0) {
			@Nullable
			BlockState placedState;
			int snowClearingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SNOW_CLEARING, player.getMainHandItem());
			if (layers <= snowClearingLevel + 1) {
				placedState = ReplaceableBlocks.withoutSnow(state);
			} else {
				placedState = state.setValue(layerProperty, layers - 1 - snowClearingLevel);
			}
			if (placedState != null) {
				world.setBlock(pos, placedState, Block.UPDATE_ALL);
			}
		}
	}
	@Override
	public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (!itemStack.is(Items.SNOW)) {
			return InteractionResult.PASS;
		}
		GameType type;
		if (player instanceof ServerPlayer serverPlayer) {
			type = serverPlayer.gameMode.getGameModeForPlayer();
		} else {
			type = ClientClassLoadingProtection.getGameType();
		}
		if (!player.blockActionRestricted(world, hitResult.getBlockPos(), type)) {
			BlockState state = world.getBlockState(hitResult.getBlockPos());
			@Nullable
			BlockState withSnow = ReplaceableBlocks.withSnow(state);
			if (withSnow != null) {
				world.setBlock(hitResult.getBlockPos(), withSnow, Block.UPDATE_ALL);
				itemStack.shrink(1);
				return InteractionResult.sidedSuccess(world.isClientSide());
			}
			if (!state.is(Blocks.SNOW) && (state.hasProperty(BlockStateProperties.LAYERS) || state.hasProperty(SchnowyProperties.HALF_LAYERS))) {
				if (state.hasProperty(BlockStateProperties.LAYERS)) {
					int layers = state.getValue(BlockStateProperties.LAYERS);
					if (layers < 8) {
						world.setBlock(hitResult.getBlockPos(), state.setValue(BlockStateProperties.LAYERS, layers + 1), Block.UPDATE_ALL);
						itemStack.shrink(1);
						return InteractionResult.sidedSuccess(world.isClientSide());
					}
				}
				if (state.hasProperty(SchnowyProperties.HALF_LAYERS)) {
					int layers = state.getValue(SchnowyProperties.HALF_LAYERS);
					if (layers < 4) {
						world.setBlock(hitResult.getBlockPos(), state.setValue(SchnowyProperties.HALF_LAYERS, layers + 1), Block.UPDATE_ALL);
						itemStack.shrink(1);
						return InteractionResult.sidedSuccess(world.isClientSide());
					}
				}
			}
		}
		return InteractionResult.PASS;
	}

	private static class ClientClassLoadingProtection {

		private static GameType getGameType() {
			return Optional.ofNullable(Minecraft.getInstance().gameMode.getPlayerMode()).orElse(GameType.DEFAULT_MODE);
		}
	}
}
