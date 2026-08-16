package com.snowmod.collector;

import net.minecraft.block.*;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
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
    private final Set<String> detectedRegions = ConcurrentHashMap.newKeySet();

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
        int radius = Math.min(renderDistance, 16);

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

        // 1. Fast Block Entity Scanning (Spawners, Bells, End Gateways, Chests)
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            BlockPos pos = be.getPos();
            BlockState state = chunk.getBlockState(pos);

            if (be instanceof MobSpawnerBlockEntity) {
                FeatureData feature = new FeatureData(CollectedFeatureType.DUNGEON_SPAWNER, chunkPos, pos, dimension, pos.asLong());
                addFeature(feature);
            } else if (state.isOf(Blocks.END_GATEWAY) && "the_end".equals(dimension)) {
                long angle = Math.round(Math.atan2(pos.getZ(), pos.getX()) * 180.0 / Math.PI);
                FeatureData feature = new FeatureData(CollectedFeatureType.END_GATEWAY, chunkPos, pos, dimension, angle);
                addFeature(feature);
            } else if (be instanceof BellBlockEntity || state.getBlock() instanceof BedBlock) {
                registerStructure(CollectedFeatureType.VILLAGE, chunkPos, pos, dimension);
            }
        }

        // 2. Block State Pattern Scanning across chunk
        int minY = world.getBottomY();
        int maxY = world.getTopYInclusive();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Sample every 4 vertical blocks for high speed
                for (int y = minY; y < maxY; y += 4) {
                    BlockPos pos = new BlockPos(chunkPos.getStartX() + x, y, chunkPos.getStartZ() + z);
                    BlockState state = chunk.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block == Blocks.AIR || block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRASS_BLOCK) continue;

                    // Village
                    if (block == Blocks.BELL || block == Blocks.COMPOSTER || block == Blocks.LECTERN || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER) {
                        registerStructure(CollectedFeatureType.VILLAGE, chunkPos, pos, dimension);
                    }
                    // Shipwreck (Chests/Wood in water or beach)
                    else if ((block == Blocks.CHEST || block == Blocks.WATER) && (chunk.getBlockState(pos.down()).isOf(Blocks.OAK_PLANKS) || chunk.getBlockState(pos.down()).isOf(Blocks.SPRUCE_PLANKS) || chunk.getBlockState(pos.down()).isOf(Blocks.DARK_OAK_PLANKS))) {
                        registerStructure(CollectedFeatureType.SHIPWRECK, chunkPos, pos, dimension);
                    }
                    // Desert Pyramid
                    else if (block == Blocks.CHISELED_SANDSTONE || block == Blocks.BLUE_TERRACOTTA) {
                        registerStructure(CollectedFeatureType.DESERT_PYRAMID, chunkPos, pos, dimension);
                    }
                    // Ruined Portal
                    else if (block == Blocks.CRYING_OBSIDIAN) {
                        registerStructure(CollectedFeatureType.RUINED_PORTAL, chunkPos, pos, dimension);
                    }
                    // Trial Chambers (1.21+)
                    else if (block == Blocks.VAULT || block == Blocks.TRIAL_SPAWNER || block == Blocks.CHISELED_TUFF) {
                        registerStructure(CollectedFeatureType.TRIAL_CHAMBERS, chunkPos, pos, dimension);
                    }
                    // Ancient City
                    else if (block == Blocks.REINFORCED_DEEPSLATE || block == Blocks.SCULK_SHRIEKER) {
                        registerStructure(CollectedFeatureType.ANCIENT_CITY, chunkPos, pos, dimension);
                    }
                    // Ocean Monument
                    else if (block == Blocks.PRISMARINE || block == Blocks.SEA_LANTERN) {
                        registerStructure(CollectedFeatureType.OCEAN_MONUMENT, chunkPos, pos, dimension);
                    }
                    // Jungle Temple
                    else if (block == Blocks.MOSSY_COBBLESTONE && chunk.getBlockState(pos.up()).isOf(Blocks.CHISELED_STONE_BRICKS)) {
                        registerStructure(CollectedFeatureType.JUNGLE_TEMPLE, chunkPos, pos, dimension);
                    }
                    // Nether Fortress
                    else if (block == Blocks.NETHER_BRICKS || block == Blocks.NETHER_BRICK_FENCE) {
                        if ("the_nether".equals(dimension)) {
                            registerStructure(CollectedFeatureType.NETHER_FORTRESS, chunkPos, pos, dimension);
                        }
                    }
                    // Bastion Remnant
                    else if (block == Blocks.POLISHED_BLACKSTONE_BRICKS || block == Blocks.GILDED_BLACKSTONE) {
                        if ("the_nether".equals(dimension)) {
                            registerStructure(CollectedFeatureType.BASTION_REMNANT, chunkPos, pos, dimension);
                        }
                    }
                    // End City
                    else if (block == Blocks.PURPUR_BLOCK || block == Blocks.END_STONE_BRICKS) {
                        if ("the_end".equals(dimension)) {
                            registerStructure(CollectedFeatureType.END_CITY, chunkPos, pos, dimension);
                        }
                    }
                }
            }
        }
    }

    private void registerStructure(CollectedFeatureType type, ChunkPos chunkPos, BlockPos blockPos, String dimension) {
        int spacing = type.getSpacing();
        if (spacing <= 0) return;

        int regionX = Math.floorDiv(chunkPos.x, spacing);
        int regionZ = Math.floorDiv(chunkPos.z, spacing);
        String regionKey = type.name() + ":" + regionX + ":" + regionZ + ":" + dimension;

        if (detectedRegions.add(regionKey)) {
            FeatureData feature = new FeatureData(type, chunkPos, blockPos, dimension, 0);
            addFeature(feature);
        }
    }

    public void addFeature(FeatureData feature) {
        if (collectedFeatures.add(feature)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(
                    net.minecraft.text.Text.literal("§b[SnowMod] §aDetected " + feature.getType().getDisplayName() + " at Chunk [" + feature.getChunkPos().x + ", " + feature.getChunkPos().z + "]"),
                    true
                );
            }
        }
    }

    public Set<FeatureData> getCollectedFeatures() {
        return Collections.unmodifiableSet(collectedFeatures);
    }

    public void clear() {
        collectedFeatures.clear();
        processedChunks.clear();
        detectedRegions.clear();
    }
}
