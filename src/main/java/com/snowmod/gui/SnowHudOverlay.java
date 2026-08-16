package com.snowmod.gui;

import com.snowmod.finder.BitTracker;
import com.snowmod.cracker.SeedCrackerEngine;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class SnowHudOverlay implements HudRenderCallback {
    private static final int COLOR_TITLE = 0xFF88CCFF;  // Ice Blue
    private static final int COLOR_TEXT = 0xFFEEEEEE;   // White
    private static final int COLOR_SEED = 0xFF55FF55;   // Light Green
    private static final int COLOR_BITS = 0xFFFFAA00;   // Gold/Orange
    private static final int COLOR_BG = 0x99000000;     // Dark semi-transparent black

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden || client.player == null) return;

        SeedCrackerEngine engine = SeedCrackerEngine.getInstance();
        BitTracker tracker = BitTracker.getInstance();

        int totalBits = tracker.getTotalBits();
        int structBits = tracker.getStructureBits();
        int worldBits = tracker.getWorldBits();
        int finders = tracker.getActiveFinders().size();

        int x = 10;
        int y = 10;

        drawContext.fill(x - 4, y - 4, x + 230, y + 66, COLOR_BG);

        drawContext.drawText(client.textRenderer, "❆ Snow Mod (SeedcrackerX Engine) ❆", x, y, COLOR_TITLE, true);
        drawContext.drawText(client.textRenderer, "Structure Bits: " + structBits + " / 48", x, y + 12, COLOR_BITS, true);
        drawContext.drawText(client.textRenderer, "World Bits: " + worldBits + " / 16 (Total: " + totalBits + ")", x, y + 24, COLOR_TEXT, true);

        if (engine.isRunning()) {
            drawContext.drawText(client.textRenderer, "Auto-Crack: Active (" + engine.getProgressPercent() + "%)", x, y + 36, COLOR_TITLE, true);
        } else {
            drawContext.drawText(client.textRenderer, "Status: " + engine.getStatusMessage(), x, y + 36, COLOR_TEXT, true);
        }

        if (engine.getFoundSeed() != null) {
            drawContext.drawText(client.textRenderer, "Seed: " + engine.getFoundSeed(), x, y + 48, COLOR_SEED, true);
        } else {
            drawContext.drawText(client.textRenderer, "Finders Active: " + finders + " (Auto-cracks at 48 bits)", x, y + 48, 0xFFAAAAAA, true);
        }
    }
}
