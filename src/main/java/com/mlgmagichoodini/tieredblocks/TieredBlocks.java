package com.mlgmagichoodini.tieredblocks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(TieredBlocks.MODID)
public class TieredBlocks {
    public static final String MODID = "tieredblocks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Every tiered block, e.g. "tieredblocks:iron_furnace", "tieredblocks:iron_blast_furnace", "tieredblocks:iron_smoker"
    public static final List<DeferredBlock<TierFurnaceBlock>> ALL_BLOCKS = new ArrayList<>();
    // The block items, grouped by kind and then tier
    public static final Map<FurnaceKind, Map<FurnaceTier, DeferredItem<BlockItem>>> ITEMS_BY_KIND = new EnumMap<>(FurnaceKind.class);
    // One menu type per kind (furnace, blast furnace, smoker)
    public static final Map<FurnaceKind, Supplier<MenuType<TierFurnaceMenu>>> TIER_MENUS = new EnumMap<>(FurnaceKind.class);

    static {
        for (FurnaceKind kind : FurnaceKind.values()) {
            Map<FurnaceTier, DeferredItem<BlockItem>> items = new EnumMap<>(FurnaceTier.class);

            for (FurnaceTier tier : FurnaceTier.values()) {
                String name = tier.getSerializedName() + "_" + kind.getSerializedName();

                DeferredBlock<TierFurnaceBlock> block = BLOCKS.registerBlock(name,
                        properties -> new TierFurnaceBlock(properties, tier, kind),
                        BlockBehaviour.Properties.of()
                                .mapColor(tier.getMapColor())
                                .requiresCorrectToolForDrops()
                                .strength(3.5F, tier.getBlastResistance())
                                .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 13 : 0));

                ALL_BLOCKS.add(block);
                items.put(tier, ITEMS.registerSimpleBlockItem(name, block));
            }
            ITEMS_BY_KIND.put(kind, items);

            Supplier<MenuType<TierFurnaceMenu>> menu = MENU_TYPES.register("tier_" + kind.getSerializedName(),
                    () -> IMenuTypeExtension.create((containerId, inventory, buffer) -> new TierFurnaceMenu(kind, containerId, inventory)));
            TIER_MENUS.put(kind, menu);
        }
    }

    // One block entity type shared by all the tiered furnaces
    public static final Supplier<BlockEntityType<TierFurnaceBlockEntity>> TIER_FURNACE_BE = BLOCK_ENTITY_TYPES.register("tier_furnace",
            () -> BlockEntityType.Builder.of(TierFurnaceBlockEntity::new,
                    ALL_BLOCKS.stream().map(DeferredBlock::get).toArray(Block[]::new)).build(null));

    public static final Supplier<CreativeModeTab> TIERED_BLOCKS_TAB = CREATIVE_MODE_TABS.register("tiered_blocks", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tieredblocks"))
            .icon(() -> ITEMS_BY_KIND.get(FurnaceKind.FURNACE).get(FurnaceTier.IRON).get().getDefaultInstance())
            .displayItems((parameters, output) -> ITEMS_BY_KIND.values().forEach(
                    items -> items.values().forEach(item -> output.accept(item.get()))))
            .build());

    public TieredBlocks(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
