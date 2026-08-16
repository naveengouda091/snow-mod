package com.snowmod.collector;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class FeatureData {
    private final CollectedFeatureType type;
    private final ChunkPos chunkPos;
    private final BlockPos blockPos;
    private final String dimension;
    private final long detailValue; // Angle, rotation, or height offset

    public FeatureData(CollectedFeatureType type, ChunkPos chunkPos, BlockPos blockPos, String dimension, long detailValue) {
        this.type = type;
        this.chunkPos = chunkPos;
        this.blockPos = blockPos;
        this.dimension = dimension;
        this.detailValue = detailValue;
    }

    public CollectedFeatureType getType() {
        return type;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public String getDimension() {
        return dimension;
    }

    public long getDetailValue() {
        return detailValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FeatureData other)) return false;
        return type == other.type &&
               chunkPos.equals(other.chunkPos) &&
               dimension.equals(other.dimension);
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 31 + chunkPos.hashCode() + dimension.hashCode();
    }

    @Override
    public String toString() {
        return type.getDisplayName() + " at Chunk [" + chunkPos.x + ", " + chunkPos.z + "] in " + dimension;
    }
}
