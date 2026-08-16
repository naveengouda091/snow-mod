package com.snowmod.finder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class DungeonFinder extends Finder {
    private final String entityType;

    public DungeonFinder(ChunkPos chunkPos, BlockPos blockPos, String dimension, String entityType) {
        super(chunkPos, blockPos, dimension, 24, 0xFF4444); // 24 bits, red
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

    @Override
    public String getName() {
        return "Dungeon (" + entityType + ")";
    }
}
