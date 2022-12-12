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
		translationBuilder.add("announcement.schnowy.blizzard.start", "A blizzard is coming! Seek shelter!");
		translationBuilder.add("announcement.schnowy.blizzard.stop", "The blizzard is over! It is save to come out again.");
	}
}
