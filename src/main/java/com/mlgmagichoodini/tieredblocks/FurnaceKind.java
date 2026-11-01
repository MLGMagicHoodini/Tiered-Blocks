package com.mlgmagichoodini.tieredblocks;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * The three kinds of furnace the mod makes tiers of. Each kind only differs in which recipes
 * it uses. The tier numbers (FurnaceTier) are applied on top of whatever the vanilla block does,
 * so a blast furnace is already 2x faster than a furnace before the tier bonus.
 */
public enum FurnaceKind implements StringRepresentable {
    FURNACE("furnace", RecipeType.SMELTING, RecipeBookType.FURNACE, 1),
    BLAST_FURNACE("blast_furnace", RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE, 2),
    SMOKER("smoker", RecipeType.SMOKING, RecipeBookType.SMOKER, 2);

    public static final Codec<FurnaceKind> CODEC = StringRepresentable.fromEnum(FurnaceKind::values);

    private final String id;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeBookType recipeBookType;
    // Vanilla blast furnaces and smokers burn fuel twice as fast, to match their 2x cooking speed
    private final int burnDivisor;

    FurnaceKind(String id, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType recipeBookType, int burnDivisor) {
        this.id = id;
        this.recipeType = recipeType;
        this.recipeBookType = recipeBookType;
        this.burnDivisor = burnDivisor;
    }

    public RecipeType<? extends AbstractCookingRecipe> getRecipeType() {
        return recipeType;
    }

    public RecipeBookType getRecipeBookType() {
        return recipeBookType;
    }

    public int getBurnDivisor() {
        return burnDivisor;
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
