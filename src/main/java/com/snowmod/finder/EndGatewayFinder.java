package com.snowmod.finder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class EndGatewayFinder extends Finder {
    private final long angle;

    public EndGatewayFinder(ChunkPos chunkPos, BlockPos blockPos, String dimension, long angle) {
        super(chunkPos, blockPos, dimension, 48, 0xCC55FF); // 48 bits, purple
        this.angle = angle;
    }

    public long getAngle() {
        return angle;
    }

    @Override
    public String getName() {
        return "End Gateway (" + angle + "°)";
    }
}
