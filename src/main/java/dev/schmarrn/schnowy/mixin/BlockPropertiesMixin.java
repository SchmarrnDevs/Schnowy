package dev.schmarrn.schnowy.mixin;

import dev.schmarrn.schnowy.common.duck.BlockPropertiesDuck;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockBehaviour.Properties.class)
public class BlockPropertiesMixin implements BlockPropertiesDuck {
	@Shadow
	boolean replaceable;

	@Override
	public BlockBehaviour.Properties nonReplaceable() {
		this.replaceable = false;
		return (BlockBehaviour.Properties) (Object) this;
	}
}
