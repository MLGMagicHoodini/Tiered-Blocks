package com.mlgmagichoodini.tieredblocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TierFurnaceBlock extends AbstractFurnaceBlock {
    public static final MapCodec<TierFurnaceBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            FurnaceTier.CODEC.fieldOf("tier").forGetter(TierFurnaceBlock::getTier),
            FurnaceKind.CODEC.fieldOf("kind").forGetter(TierFurnaceBlock::getKind)
    ).apply(instance, TierFurnaceBlock::new));

    private final FurnaceTier tier;
    private final FurnaceKind kind;

    public TierFurnaceBlock(BlockBehaviour.Properties properties, FurnaceTier tier, FurnaceKind kind) {
        super(properties);
        this.tier = tier;
        this.kind = kind;
    }

    public FurnaceTier getTier() {
        return tier;
    }

    public FurnaceKind getKind() {
        return kind;
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TierFurnaceBlockEntity(pos, state);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TierFurnaceBlockEntity) {
            player.openMenu((MenuProvider) blockEntity);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, TieredBlocks.TIER_FURNACE_BE.get(), TierFurnaceBlockEntity::serverTickTier);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }

        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        if (random.nextDouble() < 0.1) {
            SoundEvent sound = switch (kind) {
                case FURNACE -> SoundEvents.FURNACE_FIRE_CRACKLE;
                case BLAST_FURNACE -> SoundEvents.BLASTFURNACE_FIRE_CRACKLE;
                case SMOKER -> SoundEvents.SMOKER_SMOKE;
            };
            level.playLocalSound(x, y, z, sound, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        if (kind == FurnaceKind.SMOKER) {
            // Smoker: smoke rises from the top
            level.addParticle(ParticleTypes.SMOKE, x, y + 1.1, z, 0.0, 0.0, 0.0);
            return;
        }

        // Furnace and blast furnace: flames and smoke from the front
        Direction direction = state.getValue(FACING);
        Direction.Axis axis = direction.getAxis();
        double side = random.nextDouble() * 0.6 - 0.3;
        double offX = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : side;
        double offY = random.nextDouble() * 6.0 / 16.0;
        double offZ = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : side;
        level.addParticle(ParticleTypes.SMOKE, x + offX, y + offY, z + offZ, 0.0, 0.0, 0.0);
        level.addParticle(ParticleTypes.FLAME, x + offX, y + offY, z + offZ, 0.0, 0.0, 0.0);
    }
}
