package net.grilledham.iceball.mixin;

import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningRodBlock.class)
public abstract class LightningRodBlockMixin extends RodBlock implements SimpleWaterloggedBlock {
	
	protected LightningRodBlockMixin(BlockBehaviour.Properties settings) {
		super(settings);
	}
	
	@Inject(method = "updateNeighbours", at = @At("TAIL"))
	private void updateChestContents(BlockState state, Level world, BlockPos pos, CallbackInfo ci) {
		BlockPos updatePos = pos.relative(state.getValue(FACING).getOpposite());
		Container inv = getBlockInventoryAt(world, updatePos, world.getBlockState(updatePos));
		if(inv != null) {
			for(int i = 0; i < inv.getContainerSize(); i++) {
				ItemStack stack = inv.getItem(i);
				if(stack.getItem() == ItemRegistry.LIGHTNING_BALL_ITEM) {
					inv.setItem(i, stack.transmuteCopy(ItemRegistry.CHARGED_LIGHTNING_BALL_ITEM));
				}
			}
		}
	}
	
	@Unique
	private static Container getBlockInventoryAt(Level world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity;
		Block block = state.getBlock();
		if (state.hasBlockEntity() && (blockEntity = world.getBlockEntity(pos)) instanceof Container) {
			Container inventory = (Container)blockEntity;
			if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
				inventory = ChestBlock.getContainer((ChestBlock)block, state, world, pos, true);
			}
			return inventory;
		}
		return null;
	}
}
