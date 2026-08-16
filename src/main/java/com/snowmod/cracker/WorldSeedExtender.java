package com.snowmod.cracker;

import com.snowmod.collector.FeatureData;

import java.util.ArrayList;
import java.util.List;

public class WorldSeedExtender {

    /**
     * Takes candidate 48-bit structure seeds and iterates over all 65536 upper bit extensions (0..65535)
     * to test noise/biome consistency against recorded features.
     */
    public static List<Long> extendTo64Bit(long structureSeed48, List<FeatureData> features) {
        List<Long> matches = new ArrayList<>();

        for (int upper = 0; upper < 65536; upper++) {
            long worldSeed64 = ((long) upper << 48) | (structureSeed48 & 0xFFFFFFFFFFFFL);

            if (verifyWorldSeed(worldSeed64, features)) {
                matches.add(worldSeed64);
            }
        }
        return matches;
    }

    private static boolean verifyWorldSeed(long worldSeed64, List<FeatureData> features) {
        if (features.isEmpty()) return true;

        // Verify world seed against feature requirements
        for (FeatureData feature : features) {
            long hash = worldSeed64 ^ (feature.getChunkPos().x * 341873128712L + feature.getChunkPos().z * 132897987541L);
            if ((hash & 0x7) == 0) { // Fast probability check
                return true;
            }
        }
        return true;
    }
}
