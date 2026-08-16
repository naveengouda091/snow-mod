package com.snowmod.cracker;

import com.snowmod.collector.CollectedFeatureType;
import com.snowmod.collector.FeatureData;

import java.util.ArrayList;
import java.util.List;

public class StructureCracker {

    /**
     * Verifies if a 48-bit seed generates the given structure feature.
     */
    public static boolean matchesStructureSeed(long seed48, FeatureData feature) {
        CollectedFeatureType type = feature.getType();
        if (type.getSpacing() <= 0) return false; // Must be a valid region structure

        int regionX = Math.floorDiv(feature.getChunkPos().x, type.getSpacing());
        int regionZ = Math.floorDiv(feature.getChunkPos().z, type.getSpacing());

        long salt = type.getSalt();
        long stepSeed = regionX * 341873128712L + regionZ * 132897987541L + seed48 + salt;
        
        // Java Random LCG algorithm
        long lcgSeed = (stepSeed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL;
        lcgSeed = (lcgSeed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;
        int maxOffset = type.getSpacing() - type.getSeparation();
        if (maxOffset <= 0) return false;

        int offsetX = Math.abs((int) ((lcgSeed >>> 16) % maxOffset));

        lcgSeed = (lcgSeed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;
        int offsetZ = Math.abs((int) ((lcgSeed >>> 16) % maxOffset));

        int expectedChunkX = regionX * type.getSpacing() + offsetX;
        int expectedChunkZ = regionZ * type.getSpacing() + offsetZ;

        return Math.abs(feature.getChunkPos().x - expectedChunkX) <= 3 &&
               Math.abs(feature.getChunkPos().z - expectedChunkZ) <= 3;
    }

    /**
     * Finds 48-bit structure seeds matching all collected region structures.
     */
    public static List<Long> findCandidateStructureSeeds(List<FeatureData> features, long startSeed, long endSeed) {
        List<Long> results = new ArrayList<>();
        if (features == null || features.isEmpty()) return results;

        List<FeatureData> regionStructures = new ArrayList<>();
        for (FeatureData f : features) {
            if (f.getType().getSpacing() > 0) {
                regionStructures.add(f);
            }
        }

        if (regionStructures.isEmpty()) return results;

        for (long seed = startSeed; seed < endSeed; seed++) {
            boolean matchesAll = true;
            for (FeatureData struct : regionStructures) {
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
