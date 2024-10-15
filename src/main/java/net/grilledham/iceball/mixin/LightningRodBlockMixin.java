package net.grilledham.iceball.mixin;

import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningRodBlock.class)
public abstract class LightningRodBlockMixin extends RodBlock implements Waterloggable {
	
	protected LightningRodBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Inject(method = "updateNeighbors", at = @At("TAIL"))
	private void updateChestContents(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
		BlockPos updatePos = pos.offset(state.get(FACING).getOpposite());
		Inventory inv = getBlockInventoryAt(world, updatePos, world.getBlockState(updatePos));
		if(inv != null) {
			for(int i = 0; i < inv.size(); i++) {
				ItemStack stack = inv.getStack(i);
				if(stack.getItem() == ItemRegistry.LIGHTNING_BALL_ITEM) {
					inv.setStack(i, stack.withItem(ItemRegistry.CHARGED_LIGHTNING_BALL_ITEM));
				}
			}
		}
	}
	
	@Unique
	private static Inventory getBlockInventoryAt(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity;
		Block block = state.getBlock();
		if (block instanceof InventoryProvider) {
			return ((InventoryProvider)block).getInventory(state, world, pos);
		}
		if (state.hasBlockEntity() && (blockEntity = world.getBlockEntity(pos)) instanceof Inventory) {
			Inventory inventory = (Inventory)blockEntity;
			if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
				inventory = ChestBlock.getInventory((ChestBlock)block, state, world, pos, true);
			}
			return inventory;
		}
		return null;
	}
}
