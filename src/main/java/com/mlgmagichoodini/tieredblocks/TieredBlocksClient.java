package com.mlgmagichoodini.tieredblocks;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

// Client only code (never loads on a dedicated server)
@EventBusSubscriber(modid = TieredBlocks.MODID, value = Dist.CLIENT)
public class TieredBlocksClient {
    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(TieredBlocks.TIER_MENUS.get(FurnaceKind.FURNACE).get(), TierFurnaceScreen::new);
        event.register(TieredBlocks.TIER_MENUS.get(FurnaceKind.BLAST_FURNACE).get(), TierBlastFurnaceScreen::new);
        event.register(TieredBlocks.TIER_MENUS.get(FurnaceKind.SMOKER).get(), TierSmokerScreen::new);
    }
}
