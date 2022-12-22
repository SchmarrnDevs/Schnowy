package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.SchnowyGameRules;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Bee.class)
public abstract class BeeMixin {
	@Redirect(
			method = "wantsToEnterHive",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;isRaining()Z"
			)
	)
	public boolean schnowy$isRainingRedirect(Level instance) {
		if (instance.getGameRules().getBoolean(SchnowyGameRules.RULE_BEE)) {
			return false;
		} else {
			return instance.isRaining();
		}
	}
}
