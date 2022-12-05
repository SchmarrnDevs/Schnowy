package dev.schmarrn.schnowy.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SnowClearingEnchantment extends Enchantment  {
	protected SnowClearingEnchantment() {
		super(Rarity.UNCOMMON, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof ShovelItem ;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}
}
