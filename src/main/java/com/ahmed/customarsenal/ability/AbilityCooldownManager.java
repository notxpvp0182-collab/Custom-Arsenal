package com.ahmed.customarsenal.ability;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AbilityCooldownManager {
    // Player UUID -> (Ability ID -> Expiration GameTime Tick)
    private static final Map<UUID, Map<String, Long>> SERVER_COOLDOWNS = new ConcurrentHashMap<>();
    private static final Map<String, CooldownEntry> CLIENT_COOLDOWNS = new ConcurrentHashMap<>();

    public record CooldownEntry(long startTick, long endTick) {
        public float getProgress(long currentTick) {
            if (currentTick >= endTick) return 0.0f;
            long total = Math.max(1, endTick - startTick);
            return Math.clamp((float) (endTick - currentTick) / total, 0.0f, 1.0f);
        }

        public float getRemainingSeconds(long currentTick) {
            if (currentTick >= endTick) return 0.0f;
            return (endTick - currentTick) / 20.0f;
        }
    }

    public static boolean isOnCooldown(UUID playerId, String abilityId, long currentGameTime) {
        Map<String, Long> map = SERVER_COOLDOWNS.get(playerId);
        if (map == null) return false;
        Long expiry = map.get(abilityId);
        if (expiry == null) return false;
        if (currentGameTime >= expiry) {
            map.remove(abilityId);
            return false;
        }
        return true;
    }

    public static long getRemainingTicks(UUID playerId, String abilityId, long currentGameTime) {
        Map<String, Long> map = SERVER_COOLDOWNS.get(playerId);
        if (map == null) return 0;
        Long expiry = map.get(abilityId);
        if (expiry == null || currentGameTime >= expiry) return 0;
        return expiry - currentGameTime;
    }

    public static void setCooldown(UUID playerId, String abilityId, long currentGameTime, int durationTicks) {
        SERVER_COOLDOWNS.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                .put(abilityId, currentGameTime + durationTicks);
    }

    public static void setClientCooldown(String abilityId, long currentClientTick, int durationTicks) {
        CLIENT_COOLDOWNS.put(abilityId, new CooldownEntry(currentClientTick, currentClientTick + durationTicks));
    }

    public static boolean isClientOnCooldown(String abilityId, long currentClientTick) {
        CooldownEntry entry = CLIENT_COOLDOWNS.get(abilityId);
        if (entry == null) return false;
        if (currentClientTick >= entry.endTick()) {
            CLIENT_COOLDOWNS.remove(abilityId);
            return false;
        }
        return true;
    }

    public static CooldownEntry getClientEntry(String abilityId, long currentClientTick) {
        CooldownEntry entry = CLIENT_COOLDOWNS.get(abilityId);
        if (entry != null && currentClientTick >= entry.endTick()) {
            CLIENT_COOLDOWNS.remove(abilityId);
            return null;
        }
        return entry;
    }

    public static void clearPlayer(UUID playerId) {
        SERVER_COOLDOWNS.remove(playerId);
    }

    public static void clearAll() {
        SERVER_COOLDOWNS.clear();
        CLIENT_COOLDOWNS.clear();
    }
}
