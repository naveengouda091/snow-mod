package com.snowmod.finder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public abstract class Finder {
    protected final ChunkPos chunkPos;
    protected final BlockPos blockPos;
    protected final String dimension;
    protected final int bitsContributed;
    protected final int color;

    public Finder(ChunkPos chunkPos, BlockPos blockPos, String dimension, int bitsContributed, int color) {
        this.chunkPos = chunkPos;
        this.blockPos = blockPos;
        this.dimension = dimension;
        this.bitsContributed = bitsContributed;
        this.color = color;
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

    public int getBitsContributed() {
        return bitsContributed;
    }

    public int getColor() {
        return color;
    }

    public abstract String getName();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Finder finder)) return false;
        return chunkPos.equals(finder.chunkPos) &&
               dimension.equals(finder.dimension) &&
               getName().equals(finder.getName());
    }

    @Override
    public int hashCode() {
        return chunkPos.hashCode() * 31 + dimension.hashCode() * 17 + getName().hashCode();
    }
}
