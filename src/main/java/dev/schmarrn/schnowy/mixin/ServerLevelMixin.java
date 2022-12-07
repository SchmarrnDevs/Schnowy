package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.SchnowyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
	@ModifyArgs(
			method = "tickChunk",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
					ordinal = 1 // the second call to this function
			)
	)
	public void schnowy$tickChunk(Args args) {
		ServerLevel world = (ServerLevel) (Object) this;
		BlockPos pos = args.get(0);
		BlockState state = world.getBlockState(pos);

		SchnowyUtils.SnowPlacementInfo info = SchnowyUtils.getNewSnow(world, pos, state);

		args.set(0, info.pos());
		args.set(1, info.state());
	}
}
