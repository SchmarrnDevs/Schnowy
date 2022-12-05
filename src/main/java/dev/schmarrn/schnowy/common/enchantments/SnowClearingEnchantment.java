package dev.schmarrn.schnowy.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;

public class SnowClearingEnchantment extends Enchantment {
	protected SnowClearingEnchantment() {
		super(Rarity.UNCOMMON, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof ShovelItem;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
