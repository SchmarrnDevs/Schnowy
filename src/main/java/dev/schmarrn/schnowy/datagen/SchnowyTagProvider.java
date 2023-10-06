package dev.schmarrn.schnowy.datagen;

import dev.schmarrn.schnowy.common.blocks.SchnowyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class SchnowyTagProvider extends FabricTagProvider.BlockTagProvider {

	public SchnowyTagProvider(FabricDataOutput dataGenerator, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(dataGenerator, completableFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
			.add(SchnowyBlocks.FLOWERS.values().toArray(Block[]::new))
			.add(SchnowyBlocks.SLABS.values().toArray(Block[]::new))
			.add(SchnowyBlocks.STAIRS.values().toArray(Block[]::new))
			.add(SchnowyBlocks.FENCES.values().toArray(Block[]::new))
			.add(SchnowyBlocks.SNOWED_GRASS.values().toArray(Block[]::new))
			.add(SchnowyBlocks.SNOWED_DEAD_BUSH);
		getOrCreateTagBuilder(BlockTags.FENCES)
			.add(SchnowyBlocks.FENCES.values().toArray(Block[]::new));
		getOrCreateTagBuilder(BlockTags.WOODEN_FENCES)
			.add(SchnowyBlocks.FENCES.values().toArray(Block[]::new));
	}
}
