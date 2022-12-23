package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.SchnowyGameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {
	@Redirect(
			method = "releaseOccupant",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;isRaining()Z"
			)
	)
	private static boolean schnowy$isRainingRedirect(Level instance) {
		if (instance.getGameRules().getBoolean(SchnowyGameRules.RULE_BEE)) {
			return false;
		} else {
			return instance.isRaining();
		}
	}
}
