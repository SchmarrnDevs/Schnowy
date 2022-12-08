package dev.schmarrn.schnowy.client;

import dev.schmarrn.schnowy.common.blocks.SchnowyBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.mixin.client.rendering.BlockColorsMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

public class SchnowyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		for (Block block: SchnowyBlocks.FLOWERS.values()) {
			BlockRenderLayerMap.put(RenderType.cutout(), block);
		}
		BlockRenderLayerMap.put(RenderType.cutout(), SchnowyBlocks.SNOWED_GRASS);
		ColorProviderRegistry.BLOCK.register((blockState, level, blockPos, i) -> Minecraft.getInstance().getBlockColors().getColor(Blocks.GRASS.defaultBlockState(), level, blockPos, i),SchnowyBlocks.SNOWED_GRASS);
	}
}
