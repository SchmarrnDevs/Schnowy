package dev.schmarrn.schnowy.mixin;

import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {
	@ModifyVariable(
		method = "<init>",
		at = @At(
			value = "HEAD"
		),
		ordinal = 0,
		argsOnly = true
	)
	private static BlockBehaviour.Properties schnowy$FencePropertiesModify(BlockBehaviour.Properties properties) {
		return properties.noOcclusion();
	}
}
