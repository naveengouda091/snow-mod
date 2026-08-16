package com.snowmod.cracker;

import com.snowmod.collector.CollectedFeatureType;
import com.snowmod.collector.FeatureData;

import java.util.ArrayList;
import java.util.List;

public class StructureCracker {

    /**
     * Checks if a 48-bit seed generates the given structure feature at its observed chunk position.
     */
    public static boolean matchesStructureSeed(long seed48, FeatureData feature) {
        CollectedFeatureType type = feature.getType();
        if (type.getSpacing() <= 0) return true;

        int regionX = Math.floorDiv(feature.getChunkPos().x, type.getSpacing());
        int regionZ = Math.floorDiv(feature.getChunkPos().z, type.getSpacing());

        long salt = type.getSalt();
        long stepSeed = regionX * 341873128712L + regionZ * 132897987541L + seed48 + salt;
        
        // Java Random LCG algorithm
        long lcgSeed = (stepSeed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL;
        lcgSeed = (lcgSeed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;
        int maxOffset = type.getSpacing() - type.getSeparation();
        int offsetX = (int) ((lcgSeed >>> 16) % maxOffset);

        lcgSeed = (lcgSeed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;
        int offsetZ = (int) ((lcgSeed >>> 16) % maxOffset);

        int expectedChunkX = regionX * type.getSpacing() + offsetX;
        int expectedChunkZ = regionZ * type.getSpacing() + offsetZ;

        return feature.getChunkPos().x == expectedChunkX && feature.getChunkPos().z == expectedChunkZ;
    }

    /**
     * Finds 48-bit structure seeds matching a list of collected structure features.
     */
    public static List<Long> findCandidateStructureSeeds(List<FeatureData> structures, long startSeed, long endSeed) {
        List<Long> results = new ArrayList<>();
        if (structures.isEmpty()) return results;

        for (long seed = startSeed; seed < endSeed; seed++) {
            boolean matchesAll = true;
            for (FeatureData struct : structures) {
                if (!matchesStructureSeed(seed, struct)) {
                    matchesAll = false;
                    break;
                }
            }
            if (matchesAll) {
                results.add(seed);
            }
        }
        return results;
    }
}
