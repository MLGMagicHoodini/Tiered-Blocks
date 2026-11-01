package com.mlgmagichoodini.tieredblocks;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.MapColor;

/**
 * All the tier numbers for the tiered furnaces live here.
 * To rebalance a tier, change the numbers in this file only.
 *
 * speed    = smelting speed multiplier (tool mining speed / 4, where stone = 4 = vanilla = 1x)
 * fuelUse  = fuel burned per item compared to vanilla (1.0 = same, 1.5 = burns 50% more)
 * blastResistance = explosion resistance of the block (vanilla furnace = 3.5, obsidian = 1200)
 */
public enum FurnaceTier implements StringRepresentable {
    COPPER("copper", 1.25f, 1.0f, 3.5f, MapColor.COLOR_ORANGE),
    IRON("iron", 1.5f, 1.0f, 3.5f, MapColor.METAL),
    GOLD("gold", 3.0f, 1.5f, 3.5f, MapColor.GOLD),
    EMERALD("emerald", 1.75f, 1.0f, 3.5f, MapColor.EMERALD),
    DIAMOND("diamond", 2.0f, 1.0f, 3.5f, MapColor.DIAMOND),
    OBSIDIAN("obsidian", 2.0f, 1.0f, 1200.0f, MapColor.COLOR_BLACK),
    NETHERITE("netherite", 2.25f, 1.0f, 1200.0f, MapColor.COLOR_BLACK);

    public static final Codec<FurnaceTier> CODEC = StringRepresentable.fromEnum(FurnaceTier::values);

    private final String id;
    private final float speed;
    private final float fuelUse;
    private final float blastResistance;
    private final MapColor mapColor;

    FurnaceTier(String id, float speed, float fuelUse, float blastResistance, MapColor mapColor) {
        this.id = id;
        this.speed = speed;
        this.fuelUse = fuelUse;
        this.blastResistance = blastResistance;
        this.mapColor = mapColor;
    }

    public float getSpeed() {
        return speed;
    }

    public float getFuelUse() {
        return fuelUse;
    }

    public float getBlastResistance() {
        return blastResistance;
    }

    public MapColor getMapColor() {
        return mapColor;
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
