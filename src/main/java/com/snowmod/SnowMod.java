package com.snowmod;

import com.snowmod.collector.FeatureCollector;
import com.snowmod.command.SnowCommand;
import com.snowmod.gui.SnowHudOverlay;
import com.snowmod.render.WorldRenderESP;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowMod implements ClientModInitializer {
    public static final String MOD_ID = "snowmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Snow Mod] Initializing SeedcrackerX engine & 3D Render ESP...");

        // Register HUD Overlay
        HudRenderCallback.EVENT.register(new SnowHudOverlay());

        // Register 3D World Render ESP
        WorldRenderEvents.AFTER_TRANSLUCENT.register(WorldRenderESP::render);

        // Register World Feature Scanner on Client Tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FeatureCollector.getInstance().onClientTick(client);
        });

        // Register /snow & /seed Client Commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            SnowCommand.register(dispatcher, registryAccess);
        });

        LOGGER.info("[Snow Mod] Initialized successfully.");
    }
}
