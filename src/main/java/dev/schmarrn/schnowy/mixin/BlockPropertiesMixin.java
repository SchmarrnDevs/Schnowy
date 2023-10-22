package dev.schmarrn.schnowy.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.Properties.class)
public interface BlockPropertiesMixin {
	@Accessor("replaceable")
	void setReplaceable(boolean bla);
}
