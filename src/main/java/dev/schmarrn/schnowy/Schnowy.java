package dev.schmarrn.schnowy;

import dev.schmarrn.schnowy.common.PlayerJoinEvent;
import dev.schmarrn.schnowy.common.SchnowyEngine;
import dev.schmarrn.schnowy.common.SchnowyGameRules;
import dev.schmarrn.schnowy.common.SnowLayerInteractionEvents;
import dev.schmarrn.schnowy.common.blocks.SchnowyBlocks;
import dev.schmarrn.schnowy.common.enchantments.Enchantments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.core.jmx.Server;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerLoginConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;
import org.quiltmc.qsl.worldgen.biome.api.ModificationPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Schnowy implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Schnowy");

	public static String
			MODID = "";
	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Ho Ho Ho, let's get {}!", mod.metadata().name());
		MODID = mod.metadata().id();
		Enchantments.init();
		SchnowyBlocks.init();
		SchnowyGameRules.init();
		SnowLayerInteractionEvents.getInstance();
		SchnowyEngine.initialize();

		ServerPlayConnectionEvents.JOIN.register(new PlayerJoinEvent());

		// Only the Nether shall not have the snow
		BiomeModifications.create(new ResourceLocation("schnowy", "everywhere"))
				.add(
						ModificationPhase.POST_PROCESSING,
						selectionContext -> !BiomeSelectors.foundInTheNether().test(selectionContext),
						modificationContext -> {
							modificationContext.getWeather().setHasPrecipitation(true);
							modificationContext.getWeather().setTemperature(-0.5f);
							modificationContext.getWeather().setDownfall(0.4f);
						});
	}
}
