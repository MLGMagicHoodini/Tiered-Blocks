package com.mlgmagichoodini.tieredblocks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;

public class TierFurnaceMenu extends AbstractFurnaceMenu {
    // Client side constructor
    public TierFurnaceMenu(FurnaceKind kind, int containerId, Inventory inventory) {
        super(TieredBlocks.TIER_MENUS.get(kind).get(), kind.getRecipeType(), kind.getRecipeBookType(), containerId, inventory);
    }

    // Server side constructor
    public TierFurnaceMenu(FurnaceKind kind, int containerId, Inventory inventory, Container container, ContainerData data) {
        super(TieredBlocks.TIER_MENUS.get(kind).get(), kind.getRecipeType(), kind.getRecipeBookType(), containerId, inventory, container, data);
    }
}
