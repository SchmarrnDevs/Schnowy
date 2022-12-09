package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.SchnowyEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
	@Redirect(
			method="tickChunk",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
					ordinal = 1
			)
	)
	public int schnowy$disableNormalSnow(RandomSource instance, int i) {
		return 1;
	}

	@Inject(
		method = "tickChunk",
		at = @At(
				value = "INVOKE_STRING",
				target ="Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
				args = { "ldc=iceandsnow" },
				shift = At.Shift.AFTER
		)
	)
	public void schnowy$customSnowEngine(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		SchnowyEngine.tickChunk(asThis(), chunk, randomTickSpeed);
	}

	public ServerLevel asThis() {
		return (ServerLevel) (Object) this;
	}
}
