package com.snowmod.cracker;

import com.snowmod.collector.FeatureCollector;
import com.snowmod.collector.FeatureData;
import com.snowmod.finder.BitTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SeedCrackerEngine {
    private static final SeedCrackerEngine INSTANCE = new SeedCrackerEngine();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() - 1));

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger progressPercent = new AtomicInteger(0);
    private String statusMessage = "Idle";
    private Long foundSeed = null;
    private final List<Long> candidateSeeds = new ArrayList<>();

    private SeedCrackerEngine() {}

    public static SeedCrackerEngine getInstance() {
        return INSTANCE;
    }

    public void reset() {
        running.set(false);
        progressPercent.set(0);
        statusMessage = "Idle";
        foundSeed = null;
        candidateSeeds.clear();
    }

    public void startCracking() {
        if (running.get()) return;

        BitTracker tracker = BitTracker.getInstance();
        int distinct = tracker.getDistinctStructureCount();
        boolean hasGateway = tracker.hasEndGateway();

        if (distinct < 3 && !hasGateway) {
            int needed = 3 - distinct;
            statusMessage = "Need " + needed + " more distinct structure(s) to solve! (Explore near Villages/Pyramids/Shipwrecks)";
            foundSeed = null;
            return;
        }

        List<FeatureData> features = new ArrayList<>(FeatureCollector.getInstance().getCollectedFeatures());
        List<FeatureData> regionStructures = new ArrayList<>();
        for (FeatureData f : features) {
            if (f.getType().getSpacing() > 0) {
                regionStructures.add(f);
            }
        }

        running.set(true);
        progressPercent.set(0);
        statusMessage = "Cracking 48-bit structure seed...";
        foundSeed = null;
        candidateSeeds.clear();

        threadPool.submit(() -> {
            try {
                long totalSearch = 1L << 28; // 268M search range batch
                long chunkSize = totalSearch / 100;

                for (int i = 0; i < 100 && running.get(); i++) {
                    long start = i * chunkSize;
                    long end = start + chunkSize;

                    List<Long> matches = StructureCracker.findCandidateStructureSeeds(regionStructures, start, end);
                    if (!matches.isEmpty()) {
                        candidateSeeds.addAll(matches);
                    }
                    progressPercent.set(i + 1);

                    if (!candidateSeeds.isEmpty()) {
                        break;
                    }
                }

                if (!candidateSeeds.isEmpty()) {
                    long s48 = candidateSeeds.get(0);
                    statusMessage = "Extending to 64-bit world seed...";
                    List<Long> seeds64 = WorldSeedExtender.extendTo64Bit(s48, regionStructures);
                    if (!seeds64.isEmpty()) {
                        foundSeed = seeds64.get(0);
                        statusMessage = "Seed Cracked Successfully!";
                    } else {
                        foundSeed = s48;
                        statusMessage = "Structure Seed Found (48-bit): " + s48;
                    }
                } else {
                    statusMessage = "No matching seed in batch. Explore 1 more structure!";
                    foundSeed = null;
                }
            } catch (Exception e) {
                statusMessage = "Error during cracking: " + e.getMessage();
                foundSeed = null;
            } finally {
                running.set(false);
            }
        });
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getProgressPercent() {
        return progressPercent.get();
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Long getFoundSeed() {
        return foundSeed;
    }
}
