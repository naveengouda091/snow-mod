package com.snowmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.snowmod.collector.FeatureCollector;
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
                ctx.getSource().sendFeedback(Text.literal("§b[SnowMod] §fFeatures Collected: §e" + features + " §f| Engine Status: §a" + status));
                ctx.getSource().sendFeedback(Text.literal("§7Commands: /snow crack §8| §7/snow seed §8| §7/snow clear §8| §7/snow status"));
                return 1;
            })
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
