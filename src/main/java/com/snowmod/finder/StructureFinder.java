package com.snowmod.finder;

import com.snowmod.collector.CollectedFeatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class StructureFinder extends Finder {
    private final CollectedFeatureType type;

    public StructureFinder(CollectedFeatureType type, ChunkPos chunkPos, BlockPos blockPos, String dimension) {
        super(chunkPos, blockPos, dimension, 16, 0x00FF88); // 16 bits per structure, emerald green
        this.type = type;
    }

    public CollectedFeatureType getType() {
        return type;
    }

    @Override
    public String getName() {
        return type.getDisplayName();
    }
}
