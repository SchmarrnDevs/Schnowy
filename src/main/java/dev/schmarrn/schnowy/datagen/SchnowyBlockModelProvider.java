package dev.schmarrn.schnowy.datagen;

import dev.schmarrn.schnowy.Schnowy;
import dev.schmarrn.schnowy.common.blocks.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.properties.*;

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
	private static final Map<Integer, ResourceLocation> SNOW_LAYERS = Util.make(() -> {
		Map<Integer, ResourceLocation> map = new HashMap<>();
		for (int i = 1; i <= 7; i++) {
			map.put(i, new ResourceLocation("block/snow_height" + i*2));
		}
		map.put(8, new ResourceLocation(Schnowy.MODID, "block/snow"));
		return map;
	});
	private static final Map<Integer, Map<StairTypes, ModelTemplate>> STAIRS = Util.make(() -> {
		Map<Integer, Map<StairTypes, ModelTemplate>> map = new HashMap<>();
		for (int i = 1; i <= 4; i++) {
			Map<StairTypes, ModelTemplate> templates = map.computeIfAbsent(i, key -> new HashMap<>());
			for (StairTypes shape: StairTypes.values()) {
				templates.put(shape, new ModelTemplate(Optional.of(new ResourceLocation(Schnowy.MODID, "block/stair" + shape.getTemplateSuffix() + i)), Optional.of(shape.getTemplateSuffix() + i), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE));
			}
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
		for (SnowedFence snowedFence : SchnowyBlocks.FENCES.values()) {
			generateFence(snowedFence, snowedFence.parent, blockStateModelGenerator);
		}
		for (SnowedStair snowedStair : SchnowyBlocks.STAIRS.values()) {
			generateStair(snowedStair, snowedStair.textureParent, blockStateModelGenerator);
		}
		for (SnowedGrass snowedGrass : SchnowyBlocks.SNOWED_GRASS.values()) {
			blockStateModelGenerator.blockStateOutput
					.accept(
							MultiVariantGenerator.multiVariant(snowedGrass)
									.with(generateCrossPropertyDispatch(snowedGrass, snowedGrass.parent, true, blockStateModelGenerator))
					);
		}
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

	private static void generateFence(Block fence, Block textureParent, BlockModelGenerators generator) {
		TextureMapping mapping = generator.texturedModels.getOrDefault(textureParent, TexturedModel.CUBE.get(textureParent)).getMapping();
		ResourceLocation resourceLocation = ModelTemplates.FENCE_POST.create(fence, mapping, generator.modelOutput);
		ResourceLocation resourceLocation2 = ModelTemplates.FENCE_SIDE.create(fence, mapping, generator.modelOutput);
		MultiPartGenerator multiPartGenerator = (MultiPartGenerator) BlockModelGenerators.createFence(fence, resourceLocation, resourceLocation2);
		for (int i = 1; i <= 8; i++) {
			multiPartGenerator.with(Condition.condition().term(SnowLayerBlock.LAYERS, i),
					Variant.variant().with(VariantProperties.MODEL, SNOW_LAYERS.get(i)));
		}
		generator.blockStateOutput.accept(multiPartGenerator);
	}

	private static void generateStair(Block stair, Block textureParent, BlockModelGenerators generator) {
		Map<Integer, ResourceLocation> straightModels = new HashMap<>();
		Map<Integer, ResourceLocation> outerModels = new HashMap<>();
		Map<Integer, ResourceLocation> innerModels = new HashMap<>();
		for (int i = 1; i <= 4; i++) {
			straightModels.put(i, STAIRS.get(i).get(StairTypes.STRAIGHT)
					.create(stair, generator.texturedModels.getOrDefault(textureParent, TexturedModel.CUBE.get(textureParent)).getMapping(),
					generator.modelOutput));
			outerModels.put(i, STAIRS.get(i).get(StairTypes.OUTER)
					.create(stair, generator.texturedModels.getOrDefault(textureParent, TexturedModel.CUBE.get(textureParent)).getMapping(),
							generator.modelOutput));
			innerModels.put(i, STAIRS.get(i).get(StairTypes.INNER)
					.create(stair, generator.texturedModels.getOrDefault(textureParent, TexturedModel.CUBE.get(textureParent)).getMapping(),
							generator.modelOutput));
		}
		generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(stair)
				.with(stairPropertyDispatch(straightModels, outerModels, innerModels)));
	}


	//pls kill me
	private static PropertyDispatch.C3<Direction, StairsShape, Integer> stairPropertyDispatch(Map<Integer, ResourceLocation> straightModels, Map<Integer, ResourceLocation> outerModels, Map<Integer, ResourceLocation> innerModels) {
		var propertyDispatch =  PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.STAIRS_SHAPE, SchnowyProperties.HALF_LAYERS);
		for (int snowLayer = 1; snowLayer <= 4; snowLayer++) {
			ResourceLocation regularModelId = straightModels.get(snowLayer);
			ResourceLocation innerModelId = innerModels.get(snowLayer);
			ResourceLocation outerModelId = outerModels.get(snowLayer);
			propertyDispatch.select(Direction.EAST, StairsShape.STRAIGHT, snowLayer,
						Variant.variant().with(VariantProperties.MODEL, regularModelId))
				.select(
						Direction.WEST,
						StairsShape.STRAIGHT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, regularModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.SOUTH,
						StairsShape.STRAIGHT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, regularModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.NORTH,
						StairsShape.STRAIGHT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, regularModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(Direction.EAST, StairsShape.OUTER_RIGHT, snowLayer, Variant.variant().with(VariantProperties.MODEL, outerModelId))
				.select(
						Direction.WEST,
						StairsShape.OUTER_RIGHT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, outerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.SOUTH,
						StairsShape.OUTER_RIGHT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, outerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.NORTH,
						StairsShape.OUTER_RIGHT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, outerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.EAST,
						StairsShape.OUTER_LEFT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, outerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.WEST,
						StairsShape.OUTER_LEFT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, outerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(Direction.SOUTH,  StairsShape.OUTER_LEFT, snowLayer, Variant.variant().with(VariantProperties.MODEL, outerModelId))
				.select(
						Direction.NORTH,
						StairsShape.OUTER_LEFT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, outerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(Direction.EAST, StairsShape.INNER_RIGHT, snowLayer, Variant.variant().with(VariantProperties.MODEL, innerModelId))
				.select(
						Direction.WEST,
						StairsShape.INNER_RIGHT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, innerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.SOUTH,
						StairsShape.INNER_RIGHT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, innerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.NORTH,
						StairsShape.INNER_RIGHT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, innerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.EAST,
						StairsShape.INNER_LEFT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, innerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(
						Direction.WEST,
						StairsShape.INNER_LEFT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, innerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, true)
				)
				.select(Direction.SOUTH,  StairsShape.INNER_LEFT, snowLayer, Variant.variant().with(VariantProperties.MODEL, innerModelId))
				.select(
						Direction.NORTH,
						StairsShape.INNER_LEFT,
						snowLayer,
						Variant.variant()
								.with(VariantProperties.MODEL, innerModelId)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, true)
				);
		}
		return propertyDispatch;
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

	private enum StairTypes {
		STRAIGHT,
		INNER,
		OUTER;

		private String getTemplateSuffix() {
			return switch (this) {
				case INNER -> "_inner";
				case OUTER -> "_outer";
				case STRAIGHT -> "";
			};
		}
	}
}
