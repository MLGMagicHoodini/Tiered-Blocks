package com.mlgmagichoodini.tieredblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * One block entity for every tiered furnace, blast furnace and smoker. It behaves like the vanilla
 * version of its kind, but runs the vanilla smelting logic several times per game tick depending
 * on its tier. Since the vanilla logic burns one fuel tick for every cooking tick, a faster furnace
 * uses the same fuel per item as a vanilla one. Tiers with a fuelUse above 1.0 (gold) burn extra.
 */
public class TierFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    // Index 0 of the furnace's data is the remaining burn time (litTime)
    private static final int LIT_TIME = 0;

    // Leftover fractions, so multipliers like 1.25x work over time
    private float speedProgress = 0.0f;
    private float fuelDebt = 0.0f;

    public TierFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(TieredBlocks.TIER_FURNACE_BE.get(), pos, state, recipeTypeOf(state));
    }

    private static RecipeType<? extends AbstractCookingRecipe> recipeTypeOf(BlockState state) {
        if (state.getBlock() instanceof TierFurnaceBlock block) {
            return block.getKind().getRecipeType();
        }
        return RecipeType.SMELTING;
    }

    private FurnaceTier getTier() {
        if (this.getBlockState().getBlock() instanceof TierFurnaceBlock block) {
            return block.getTier();
        }
        return FurnaceTier.IRON;
    }

    private FurnaceKind getKind() {
        if (this.getBlockState().getBlock() instanceof TierFurnaceBlock block) {
            return block.getKind();
        }
        return FurnaceKind.FURNACE;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new TierFurnaceMenu(getKind(), containerId, inventory, this, this.dataAccess);
    }

    // Same as vanilla: blast furnaces and smokers burn fuel for half as long
    @Override
    protected int getBurnDuration(ItemStack fuel) {
        return super.getBurnDuration(fuel) / getKind().getBurnDivisor();
    }

    public static void serverTickTier(Level level, BlockPos pos, BlockState state, TierFurnaceBlockEntity furnace) {
        FurnaceTier tier = furnace.getTier();

        furnace.speedProgress += tier.getSpeed();
        int steps = (int) furnace.speedProgress;
        furnace.speedProgress -= steps;

        for (int i = 0; i < steps; i++) {
            BlockState current = level.getBlockState(pos);
            if (!(current.getBlock() instanceof TierFurnaceBlock)) {
                return;
            }
            AbstractFurnaceBlockEntity.serverTick(level, pos, current, furnace);
            furnace.applyExtraFuelUse(tier);
        }
    }

    private void applyExtraFuelUse(FurnaceTier tier) {
        float extra = tier.getFuelUse() - 1.0f;
        if (extra <= 0.0f || this.dataAccess.get(LIT_TIME) <= 1) {
            return;
        }

        this.fuelDebt += extra;
        // Stop at 1 so vanilla runs out of fuel by itself and updates the lit state correctly
        while (this.fuelDebt >= 1.0f && this.dataAccess.get(LIT_TIME) > 1) {
            this.dataAccess.set(LIT_TIME, this.dataAccess.get(LIT_TIME) - 1);
            this.fuelDebt -= 1.0f;
        }
    }
}
