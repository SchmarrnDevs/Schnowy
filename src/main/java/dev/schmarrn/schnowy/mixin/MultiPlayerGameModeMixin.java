package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.SnowLayerInteractionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Redirect(
			method = "destroyBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;destroy(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
			)
	)
	public void schnowy$clientFlickerFix(Block instance, LevelAccessor world, BlockPos pos, BlockState state) {
		if (SnowLayerInteractionEvents.getInstance().beforeBlockBreak(minecraft.level, minecraft.player, pos, state, null)) {
			instance.destroy(world, pos, state);
		}
	}
}
