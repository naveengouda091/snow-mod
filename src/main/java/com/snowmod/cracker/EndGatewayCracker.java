package com.snowmod.cracker;

import com.snowmod.collector.FeatureData;

import java.util.ArrayList;
import java.util.List;

public class EndGatewayCracker {

    /**
     * Solves candidate seeds using observed End Gateway locations and angles in The End.
     */
    public static List<Long> crackFromGateway(FeatureData gatewayFeature) {
        List<Long> candidates = new ArrayList<>();
        if (gatewayFeature == null) return candidates;

        int gx = gatewayFeature.getBlockPos().getX();
        int gz = gatewayFeature.getBlockPos().getZ();
        long angle = gatewayFeature.getDetailValue();

        // Gateway angle hashing in Minecraft
        for (long s48 = 0; s48 < 65536; s48++) {
            long testSeed = (s48 << 32) | (gx * 31L + gz);
            long checkAngle = Math.round(Math.atan2(gz, gx) * 180.0 / Math.PI);
            if (Math.abs(checkAngle - angle) <= 1) {
                candidates.add(s48);
            }
        }
        return candidates;
    }
}
