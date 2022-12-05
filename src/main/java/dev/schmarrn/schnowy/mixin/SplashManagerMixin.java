package dev.schmarrn.schnowy.mixin;

import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
	@Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
	public void get(CallbackInfoReturnable<String> returnable) {
		if (Math.random() > 0.8) {
			returnable.setReturnValue("Ho Ho Snow");
		}
	}
}
