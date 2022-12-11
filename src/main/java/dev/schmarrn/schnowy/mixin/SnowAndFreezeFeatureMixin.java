package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.ReplaceableBlocks;
import dev.schmarrn.schnowy.common.blocks.SchnowyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SnowAndFreezeFeature.class)
public class SnowAndFreezeFeatureMixin {

	@Redirect(
			method = "place",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/biome/Biome;shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"
			)
	)
	public boolean schnowy$place(Biome instance, LevelReader world, BlockPos blockPos) {
		WorldGenLevel level = (WorldGenLevel) world;
		if (instance.shouldSnow(world, blockPos)) {
			@Nullable BlockState stateWithSnow = ReplaceableBlocks.withSnow(level.getBlockState(blockPos));
			if (stateWithSnow != null) {
				level.setBlock(blockPos, stateWithSnow, 2);
				BlockState belowState = level.getBlockState(blockPos.below());
				if (belowState.hasProperty(SnowyDirtBlock.SNOWY)) {
					level.setBlock(blockPos.below(), belowState.setValue(SnowyDirtBlock.SNOWY, true), 2);
				}
			} else {
				BlockPos below = blockPos.below();
				@Nullable BlockState belowStateWithSnow = ReplaceableBlocks.withSnow(level.getBlockState(below));
				if (belowStateWithSnow != null) {
					level.setBlock(below, belowStateWithSnow, 2);
				} else {
					level.setBlock(blockPos, Blocks.SNOW.defaultBlockState(), 2);
				}
			}
		}
		return false;
	}

}
