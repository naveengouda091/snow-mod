package com.snowmod.finder;

import com.snowmod.cracker.SeedCrackerEngine;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BitTracker {
    private static final BitTracker INSTANCE = new BitTracker();
    private final Set<Finder> activeFinders = ConcurrentHashMap.newKeySet();
    private final AtomicInteger totalBits = new AtomicInteger(0);
    private boolean autoCrackTriggered = false;

    private BitTracker() {}

    public static BitTracker getInstance() {
        return INSTANCE;
    }

    public void addFinder(Finder finder) {
        if (activeFinders.add(finder)) {
            int newTotal = totalBits.addAndGet(finder.getBitsContributed());
            
            // Auto-trigger cracking as soon as 48 bits are reached
            if (newTotal >= 48 && !autoCrackTriggered) {
                autoCrackTriggered = true;
                SeedCrackerEngine.getInstance().startCracking();
            }
        }
    }

    public Set<Finder> getActiveFinders() {
        return Collections.unmodifiableSet(activeFinders);
    }

    public int getTotalBits() {
        return totalBits.get();
    }

    public int getStructureBits() {
        return Math.min(48, totalBits.get());
    }

    public int getWorldBits() {
        return Math.max(0, Math.min(16, totalBits.get() - 48));
    }

    public void clear() {
        activeFinders.clear();
        totalBits.set(0);
        autoCrackTriggered = false;
    }
}
