package dev.schmarrn.schnowy.common.blocks;

import dev.schmarrn.schnowy.Schnowy;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;

import java.util.HashMap;
import java.util.Map;

public class SchnowyBlocks {

	public static final Map<Block, SnowedSlab> SLABS = new HashMap<>();
	public static final Map<Block, SnowedFence> FENCES = new HashMap<>();
	public static final Map<Block, SnowedStair> STAIRS = new HashMap<>();
	public static final Map<FlowerBlock, SnowedFlower> FLOWERS = new HashMap<>();
	public static final Map<TallGrassBlock, SnowedGrass> SNOWED_GRASS = new HashMap<>();
	public static final Block SNOWED_DEAD_BUSH = new SnowedDeadBush();

	private static final Map<Block, Block> textureRedirects = Util.make(() -> {
		Map<Block, Block> map = new HashMap<>();
		map.put(Blocks.WAXED_CUT_COPPER, Blocks.CUT_COPPER);
		map.put(Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.EXPOSED_CUT_COPPER);
		map.put(Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER);
		map.put(Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER);
		return map;
	});
	public static void init() {
		Registry.BLOCK.stream()
				.filter(FlowerBlock.class::isInstance)
				.map(FlowerBlock.class::cast)
				.filter(block -> Registry.BLOCK.getKey(block).getNamespace().equals("minecraft")).forEach(SchnowyBlocks::createFlower);

		BlockFamilies.getAllFamilies().forEach(family -> {
			Block slab = family.get(BlockFamily.Variant.SLAB);
			if (slab != null) {
				createSlab(slab, family.getBaseBlock());
			}
		});

		BlockFamilies.getAllFamilies().forEach(blockFamily -> {
			Block fence = blockFamily.get(BlockFamily.Variant.FENCE);
			if (fence != null) {
				createFence(fence, blockFamily.getBaseBlock());
			}
		});
		BlockFamilies.getAllFamilies().forEach(blockFamily -> {
			Block stair = blockFamily.get(BlockFamily.Variant.STAIRS);
			if (stair != null) {
				createStair(stair, blockFamily.getBaseBlock());
			}
		});

		createGrass((TallGrassBlock) Blocks.GRASS);
		createGrass((TallGrassBlock) Blocks.FERN);
		Registry.register(Registry.BLOCK, new ResourceLocation(Schnowy.MODID, "snowed_dead_bush"), SNOWED_DEAD_BUSH);
	}

	public static void createGrass(TallGrassBlock parent) {
		SnowedGrass snowed = new SnowedGrass(parent);
		SNOWED_GRASS.put(parent, snowed);
		Registry.register(Registry.BLOCK, new ResourceLocation(Schnowy.MODID, "snowed_" + Registry.BLOCK.getKey(parent).getPath()), snowed);
	}

	public static void createFlower(FlowerBlock flowerReplacement) {
		SnowedFlower flower = new SnowedFlower(flowerReplacement);
		FLOWERS.put(flowerReplacement, flower);
		Registry.register(Registry.BLOCK, new ResourceLocation(Schnowy.MODID, "snowed_" + Registry.BLOCK.getKey(flowerReplacement).getPath()), flower);
	}

	public static void createSlab(Block slabReplacement, Block fullBlock) {
		SnowedSlab slab = new SnowedSlab(fullBlock, textureRedirects.getOrDefault(fullBlock, fullBlock));
		SLABS.put(slabReplacement, slab);
		Registry.register(Registry.BLOCK, new ResourceLocation(Schnowy.MODID, "snowed_" + Registry.BLOCK.getKey(slabReplacement).getPath()), slab);
	}
	public static void createStair(Block stair, Block fullBlock) {
		SnowedStair snowedStair = new SnowedStair(textureRedirects.getOrDefault(fullBlock, fullBlock));
		STAIRS.put(stair, snowedStair);
		Registry.register(Registry.BLOCK, new ResourceLocation(Schnowy.MODID, "snowed_" + Registry.BLOCK.getKey(stair).getPath()), snowedStair);
	}

	public static void createFence(Block fence, Block textureParent) {
		SnowedFence slab = new SnowedFence(textureParent);
		FENCES.put(fence, slab);
		Registry.register(Registry.BLOCK, new ResourceLocation(Schnowy.MODID, "snowed_" + Registry.BLOCK.getKey(fence).getPath()), slab);
	}
}
