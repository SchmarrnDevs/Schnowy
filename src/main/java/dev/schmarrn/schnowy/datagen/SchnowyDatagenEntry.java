package dev.schmarrn.schnowy.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SchnowyDatagenEntry implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(LanguageProvider::new);
		pack.addProvider(LootTableProvider::new);
		pack.addProvider(SchnowyBlockModelProvider::new);
		pack.addProvider(SchnowyTagProvider::new);
	}
}
