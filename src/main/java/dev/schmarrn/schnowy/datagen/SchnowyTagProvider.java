package dev.schmarrn.schnowy.datagen;

import dev.schmarrn.schnowy.common.blocks.SchnowyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class SchnowyTagProvider extends FabricTagProvider.BlockTagProvider {

	public SchnowyTagProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		tag(BlockTags.MINEABLE_WITH_SHOVEL)
				.add(SchnowyBlocks.FLOWERS.values().toArray(Block[]::new))
				.add(SchnowyBlocks.SLABS.values().toArray(Block[]::new))
				.add(SchnowyBlocks.SNOWED_GRASS.values().toArray(Block[]::new))
				.add(SchnowyBlocks.SNOWED_DEAD_BUSH);
	}
}
