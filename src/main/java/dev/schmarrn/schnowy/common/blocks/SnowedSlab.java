package dev.schmarrn.schnowy.common.blocks;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class SnowedSlab extends Block {

	public static final Map<Integer, VoxelShape> shapes = Util.make(() -> {
		Map<Integer, VoxelShape> shapes = new HashMap<>();
		for (int i = 1; i <= 4; i++) {
			shapes.put(i, Block.box(0,0,0,16, 8+2*i, 16));
		}
		return shapes;
	});
	public final Block parent;
	public final Block textureParent;

	public SnowedSlab(Block parent, Block textureParent) {
		super(Properties.copy(parent).strength( Blocks.SNOW.defaultDestroyTime(), Blocks.SNOW.getExplosionResistance()));
		this.parent = parent;
		this.textureParent = textureParent;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SchnowyProperties.HALF_LAYERS);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return shapes.get(state.getValue(SchnowyProperties.HALF_LAYERS));
	}
}
