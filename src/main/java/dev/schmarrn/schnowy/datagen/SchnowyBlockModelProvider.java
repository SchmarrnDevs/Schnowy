package dev.schmarrn.schnowy.datagen;

import dev.schmarrn.schnowy.Schnowy;
import dev.schmarrn.schnowy.common.blocks.SchnowyBlocks;
import dev.schmarrn.schnowy.common.blocks.SchnowyProperties;
import dev.schmarrn.schnowy.common.blocks.SnowedFlower;
import dev.schmarrn.schnowy.common.blocks.SnowedSlab;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.Util;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SchnowyBlockModelProvider extends FabricModelProvider {
	public SchnowyBlockModelProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	private static final Map<Integer, ModelTemplate> SNOWY_SLAB_TEMPLATES = Util.make(() -> {
		Map<Integer, ModelTemplate> map = new HashMap<>();
		map.put(1, new ModelTemplate(Optional.of(new ResourceLocation(Schnowy.MODID, "block/lowerslab1")), Optional.empty(), TextureSlot.SIDE, TextureSlot.BOTTOM));
		map.put(2, new ModelTemplate(Optional.of(new ResourceLocation(Schnowy.MODID, "block/lowerslab2")), Optional.empty(), TextureSlot.SIDE, TextureSlot.BOTTOM));
		map.put(3, new ModelTemplate(Optional.of(new ResourceLocation(Schnowy.MODID, "block/lowerslab3")), Optional.empty(), TextureSlot.SIDE, TextureSlot.BOTTOM));
		map.put(4, new ModelTemplate(Optional.of(new ResourceLocation(Schnowy.MODID, "block/lowerslab4")), Optional.empty(), TextureSlot.SIDE, TextureSlot.BOTTOM));
		return map;
	});
	private static final Map<Integer, ModelTemplate> SNOWY_CROSS_TEMPLATES = Util.make(() -> {
		Map<Integer, ModelTemplate> map = new HashMap<>();
		for (int i = 1; i <= 8; i++) {
			map.put(i, new ModelTemplate(Optional.of(new ResourceLocation(Schnowy.MODID, "block/cross" + i)), Optional.empty(), TextureSlot.CROSS));
		}
		return map;
	});
	private static final Map<Integer, ModelTemplate> SNOWY_TINTED_CROSS_TEMPLATES = Util.make(() -> {
		Map<Integer, ModelTemplate> map = new HashMap<>();
		for (int i = 1; i <= 8; i++) {
			map.put(i, new ModelTemplate(Optional.of(new ResourceLocation(Schnowy.MODID, "block/tinted_cross" + i)), Optional.empty(), TextureSlot.CROSS));
		}
		return map;
	});
	@Override
	public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
		for (SnowedSlab snowedSlab : SchnowyBlocks.SLABS.values()) {
			blockStateModelGenerator.blockStateOutput
					.accept(
							MultiVariantGenerator.multiVariant(snowedSlab)
									.with(
											PropertyDispatch.property(SchnowyProperties.HALF_LAYERS)
													.select(1, generateSlabModel(1, snowedSlab, snowedSlab.textureParent, blockStateModelGenerator))
													.select(2, generateSlabModel(2, snowedSlab, snowedSlab.textureParent, blockStateModelGenerator))
													.select(3, generateSlabModel(3, snowedSlab, snowedSlab.textureParent, blockStateModelGenerator))
													.select(4, generateSlabModel(4, snowedSlab, snowedSlab.textureParent, blockStateModelGenerator))
									)
					);
		}
		for (SnowedFlower snowedFlower : SchnowyBlocks.FLOWERS.values()) {
			blockStateModelGenerator.blockStateOutput
					.accept(
							MultiVariantGenerator.multiVariant(snowedFlower)
									.with(generateCrossPropertyDispatch(snowedFlower, snowedFlower.parent, false, blockStateModelGenerator))
					);
		}
		blockStateModelGenerator.blockStateOutput
				.accept(
						MultiVariantGenerator.multiVariant(SchnowyBlocks.SNOWED_GRASS)
								.with(generateCrossPropertyDispatch(SchnowyBlocks.SNOWED_GRASS, Blocks.GRASS, true, blockStateModelGenerator))
				);
		blockStateModelGenerator.blockStateOutput
				.accept(
						MultiVariantGenerator.multiVariant(SchnowyBlocks.SNOWED_DEAD_BUSH)
								.with(generateCrossPropertyDispatch(SchnowyBlocks.SNOWED_DEAD_BUSH, Blocks.DEAD_BUSH, false, blockStateModelGenerator))
				);
	}

	private static Variant generateSlabModel(int level, Block block, Block parent, BlockModelGenerators blockStateModelGenerator) {
		return Variant.variant().with(VariantProperties.MODEL,
				SNOWY_SLAB_TEMPLATES.get(level).createWithSuffix(block, level + "",
						blockStateModelGenerator.texturedModels.getOrDefault(parent, TexturedModel.CUBE.get(parent)).getMapping(),
						blockStateModelGenerator.modelOutput));
	}

	private static PropertyDispatch generateCrossPropertyDispatch(Block block, Block parent, boolean isTinted, BlockModelGenerators blockStateModelGenerator) {
		PropertyDispatch.C1<Integer> property = PropertyDispatch.property(BlockStateProperties.LAYERS);
		for (Integer i: BlockStateProperties.LAYERS.getPossibleValues()) {
			property.select(i, generateCrossModel(i, block, parent, isTinted, blockStateModelGenerator));
		}
		return property;
	}
	private static Variant generateCrossModel(int level, Block block, Block parent, boolean isTinted, BlockModelGenerators blockStateModelGenerator) {
		Map<Integer, ModelTemplate> template = isTinted ? SNOWY_TINTED_CROSS_TEMPLATES : SNOWY_CROSS_TEMPLATES;
		return Variant.variant().with(VariantProperties.MODEL,
				template.get(level).createWithSuffix(block, level + "",
						TextureMapping.cross(parent),
						blockStateModelGenerator.modelOutput));
	}
	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {

	}
}
