package com.ahmed.customarsenal;

import com.ahmed.customarsenal.ability.AbilityCooldownManager;
import com.ahmed.customarsenal.ability.AbilityRegistry;
import com.ahmed.customarsenal.config.CustomItemConfig;
import com.ahmed.customarsenal.element.ElementRegistry;
import com.ahmed.customarsenal.item.CustomItemRegistry;
import com.ahmed.customarsenal.network.ModNetworking;
import com.ahmed.customarsenal.util.CustomSoundManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomArsenalMod implements ModInitializer {
    public static final String MOD_ID = "custom_arsenal";
    public static final String DEVELOPER = "Ahmed";
    public static final Logger LOGGER = LoggerFactory.getLogger("CustomArsenal");

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Custom Arsenal by {} for Minecraft 1.21.11", DEVELOPER);

        CustomItemConfig.load();
        CustomSoundManager.init();
        ElementRegistry.init();
        AbilityRegistry.init();
        CustomItemRegistry.init();
        ModNetworking.initCommon();

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
            AbilityCooldownManager.clearPlayer(handler.getPlayer().getUUID())
        );
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
            AbilityCooldownManager.clearAll()
        );

        LOGGER.info("Custom Arsenal initialized with {} elements, {} abilities, and {} custom items.",
                ElementRegistry.size(), AbilityRegistry.size(), CustomItemRegistry.size());
    }
}
