package com.ahmed.customarsenal.item;

import com.ahmed.customarsenal.ability.CustomAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public class CustomItem extends Item {
    private final CustomItemData customData;

    public CustomItem(CustomItemData customData, Properties properties) {
        super(configureProperties(customData, properties));
        this.customData = customData;
    }

    private static Properties configureProperties(CustomItemData data, Properties props) {
        props.stacksTo(data.maxStackSize()).rarity(data.rarity());
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
        return props;
    }

    public CustomItemData getCustomData() {
        return customData;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(customData.displayName()).withStyle(customData.element().chatColor());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (customData.element() != null) {
            customData.element().applyElementalHit(target, attacker);
        }
        if (customData.maxDurability() > 0) {
            stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        // Lightweight check: only apply passive effect once every 40 ticks (2 seconds) when held in main/offhand
        if (customData.passiveHeldEffect() != null && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {
            if (level.getGameTime() % 40L == 0L && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(customData.passiveHeldEffect(), 65, 0, true, false, true));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Element: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(customData.element().displayName()).withStyle(customData.element().chatColor())));
        tooltip.add(Component.literal("Category: " + customData.category().getDisplayName())
                .withStyle(ChatFormatting.DARK_GRAY));

        for (String line : customData.lore()) {
            tooltip.add(Component.literal(line).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }

        if (!customData.abilities().isEmpty()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Contextual Abilities:").withStyle(ChatFormatting.GOLD));
            for (CustomAbility ability : customData.abilities()) {
                String defaultKey = switch (ability.keySlot()) {
                    case "slot_1" -> "R";
                    case "slot_2" -> "G";
                    case "slot_3" -> "V";
                    case "shield" -> "C";
                    default -> "R";
                };
                float cdSec = ability.getEffectiveCooldownTicks() / 20.0f;
                tooltip.add(Component.literal(" [" + defaultKey + "] " + ability.displayName())
                        .withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal(" (" + String.format("%.1fs", cdSec) + " | " + ability.getEffectiveHand().getDisplayName() + ")")
                                .withStyle(ChatFormatting.DARK_GRAY)));
                tooltip.add(Component.literal("   " + ability.description()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
