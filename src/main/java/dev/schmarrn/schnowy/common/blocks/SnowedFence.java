package dev.schmarrn.schnowy.common.blocks;

import dev.schmarrn.schnowy.common.FallingSnowUtil;
import dev.schmarrn.schnowy.common.ReplaceableBlocks;
import dev.schmarrn.schnowy.common.SchnowyEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnowedFence extends FenceBlock implements SchnowyBlockInterface {
	public final Block parent;

	public SnowedFence(Block parent) {
		super(BlockBehaviour.Properties.copy(parent)
				.requiresCorrectToolForDrops()
				.strength(Blocks.SNOW.defaultDestroyTime(), Blocks.SNOW.getExplosionResistance())
				.sound(SoundType.SNOW)
				.noOcclusion()
		);
		this.parent = parent;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.LAYERS);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		int i = state.getValue(BlockStateProperties.LAYERS);
		return Shapes.or(super.getCollisionShape(state, world, pos, context), Block.box(0,0,0,16, 2*i, 16));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		int i = state.getValue(BlockStateProperties.LAYERS);
		return Shapes.or(super.getShape(state, world, pos, context), Block.box(0,0,0,16, 2*i, 16));
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return state.getValue(BlockStateProperties.LAYERS) == 8 ? 0.2F : 1.0F;
	}

	@Override
	protected void spawnDestroyParticles(Level world, Player player, BlockPos pos, BlockState state) {
		super.spawnDestroyParticles(world, player, pos, Blocks.SNOW.defaultBlockState());
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		BlockState fallen = FallingSnowUtil.updateShape(state, direction, neighborState, world, pos, neighborPos, parent);

		if (fallen == null) {
			return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
		} else {
			return fallen;
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.below());
		return SchnowyEngine.isFullSnowLogged(blockState) || Blocks.SNOW.canSurvive(state, world, pos);
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canLog(LevelReader level, BlockPos pos) {
		return Blocks.SNOW.defaultBlockState().canSurvive(level, pos);
	}
}
