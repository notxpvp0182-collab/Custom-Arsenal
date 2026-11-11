package com.ahmed.customarsenal.util;

import com.ahmed.customarsenal.config.CustomItemConfig;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class CustomParticleManager {

    public static void spawnBurst(ServerLevel level, Vec3 pos, ParticleOptions particle, int baseCount, double spread, double speed) {
        if (!CustomItemConfig.get().enableCustomParticles || particle == null) return;
        int count = Mth.clamp(Math.round(baseCount * CustomItemConfig.get().particleIntensity), 1, 80);
        level.sendParticles(particle, pos.x, pos.y, pos.z, count, spread, spread, spread, speed);
    }

    public static void spawnRing(ServerLevel level, Vec3 center, ParticleOptions particle, double radius, int points) {
        if (!CustomItemConfig.get().enableCustomParticles || particle == null) return;
        int scaledPoints = Mth.clamp(Math.round(points * CustomItemConfig.get().particleIntensity), 4, 48);
        for (int i = 0; i < scaledPoints; i++) {
            double angle = (2 * Math.PI * i) / scaledPoints;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            level.sendParticles(particle, x, center.y + 0.1, z, 1, 0.02, 0.02, 0.02, 0.01);
        }
    }

    public static void spawnLine(ServerLevel level, Vec3 start, Vec3 end, ParticleOptions particle, int steps) {
        if (!CustomItemConfig.get().enableCustomParticles || particle == null) return;
        int scaledSteps = Mth.clamp(Math.round(steps * CustomItemConfig.get().particleIntensity), 2, 40);
        for (int i = 0; i <= scaledSteps; i++) {
            double t = (double) i / scaledSteps;
            Vec3 point = start.lerp(end, t);
            level.sendParticles(particle, point.x, point.y, point.z, 1, 0.03, 0.03, 0.03, 0.005);
        }
    }
}
