package com.ahmed.customarsenal.client.render;

import com.ahmed.customarsenal.config.CustomItemConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;

public final class CustomItemRenderer {
    private static long mainHandAnimStartMs = 0L;
    private static long offHandAnimStartMs = 0L;
    private static final long ANIM_DURATION_MS = 280L;

    public static void init() {
        // Initialized during client startup
    }

    public static void triggerActivationAnimation(InteractionHand hand) {
        if (!CustomItemConfig.get().enableAbilityAnimations) return;
        long now = System.currentTimeMillis();
        if (hand == InteractionHand.MAIN_HAND) {
            mainHandAnimStartMs = now;
        } else {
            offHandAnimStartMs = now;
        }
    }

    public static float getAnimationProgress(InteractionHand hand) {
        if (!CustomItemConfig.get().enableAbilityAnimations) return 0.0f;
        long start = (hand == InteractionHand.MAIN_HAND) ? mainHandAnimStartMs : offHandAnimStartMs;
        if (start == 0L) return 0.0f;
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed >= ANIM_DURATION_MS) return 0.0f;
        return Mth.sin(((float) elapsed / ANIM_DURATION_MS) * (float) Math.PI);
    }
}
