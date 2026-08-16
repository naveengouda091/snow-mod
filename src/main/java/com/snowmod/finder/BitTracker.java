package com.snowmod.finder;

import com.snowmod.cracker.SeedCrackerEngine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BitTracker {
    private static final BitTracker INSTANCE = new BitTracker();
    private final Set<Finder> activeFinders = ConcurrentHashMap.newKeySet();
    private boolean autoCrackTriggered = false;

    private BitTracker() {}

    public static BitTracker getInstance() {
        return INSTANCE;
    }

    public void addFinder(Finder finder) {
        if (activeFinders.add(finder)) {
            checkAutoCrack();
        }
    }

    private void checkAutoCrack() {
        if (getStructureBits() >= 48 && !autoCrackTriggered) {
            autoCrackTriggered = true;
            SeedCrackerEngine.getInstance().startCracking();
        }
    }

    public Set<Finder> getActiveFinders() {
        return Collections.unmodifiableSet(activeFinders);
    }

    public int getDistinctStructureCount() {
        Set<String> distinctStructures = new HashSet<>();
        for (Finder f : activeFinders) {
            if (f instanceof StructureFinder sf) {
                int spacing = sf.getType().getSpacing();
                if (spacing > 0) {
                    int rx = Math.floorDiv(sf.getChunkPos().x, spacing);
                    int rz = Math.floorDiv(sf.getChunkPos().z, spacing);
                    distinctStructures.add(sf.getType().name() + ":" + rx + ":" + rz);
                }
            }
        }
        return distinctStructures.size();
    }

    public boolean hasEndGateway() {
        for (Finder f : activeFinders) {
            if (f instanceof EndGatewayFinder) return true;
        }
        return false;
    }

    public int getStructureBits() {
        if (hasEndGateway()) return 48;
        int distinct = getDistinctStructureCount();
        return Math.min(48, distinct * 16);
    }

    public int getWorldBits() {
        int biomes = 0;
        for (Finder f : activeFinders) {
            if (f instanceof BiomeFinder) biomes++;
        }
        return Math.min(16, biomes);
    }

    public int getTotalBits() {
        return getStructureBits() + getWorldBits();
    }

    public void clear() {
        activeFinders.clear();
        autoCrackTriggered = false;
    }
}
