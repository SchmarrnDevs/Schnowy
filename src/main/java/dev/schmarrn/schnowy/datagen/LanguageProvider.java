package dev.schmarrn.schnowy.datagen;

import dev.schmarrn.schnowy.common.enchantments.Enchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class LanguageProvider extends FabricLanguageProvider {
	public LanguageProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		translationBuilder.add(Enchantments.SNOW_CLEARING, "Snow Clearing");
	}
}
