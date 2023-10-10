package dev.schmarrn.schnowy.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnowedStair extends StairBlock {

	public final Block textureParent;
	public SnowedStair(Block parent, Block textureParent) {
		super(
				textureParent.defaultBlockState(),
				BlockBehaviour.Properties.copy(parent)
						.requiresCorrectToolForDrops()
						.strength(Blocks.SNOW.defaultDestroyTime(), Blocks.SNOW.getExplosionResistance())
						.sound(SoundType.SNOW)
						.noOcclusion()
		);
		this.textureParent = textureParent;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SchnowyProperties.HALF_LAYERS);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.or(super.getShape(state, world, pos, context), box(0, 8, 0, 16, 8 + 2*state.getValue(SchnowyProperties.HALF_LAYERS), 16));
	}
}
