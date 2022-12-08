package dev.schmarrn.schnowy.common.blocks;

import dev.schmarrn.schnowy.Schnowy;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;

import java.util.HashMap;
import java.util.Map;

public class SchnowyBlocks {

	public static final Map<Block, SnowedSlab> SLABS = new HashMap<>();
	public static final Map<FlowerBlock, SnowedFlower> FLOWERS = new HashMap<>();
	public static final Block SNOWED_GRASS = new SnowedGrass();

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

		Registry.register(Registry.BLOCK, new ResourceLocation(Schnowy.MODID, "snowed_grass"), SNOWED_GRASS);
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
}
