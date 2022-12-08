package dev.schmarrn.schnowy.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class SchnowyDatagenEntry implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		fabricDataGenerator.addProvider(new LanguageProvider(fabricDataGenerator));
		fabricDataGenerator.addProvider(new LootTableProvider(fabricDataGenerator, LootContextParamSets.BLOCK));
		fabricDataGenerator.addProvider(new SchnowyBlockModelProvider(fabricDataGenerator));
		fabricDataGenerator.addProvider(new SchnowyTagProvider(fabricDataGenerator));
	}
}
