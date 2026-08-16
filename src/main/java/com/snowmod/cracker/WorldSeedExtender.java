package com.snowmod.cracker;

import com.snowmod.collector.FeatureData;

import java.util.ArrayList;
import java.util.List;

public class WorldSeedExtender {

    /**
     * Takes candidate 48-bit structure seed and tests all 65536 upper bit extensions (0..65535)
     * against collected features.
     */
    public static List<Long> extendTo64Bit(long structureSeed48, List<FeatureData> features) {
        List<Long> matches = new ArrayList<>();
        if (features == null || features.isEmpty()) return matches;

        for (int upper = 0; upper < 65536; upper++) {
            long worldSeed64 = ((long) upper << 48) | (structureSeed48 & 0xFFFFFFFFFFFFL);

            if (verifyWorldSeed(worldSeed64, features)) {
                matches.add(worldSeed64);
            }
        }
        return matches;
    }

    private static boolean verifyWorldSeed(long worldSeed64, List<FeatureData> features) {
        for (FeatureData feature : features) {
            if (feature.getType().getSpacing() > 0) {
                if (!StructureCracker.matchesStructureSeed(worldSeed64 & 0xFFFFFFFFFFFFL, feature)) {
                    return false;
                }
            }
        }
        return true;
    }
}
