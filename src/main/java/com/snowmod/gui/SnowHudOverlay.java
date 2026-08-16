package com.snowmod.gui;

import com.snowmod.collector.FeatureCollector;
import com.snowmod.cracker.SeedCrackerEngine;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class SnowHudOverlay implements HudRenderCallback {
    private static final int COLOR_TITLE = 0xFF88CCFF;  // Ice Blue
    private static final int COLOR_TEXT = 0xFFEEEEEE;   // White
    private static final int COLOR_SEED = 0xFF55FF55;   // Light Green
    private static final int COLOR_BG = 0x88000000;     // Semi-transparent black

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden || client.player == null) return;

        SeedCrackerEngine engine = SeedCrackerEngine.getInstance();
        int featureCount = FeatureCollector.getInstance().getCollectedFeatures().size();

        int x = 10;
        int y = 10;

        drawContext.fill(x - 4, y - 4, x + 210, y + 54, COLOR_BG);

        drawContext.drawText(client.textRenderer, "❆ Snow Mod - Seed Engine ❆", x, y, COLOR_TITLE, true);
        drawContext.drawText(client.textRenderer, "Features Collected: " + featureCount, x, y + 12, COLOR_TEXT, true);

        if (engine.isRunning()) {
            drawContext.drawText(client.textRenderer, "Status: Cracking (" + engine.getProgressPercent() + "%)", x, y + 24, COLOR_TITLE, true);
        } else {
            drawContext.drawText(client.textRenderer, "Status: " + engine.getStatusMessage(), x, y + 24, COLOR_TEXT, true);
        }

        if (engine.getFoundSeed() != null) {
            drawContext.drawText(client.textRenderer, "Seed: " + engine.getFoundSeed(), x, y + 36, COLOR_SEED, true);
        } else {
            drawContext.drawText(client.textRenderer, "Run /snow crack to solve", x, y + 36, 0xFFAAAAAA, true);
        }
    }
}
