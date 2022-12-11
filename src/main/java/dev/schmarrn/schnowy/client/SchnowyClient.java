package dev.schmarrn.schnowy.client;

import dev.schmarrn.schnowy.common.blocks.SchnowyBlocks;
import dev.schmarrn.schnowy.common.blocks.SnowedGrass;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.mixin.client.rendering.BlockColorsMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import oshi.util.tuples.Pair;

import java.util.function.BiConsumer;

public class SchnowyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		for (Block block: SchnowyBlocks.FLOWERS.values()) {
			BlockRenderLayerMap.put(RenderType.cutout(), block);
		}
		SchnowyBlocks.SNOWED_GRASS.forEach((key, block) -> {
			BlockRenderLayerMap.put(RenderType.cutout(), block);
			ColorProviderRegistry.BLOCK.register((blockState, level, blockPos, i) -> Minecraft.getInstance().getBlockColors().getColor(key.defaultBlockState(), level, blockPos, i),block);
		});
		BlockRenderLayerMap.put(RenderType.cutout(), SchnowyBlocks.SNOWED_DEAD_BUSH);
	}
}
