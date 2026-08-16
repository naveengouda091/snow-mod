package com.snowmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.snowmod.collector.FeatureCollector;
import com.snowmod.config.SnowConfig;
import com.snowmod.cracker.SeedCrackerEngine;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class SnowCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("snow")
            .executes(ctx -> {
                int features = FeatureCollector.getInstance().getCollectedFeatures().size();
                String status = SeedCrackerEngine.getInstance().getStatusMessage();
                String state = SnowConfig.enabled ? "§aENABLED" : "§cDISABLED";
                ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §fStatus: " + state + " §f| Features: §e" + features + " §f| Engine: §a" + status));
                ctx.getSource().sendFeedback(Text.literal("§7Commands: /snow toggle §8| §7/snow hud §8| §7/snow esp §8| §7/snow crack §8| §7/snow seed §8| §7/snow clear"));
                return 1;
            })
            .then(ClientCommandManager.literal("toggle").executes(ctx -> {
                SnowConfig.enabled = !SnowConfig.enabled;
                String state = SnowConfig.enabled ? "§aENABLED" : "§cDISABLED";
                ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §fMod toggled " + state));
                return 1;
            }))
            .then(ClientCommandManager.literal("hud").executes(ctx -> {
                SnowConfig.renderHud = !SnowConfig.renderHud;
                String state = SnowConfig.renderHud ? "§aENABLED" : "§cDISABLED";
                ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §fHUD Overlay toggled " + state));
                return 1;
            }))
            .then(ClientCommandManager.literal("esp").executes(ctx -> {
                SnowConfig.renderEsp = !SnowConfig.renderEsp;
                String state = SnowConfig.renderEsp ? "§aENABLED" : "§cDISABLED";
                ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §f3D World ESP toggled " + state));
                return 1;
            }))
            .then(ClientCommandManager.literal("status").executes(ctx -> {
                int features = FeatureCollector.getInstance().getCollectedFeatures().size();
                String status = SeedCrackerEngine.getInstance().getStatusMessage();
                ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §fFeatures Collected: §e" + features + " §f| Status: §a" + status));
                return 1;
            }))
            .then(ClientCommandManager.literal("crack").executes(ctx -> {
                int features = FeatureCollector.getInstance().getCollectedFeatures().size();
                if (features == 0) {
                    ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §cCannot crack: No structure features collected yet! Explore near Villages, Shipwrecks, or Temples first."));
                    return 0;
                }
                SeedCrackerEngine.getInstance().startCracking();
                ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §aSeed cracking task initiated in background..."));
                return 1;
            }))
            .then(ClientCommandManager.literal("clear").executes(ctx -> {
                FeatureCollector.getInstance().clear();
                SeedCrackerEngine.getInstance().reset();
                ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §cCollected feature cache & solver state cleared."));
                return 1;
            }))
            .then(ClientCommandManager.literal("seed").executes(ctx -> {
                Long seed = SeedCrackerEngine.getInstance().getFoundSeed();
                if (seed != null) {
                    MinecraftClient.getInstance().keyboard.setClipboard(String.valueOf(seed));
                    ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §aFound World Seed: §e" + seed + " §7(Copied to clipboard!)"));
                } else {
                    ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §cNo seed solved yet. Gather features and run /snow crack"));
                }
                return 1;
            }))
        );
    }
}
