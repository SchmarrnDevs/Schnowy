package dev.schmarrn.schnowy.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
	@Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
	public void get(CallbackInfoReturnable<String> returnable) {
		List<String> splashes = List.of("Ho Ho Schnow!", "All I want for christmas is ENDEREGG!");
		if (Math.random() > 0.8f) {
			if (Math.random() > 0.5f && Minecraft.getInstance().getUser().getProfileId().equals(new UUID(0xa1cb77e34baf4e1fL, 0xa143306aaa3a8f6cL))) {
				returnable.setReturnValue("Wo ist mein StoneCutter/Smithing Table");
			}
			returnable.setReturnValue(splashes.get(new Random().nextInt(splashes.size())));
		}
	}
}
