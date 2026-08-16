package com.snowmod;

import com.snowmod.collector.FeatureCollector;
import com.snowmod.command.SnowCommand;
import com.snowmod.config.SnowConfig;
import com.snowmod.gui.SnowHudOverlay;
import com.snowmod.render.WorldRenderESP;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowMod implements ClientModInitializer {
    public static final String MOD_ID = "snowmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static KeyBinding toggleKeyBinding;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Snow Mod] Initializing SeedcrackerX engine, Keybinding Toggles & 3D Render ESP...");

        // Register Toggle KeyBinding (Default: 'O' key)
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.snowmod.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.snowmod.title"
        ));

        // Register HUD Overlay
        HudRenderCallback.EVENT.register(new SnowHudOverlay());

        // Register 3D World Render ESP
        WorldRenderEvents.AFTER_TRANSLUCENT.register(WorldRenderESP::render);

        // Register World Feature Scanner and KeyPress on Client Tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKeyBinding.wasPressed()) {
                SnowConfig.enabled = !SnowConfig.enabled;
                if (client.player != null) {
                    String state = SnowConfig.enabled ? "§aENABLED" : "§cDISABLED";
                    client.player.sendMessage(Text.literal("§b[SnowMod] §fMod toggled " + state), true);
                }
            }

            FeatureCollector.getInstance().onClientTick(client);
        });

        // Register /snow & /seed Client Commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            SnowCommand.register(dispatcher, registryAccess);
        });

        LOGGER.info("[Snow Mod] Initialized successfully.");
    }
}
