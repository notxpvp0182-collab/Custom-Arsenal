package com.ahmed.customarsenal.item;

import com.ahmed.customarsenal.ability.AbilityManager;
import com.ahmed.customarsenal.ability.AbilityTrigger;
import com.ahmed.customarsenal.ability.CustomAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CustomItem extends Item {
    private final CustomItemData customData;

    public CustomItem(CustomItemData customData, Properties properties) {
        super(configureProperties(customData, properties));
        this.customData = customData;
    }

    private static Properties configureProperties(CustomItemData data, Properties props) {
        props.stacksTo(data.maxStackSize()).rarity(data.customRarity().getVanillaRarity());
        if (data.maxDurability() > 0 && data.maxStackSize() == 1) {
            props.durability(data.maxDurability());
        }
        if (data.attackDamage() > 0) {
            ItemAttributeModifiers modifiers = ItemAttributeModifiers.builder()
                    .add(Attributes.ATTACK_DAMAGE,
                            new AttributeModifier(BASE_ATTACK_DAMAGE_ID, data.attackDamage() - 1.0, AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND)
                    .add(Attributes.ATTACK_SPEED,
                            new AttributeModifier(BASE_ATTACK_SPEED_ID, data.attackSpeed() - 4.0, AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND)
                    .build();
            props.attributes(modifiers);
        }

        // Concise, no-fluff tooltip: Rarity, Element, Ability Name, Keybind, Cooldown
        List<Component> loreLines = new ArrayList<>();
        loreLines.add(Component.literal(data.customRarity().getDisplayName())
                .withStyle(data.customRarity().getFormatting(), ChatFormatting.BOLD)
                .append(Component.literal(" • " + data.element().displayName()).withStyle(data.element().chatColor())));

        for (CustomAbility ability : data.abilities()) {
            AbilityTrigger trig = AbilityTrigger.fromSlotId(ability.keySlot());
            float cdSec = ability.getEffectiveCooldownTicks() / 20.0f;
            loreLines.add(Component.literal(trig.getShortLabel() + ": ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(ability.displayName()).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" (" + String.format("%.0fs", cdSec) + ")").withStyle(ChatFormatting.DARK_GRAY)));
        }
        props.component(DataComponents.LORE, new ItemLore(loreLines));
        return props;
    }

    public CustomItemData getCustomData() {
        return customData;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(customData.displayName()).withStyle(customData.customRarity().getFormatting());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        String slot = player.isShiftKeyDown()
                ? AbilityTrigger.SHIFT_RIGHT_CLICK.getSlotId()
                : AbilityTrigger.RIGHT_CLICK.getSlotId();

        CustomAbility ability = customData.getAbilityBySlot(slot);
        if (ability == null) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            AbilityManager.tryExecuteServer(serverPlayer, customData.id(), ability.id(), hand);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Normal Left Click performs normal melee attack + elemental hit.
        // Shift + Left Click additionally triggers the item's Shift+LMB special ability!
        if (customData.element() != null) {
            customData.element().applyElementalHit(target, attacker);
        }
        if (attacker instanceof ServerPlayer serverPlayer && serverPlayer.isShiftKeyDown()) {
            CustomAbility shiftLeftAbility = customData.getAbilityBySlot(AbilityTrigger.SHIFT_LEFT_CLICK.getSlotId());
            if (shiftLeftAbility != null) {
                AbilityManager.tryExecuteServer(serverPlayer, customData.id(), shiftLeftAbility.id(), InteractionHand.MAIN_HAND);
            }
        }
        if (customData.maxDurability() > 0) {
            stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (customData.passiveHeldEffect() != null && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {
            if (level.getGameTime() % 40L == 0L && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(customData.passiveHeldEffect(), 65, 0, true, false, true));
            }
        }
    }
}
