package com.snowmod.collector;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FeatureCollector {
    private static final FeatureCollector INSTANCE = new FeatureCollector();
    private final Set<FeatureData> collectedFeatures = ConcurrentHashMap.newKeySet();
    private final Set<ChunkPos> processedChunks = ConcurrentHashMap.newKeySet();

    private FeatureCollector() {}

    public static FeatureCollector getInstance() {
        return INSTANCE;
    }

    public void onClientTick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        ClientWorld world = client.world;
        BlockPos playerPos = client.player.getBlockPos();
        ChunkPos centerChunk = new ChunkPos(playerPos);

        int renderDistance = client.options.getViewDistance().getValue();
        int radius = Math.min(renderDistance, 8);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos cPos = new ChunkPos(centerChunk.x + dx, centerChunk.z + dz);
                if (world.isChunkLoaded(cPos.x, cPos.z) && processedChunks.add(cPos)) {
                    scanChunk(world, cPos);
                }
            }
        }
    }

    private void scanChunk(ClientWorld world, ChunkPos chunkPos) {
        WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
        if (chunk == null) return;

        String dimension = world.getRegistryKey().getValue().getPath();

        // Scan blocks in chunk for End Gateways and Monster Spawners
        int minY = world.getBottomY();
        int maxY = world.getTopYInclusive();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    BlockPos pos = new BlockPos(chunkPos.getStartX() + x, y, chunkPos.getStartZ() + z);
                    BlockState state = chunk.getBlockState(pos);

                    if (state.isOf(Blocks.END_GATEWAY) && "the_end".equals(dimension)) {
                        long angle = Math.round(Math.atan2(pos.getZ(), pos.getX()) * 180.0 / Math.PI);
                        FeatureData feature = new FeatureData(CollectedFeatureType.END_GATEWAY, chunkPos, pos, dimension, angle);
                        addFeature(feature);
                    } else if (state.isOf(Blocks.SPAWNER)) {
                        FeatureData feature = new FeatureData(CollectedFeatureType.DUNGEON_SPAWNER, chunkPos, pos, dimension, pos.asLong());
                        addFeature(feature);
                    }
                }
            }
        }
    }

    public void addFeature(FeatureData feature) {
        if (collectedFeatures.add(feature)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                // Background quiet notification
            }
        }
    }

    public Set<FeatureData> getCollectedFeatures() {
        return Collections.unmodifiableSet(collectedFeatures);
    }

    public void clear() {
        collectedFeatures.clear();
        processedChunks.clear();
    }
}
