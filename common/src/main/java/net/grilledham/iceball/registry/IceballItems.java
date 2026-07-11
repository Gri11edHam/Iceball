//~ identifier
package net.grilledham.iceball.registry;

import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.grilledham.iceball.item.BigBouncyBallItem;
import net.grilledham.iceball.item.IceballItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.DispenserBlock;

public class IceballItems {
	
	public static IceballItem ICEBALL_ITEM;
	public static IceballItem PACKED_ICEBALL_ITEM;
	public static IceballItem BLUE_ICEBALL_ITEM;
	public static IceballItem BOOMBALL_ITEM;
	public static IceballItem SPIKEBALL_ITEM;
	public static Item MEATBALL_ITEM;
	public static Item COOKED_MEATBALL_ITEM;
	public static IceballItem BOUNCY_BALL_ITEM;
	public static BigBouncyBallItem BIG_BOUNCY_BALL_ITEM;
	public static IceballItem LIGHTNING_BALL_ITEM;
	public static IceballItem CHARGED_LIGHTNING_BALL_ITEM;
	public static IceballItem FIREBALL_ITEM;
	public static IceballItem FLAMING_FIREBALL_ITEM;
	
	public static void init(ItemRegistry itemRegistry) {
		ICEBALL_ITEM = itemRegistry.register("iceball", new IceballItem.Builder(IceballEntities.ICEBALL_ENTITY)
						.cooldown(0)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
		);
		PACKED_ICEBALL_ITEM = itemRegistry.register("packed_iceball", new IceballItem.Builder(IceballEntities.PACKED_ICEBALL_ENTITY)
						.cooldown(5)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
		);
		BLUE_ICEBALL_ITEM = itemRegistry.register("blue_iceball", new IceballItem.Builder(IceballEntities.BLUE_ICEBALL_ENTITY)
						.cooldown(10)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.RARE)
		);
		BOOMBALL_ITEM = itemRegistry.register("boomball", new IceballItem.Builder(IceballEntities.BOOMBALL_ENTITY)
						.cooldown(20)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)
		);
		SPIKEBALL_ITEM = itemRegistry.register("spikeball", new IceballItem.Builder(IceballEntities.SPIKEBALL_ENTITY)
						.cooldown(20)
						.build(),
				new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(100)
		);
		MEATBALL_ITEM = itemRegistry.register("meatball", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.3f).build()));
		COOKED_MEATBALL_ITEM = itemRegistry.register("cooked_meatball", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(1.0f).build()));
		BOUNCY_BALL_ITEM = itemRegistry.register("bouncy_ball", new IceballItem.Builder(IceballEntities.BOUNCY_BALL_ENTITY)
						.cooldown(0)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
		);
		BIG_BOUNCY_BALL_ITEM = itemRegistry.register("big_bouncy_ball", BigBouncyBallItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
		LIGHTNING_BALL_ITEM = itemRegistry.register("lightning_ball", new IceballItem.Builder(IceballEntities.LIGHTNING_BALL_ENTITY)
						.cooldown(0)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
		);
		CHARGED_LIGHTNING_BALL_ITEM = itemRegistry.register("charged_lightning_ball", new IceballItem.Builder(IceballEntities.CHARGED_LIGHTNING_BALL_ENTITY)
						.cooldown(5)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
		);
		FIREBALL_ITEM = itemRegistry.register("fireball", new IceballItem.Builder(IceballEntities.FIREBALL_ENTITY)
						.cooldown(0)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
		);
		FLAMING_FIREBALL_ITEM = itemRegistry.register("flaming_fireball", new IceballItem.Builder(IceballEntities.FLAMING_FIREBALL_ENTITY)
						.cooldown(5)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON).fireResistant()
		);
		
		DispenserBlock.registerProjectileBehavior(ICEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(PACKED_ICEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(BLUE_ICEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(BOOMBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(SPIKEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(BOUNCY_BALL_ITEM);
		DispenserBlock.registerBehavior(BIG_BOUNCY_BALL_ITEM, new DispenseItemBehavior() {
			@Override
			public ItemStack dispense(BlockSource source, ItemStack dispensed) {
				Direction direction = source.state().getValue(DispenserBlock.FACING);
				BlockPos blockPos = source.pos().relative(direction);
				ServerLevel serverWorld = source.level();
				BigBouncyBallEntity bigBouncyBallEntity = IceballEntities.BIG_BOUNCY_BALL_ENTITY.spawn(serverWorld, EntityType.appendCustomEntityStackConfig(bigBouncyBall -> bigBouncyBall.setYRot(direction.toYRot()), serverWorld, dispensed, null), blockPos, EntitySpawnReason.DISPENSER, false, false);
				if(bigBouncyBallEntity != null) {
					bigBouncyBallEntity.setBallColor(DyedItemColor.getOrDefault(dispensed, 0xFF88DD88));
					dispensed.shrink(1);
				}
				return dispensed;
			}
		});
		DispenserBlock.registerProjectileBehavior(LIGHTNING_BALL_ITEM);
		DispenserBlock.registerProjectileBehavior(CHARGED_LIGHTNING_BALL_ITEM);
		DispenserBlock.registerProjectileBehavior(FIREBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(FLAMING_FIREBALL_ITEM);
	}
	
	public static void initClient(ItemRegistry itemRegistry) {
		itemRegistry.registerClient(ICEBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(Items.SNOWBALL), new ItemRegistry.ItemGroupData(CreativeModeTabs.INGREDIENTS).after(Items.SNOWBALL));
		itemRegistry.registerClient(PACKED_ICEBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(ICEBALL_ITEM), new ItemRegistry.ItemGroupData(CreativeModeTabs.INGREDIENTS).after(ICEBALL_ITEM));
		itemRegistry.registerClient(BLUE_ICEBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(PACKED_ICEBALL_ITEM), new ItemRegistry.ItemGroupData(CreativeModeTabs.INGREDIENTS).after(PACKED_ICEBALL_ITEM));
		itemRegistry.registerClient(BOOMBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(BLUE_ICEBALL_ITEM));
		itemRegistry.registerClient(LIGHTNING_BALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(BOOMBALL_ITEM));
		itemRegistry.registerClient(CHARGED_LIGHTNING_BALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(LIGHTNING_BALL_ITEM));
		itemRegistry.registerClient(FIREBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(CHARGED_LIGHTNING_BALL_ITEM));
		itemRegistry.registerClient(FLAMING_FIREBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(FIREBALL_ITEM));
		itemRegistry.registerClient(SPIKEBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(FLAMING_FIREBALL_ITEM));
		
		itemRegistry.registerClient(MEATBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.FOOD_AND_DRINKS).after(Items.COOKED_RABBIT));
		itemRegistry.registerClient(COOKED_MEATBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.FOOD_AND_DRINKS).after(MEATBALL_ITEM));
		
		itemRegistry.registerClient(BOUNCY_BALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(SPIKEBALL_ITEM));
		itemRegistry.registerClient(BIG_BOUNCY_BALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.TOOLS_AND_UTILITIES).after(Items.BAMBOO_CHEST_RAFT));
	}
}
