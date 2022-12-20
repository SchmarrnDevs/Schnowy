package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.enchantments.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

	@Inject(
			method = "getAvailableEnchantmentResults",
			at = @At(
					value = "RETURN"
			)
	)
	private static void schnowy$snowClearingEnchantmentFixer(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
		List<EnchantmentInstance> returnValue = cir.getReturnValue();
		for (EnchantmentInstance instance: List.copyOf(returnValue)) {
			if (instance.enchantment == Enchantments.SNOW_CLEARING && !instance.enchantment.canEnchant(stack)) {
				returnValue.remove(instance);
			}
		}
	}

}
