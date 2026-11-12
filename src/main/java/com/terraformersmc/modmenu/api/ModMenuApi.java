package com.terraformersmc.modmenu.api;

import net.minecraft.network.chat.Component;
import java.util.Map;

public interface ModMenuApi {
    static Component createModsButtonText() {
        return Component.translatable("modmenu.title");
    }

    default ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> null;
    }

    default Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return Map.of();
    }
}
