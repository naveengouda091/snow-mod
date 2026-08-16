package com.snowmod.collector;

public enum CollectedFeatureType {
    VILLAGE("Village", 10387312, 34, 8),
    PILLAGER_OUTPOST("Pillager Outpost", 165745296, 32, 8),
    SHIPWRECK("Shipwreck", 165745295, 24, 4),
    OCEAN_MONUMENT("Ocean Monument", 10387313, 32, 5),
    WOODLAND_MANSION("Woodland Mansion", 10387319, 80, 20),
    DESERT_PYRAMID("Desert Pyramid", 14357617, 32, 8),
    JUNGLE_TEMPLE("Jungle Temple", 14357619, 32, 8),
    SWAMP_HUT("Swamp Hut", 14357620, 32, 8),
    IGLOO("Igloo", 14357618, 32, 8),
    OCEAN_RUIN("Ocean Ruin", 14357621, 20, 8),
    RUINED_PORTAL("Ruined Portal", 34222645, 40, 15),
    TRIAL_CHAMBERS("Trial Chambers", 94251327, 34, 12),
    ANCIENT_CITY("Ancient City", 20083237, 24, 8),
    NETHER_FORTRESS("Nether Fortress", 30084232, 30, 4),
    BASTION_REMNANT("Bastion Remnant", 30084233, 30, 4),
    END_CITY("End City", 10387313, 20, 11),
    END_GATEWAY("End Gateway", 0, 0, 0),
    DUNGEON_SPAWNER("Dungeon Spawner", 0, 0, 0),
    SLIME_CHUNK("Slime Chunk", 0, 0, 0);

    private final String displayName;
    private final int salt;
    private final int spacing;
    private final int separation;

    CollectedFeatureType(String displayName, int salt, int spacing, int separation) {
        this.displayName = displayName;
        this.salt = salt;
        this.spacing = spacing;
        this.separation = separation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSalt() {
        return salt;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getSeparation() {
        return separation;
    }
}
