package dev.schmarrn.schnowy.common.enchantments;

import dev.schmarrn.schnowy.Schnowy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Enchantments {

	public static final Enchantment SNOW_CLEARING = new SnowClearingEnchantment();

	public static void init() {
		Registry.register(Registry.ENCHANTMENT, new Identifier(Schnowy.MODID, "snow_clearing"), SNOW_CLEARING);
	}
}
