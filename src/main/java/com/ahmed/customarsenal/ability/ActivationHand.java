package com.ahmed.customarsenal.ability;

import net.minecraft.world.InteractionHand;

public enum ActivationHand {
    MAIN_HAND("Main Hand Only"),
    OFF_HAND("Offhand Only"),
    BOTH("Both Hands");

    private final String displayName;

    ActivationHand(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean matches(InteractionHand hand) {
        return switch (this) {
            case MAIN_HAND -> hand == InteractionHand.MAIN_HAND;
            case OFF_HAND -> hand == InteractionHand.OFF_HAND;
            case BOTH -> true;
        };
    }

    public ActivationHand next() {
        ActivationHand[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
