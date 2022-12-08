package dev.schmarrn.schnowy.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.function.BiConsumer;

public class LootTableProvider extends SimpleFabricLootTableProvider {
	public LootTableProvider(FabricDataGenerator dataGenerator, LootContextParamSet lootContextType) {
		super(dataGenerator, lootContextType);
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		consumer.accept(Blocks.SNOW.getLootTable(), LootTable.lootTable());
	}
}
