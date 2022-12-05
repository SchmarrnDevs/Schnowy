package dev.schmarrn.schnowy.common.enchantments;

import dev.schmarrn.schnowy.Schnowy;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class Enchantments {

	public static final Enchantment  SNOW_CLEARING = new SnowClearingEnchantment();

	public static void init() {
		Registry .register(Registry.ENCHANTMENT, new ResourceLocation(Schnowy.MODID, "snow_clearing"), SNOW_CLEARING);
	}
}
