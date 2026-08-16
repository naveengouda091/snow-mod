package com.snowmod.finder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class BiomeFinder extends Finder {
    private final String biomeId;

    public BiomeFinder(ChunkPos chunkPos, BlockPos blockPos, String dimension, String biomeId) {
        super(chunkPos, blockPos, dimension, 4, 0x00CCCC); // 4 bits per biome sample, cyan
        this.biomeId = biomeId;
    }

    public String getBiomeId() {
        return biomeId;
    }

    @Override
    public String getName() {
        return "Biome: " + biomeId;
    }
}
