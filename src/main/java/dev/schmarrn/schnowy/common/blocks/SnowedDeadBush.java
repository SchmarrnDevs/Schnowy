package dev.schmarrn.schnowy.common.blocks;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class SnowedDeadBush extends DeadBushBlock {
	public static final Map<Integer, VoxelShape> shapes = Util.make(() -> {
		Map<Integer, VoxelShape> shapes = new HashMap<>();
		for (int i = 1; i <= 8; i++) {
			shapes.put(i, Block.box(0,0,0,16, 2*(i-1), 16));
		}
		return shapes;
	});

	public static final Map<Integer, VoxelShape> interaction_shapes = Util.make(() -> {
		Map<Integer, VoxelShape> shapes = new HashMap<>();
		for (int i = 1; i <= 8; i++) {
			shapes.put(i, Shapes.or(DeadBushBlock.SHAPE, Block.box(0,0,0,16, 2*i, 16)));
		}
		return shapes;
	});

	public SnowedDeadBush() {
		super(BlockBehaviour.Properties.of(Material.SNOW).requiresCorrectToolForDrops().strength(0.2f).sound(SoundType.SNOW));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.LAYERS);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return shapes.get(state.getValue(BlockStateProperties.LAYERS));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return interaction_shapes.get(state.getValue(BlockStateProperties.LAYERS));
	}

	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return state.getValue(BlockStateProperties.LAYERS) == 8 ? 0.2F : 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return true;
	}

	@Override
	protected void spawnDestroyParticles(Level world, Player player, BlockPos pos, BlockState state) {
		super.spawnDestroyParticles(world, player, pos, Blocks.SNOW.defaultBlockState());
	}
}
