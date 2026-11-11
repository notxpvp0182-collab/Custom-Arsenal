package com.ahmed.customarsenal.ability;

import com.ahmed.customarsenal.CustomArsenalMod;
import com.ahmed.customarsenal.element.ElementRegistry;
import com.ahmed.customarsenal.util.AssetValidator;
import com.ahmed.customarsenal.util.CustomParticleManager;
import com.ahmed.customarsenal.util.CustomSoundManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public final class AbilityRegistry {
    private static final Map<String, CustomAbility> ABILITIES = new LinkedHashMap<>();

    public static final CustomAbility SHADOW_DASH = register(new CustomAbility(
            "shadow_dash", "Shadow Dash",
            "Lunges through shadows, dealing dark damage along the path.",
            "slot_1", 100, ActivationHand.MAIN_HAND, ElementRegistry.SHADOW,
            CustomSoundManager.SHADOW_DASH, 7.0, 20,
            ctx -> !ctx.player().isPassenger(),
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                Vec3 look = player.getLookAngle();
                Vec3 start = player.position().add(0, 1.0, 0);
                Vec3 dashVel = new Vec3(look.x * 1.85, Math.max(0.25, look.y * 0.8), look.z * 1.85);
                player.setDeltaMovement(dashVel);
                player.hurtMarked = true;
                player.fallDistance = 0;

                CustomParticleManager.spawnBurst(level, start, ParticleTypes.PORTAL, 28, 0.4, 0.2);
                CustomParticleManager.spawnBurst(level, start, ParticleTypes.SMOKE, 16, 0.3, 0.05);

                AABB hitBox = player.getBoundingBox().expandTowards(look.scale(5.0)).inflate(1.2);
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, hitBox,
                        e -> e != player && e.isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(e));
                for (LivingEntity target : targets) {
                    target.hurtServer(level, player.damageSources().playerAttack(player), 8.0f);
                    ElementRegistry.SHADOW.applyElementalHit(target, player);
                    CustomParticleManager.spawnBurst(level, target.position().add(0, 1.0, 0), ParticleTypes.REVERSE_PORTAL, 12, 0.3, 0.1);
                }
                return true;
            }
    ));

    public static final CustomAbility SHADOW_STRIKE = register(new CustomAbility(
            "shadow_strike", "Shadow Strike",
            "Unleashes a devastating shadow crescent that blinds and weakens nearby foes.",
            "slot_2", 160, ActivationHand.MAIN_HAND, ElementRegistry.SHADOW,
            CustomSoundManager.SHADOW_STRIKE, 5.5, 60,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                Vec3 center = player.position().add(player.getLookAngle().scale(2.5));
                CustomParticleManager.spawnRing(level, player.position(), ParticleTypes.REVERSE_PORTAL, 4.0, 28);
                CustomParticleManager.spawnBurst(level, center.add(0, 1.0, 0), ParticleTypes.SOUL_FIRE_FLAME, 24, 1.2, 0.08);

                AABB box = player.getBoundingBox().inflate(5.0, 2.5, 5.0);
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box,
                        e -> e != player && e.isAlive());
                for (LivingEntity target : targets) {
                    target.hurtServer(level, player.damageSources().magic(), 11.0f);
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
                    ElementRegistry.SHADOW.applyElementalHit(target, player);
                }
                return true;
            }
    ));

    public static final CustomAbility SHADOW_TELEPORT = register(new CustomAbility(
            "shadow_teleport", "Shadow Teleport",
            "Blinks instantaneously through the void up to 14 blocks ahead.",
            "slot_3", 200, ActivationHand.MAIN_HAND, ElementRegistry.SHADOW,
            CustomSoundManager.VOID_PULSE, 14.0, 40,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                Vec3 eye = player.getEyePosition();
                Vec3 end = eye.add(player.getLookAngle().scale(14.0));
                BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                Vec3 dest = hit.getType() != HitResult.Type.MISS
                        ? hit.getLocation().subtract(player.getLookAngle().scale(0.8))
                        : end;

                CustomParticleManager.spawnLine(level, eye, dest, ParticleTypes.PORTAL, 20);
                player.teleportTo(dest.x, dest.y, dest.z);
                player.fallDistance = 0;
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0, false, false));
                CustomParticleManager.spawnBurst(level, dest, ParticleTypes.REVERSE_PORTAL, 30, 0.5, 0.15);
                return true;
            }
    ));

    public static final CustomAbility FIRE_SLASH = register(new CustomAbility(
            "fire_slash", "Fire Slash",
            "Projects a searing arc of inferno flames that ignites and knocks back enemies.",
            "slot_1", 100, ActivationHand.MAIN_HAND, ElementRegistry.FIRE,
            CustomSoundManager.FIRE_SLASH, 6.5, 80,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                Vec3 look = player.getLookAngle();
                for (int i = 1; i <= 6; i++) {
                    Vec3 point = player.getEyePosition().add(look.scale(i));
                    CustomParticleManager.spawnBurst(level, point, ParticleTypes.FLAME, 8, 0.45, 0.04);
                    CustomParticleManager.spawnBurst(level, point, ParticleTypes.LAVA, 2, 0.2, 0.02);
                }
                AABB area = player.getBoundingBox().expandTowards(look.scale(6.5)).inflate(2.0);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                    target.hurtServer(level, player.damageSources().onFire(), 9.5f);
                    ElementRegistry.FIRE.applyElementalHit(target, player);
                    target.knockback(0.6, -look.x, -look.z);
                }
                return true;
            }
    ));

    public static final CustomAbility FROST_WAVE = register(new CustomAbility(
            "frost_wave", "Frost Wave",
            "Releases a radial blizzard wave that deep-freezes and slows surrounding hostiles.",
            "slot_1", 120, ActivationHand.MAIN_HAND, ElementRegistry.ICE,
            CustomSoundManager.FROST_WAVE, 6.0, 100,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                CustomParticleManager.spawnRing(level, player.position(), ParticleTypes.SNOWFLAKE, 3.0, 20);
                CustomParticleManager.spawnRing(level, player.position(), ParticleTypes.SNOWFLAKE, 5.5, 32);

                AABB area = player.getBoundingBox().inflate(6.0, 2.5, 6.0);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                    target.hurtServer(level, player.damageSources().freeze(), 7.5f);
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
                    ElementRegistry.ICE.applyElementalHit(target, player);
                    CustomParticleManager.spawnBurst(level, target.position().add(0, 1.0, 0), ParticleTypes.SNOWFLAKE, 15, 0.4, 0.05);
                }
                return true;
            }
    ));

    public static final CustomAbility LIGHTNING_STRIKE = register(new CustomAbility(
            "lightning_strike", "Lightning Strike",
            "Calls down high-voltage lightning at the target location up to 24 blocks away.",
            "slot_1", 140, ActivationHand.BOTH, ElementRegistry.LIGHTNING,
            CustomSoundManager.THUNDER_STRIKE, 24.0, 60,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                Vec3 eye = player.getEyePosition();
                Vec3 end = eye.add(player.getLookAngle().scale(24.0));
                BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                Vec3 strikePos = hit.getLocation();

                CustomParticleManager.spawnLine(level, eye.add(0, -0.2, 0), strikePos, ParticleTypes.ELECTRIC_SPARK, 24);
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
                if (bolt != null) {
                    bolt.setPos(strikePos.x, strikePos.y, strikePos.z);
                    bolt.setCause(player);
                    level.addFreshEntity(bolt);
                }
                AABB area = AABB.ofSize(strikePos, 5.0, 5.0, 5.0);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                    target.hurtServer(level, player.damageSources().lightningBolt(), 10.0f);
                    ElementRegistry.LIGHTNING.applyElementalHit(target, player);
                }
                return true;
            }
    ));

    public static final CustomAbility VOID_PULSE = register(new CustomAbility(
            "void_pulse", "Void Pulse",
            "Implodes space in front of the caster, pulling enemies inward and inflicting Wither.",
            "slot_1", 160, ActivationHand.BOTH, ElementRegistry.VOID,
            CustomSoundManager.VOID_PULSE, 8.0, 80,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                Vec3 vortex = player.getEyePosition().add(player.getLookAngle().scale(4.5));
                CustomParticleManager.spawnBurst(level, vortex, ParticleTypes.REVERSE_PORTAL, 40, 1.5, 0.25);
                CustomParticleManager.spawnRing(level, vortex, ParticleTypes.PORTAL, 3.5, 24);

                AABB area = AABB.ofSize(vortex, 8.0, 6.0, 8.0);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                    Vec3 pull = vortex.subtract(target.position()).normalize().scale(0.75);
                    target.setDeltaMovement(target.getDeltaMovement().add(pull));
                    target.hurtMarked = true;
                    target.hurtServer(level, player.damageSources().fellOutOfWorld(), 8.5f);
                    ElementRegistry.VOID.applyElementalHit(target, player);
                }
                return true;
            }
    ));

    public static final CustomAbility GUARDIAN_BARRIER = register(new CustomAbility(
            "guardian_barrier", "Guardian Barrier",
            "Generates a hardened earth bastion, granting Absorption, Resistance II, and knocking back attackers.",
            "slot_1", 200, ActivationHand.BOTH, ElementRegistry.EARTH,
            CustomSoundManager.GUARDIAN_BARRIER, 4.5, 160,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 160, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 160, 2));
                CustomParticleManager.spawnRing(level, player.position(), ParticleTypes.TOTEM_OF_UNDYING, 2.8, 28);

                AABB area = player.getBoundingBox().inflate(4.5);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                    Vec3 push = target.position().subtract(player.position()).normalize();
                    target.knockback(1.2, -push.x, -push.z);
                    target.hurtServer(level, player.damageSources().playerAttack(player), 5.0f);
                }
                return true;
            }
    ));

    public static final CustomAbility GRAPPLE = register(new CustomAbility(
            "grapple", "Grapple Pull",
            "Fires a high-tension anchor line up to 28 blocks and propels you toward the anchor point.",
            "slot_1", 70, ActivationHand.BOTH, ElementRegistry.WIND,
            CustomSoundManager.GRAPPLE_LAUNCH, 28.0, 20,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                Vec3 eye = player.getEyePosition();
                Vec3 end = eye.add(player.getLookAngle().scale(28.0));
                BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                if (hit.getType() == HitResult.Type.MISS) {
                    return false;
                }
                Vec3 anchor = hit.getLocation();
                CustomParticleManager.spawnLine(level, eye, anchor, ParticleTypes.CRIT, 28);
                Vec3 pull = anchor.subtract(player.position()).normalize().scale(2.1).add(0, 0.38, 0);
                player.setDeltaMovement(pull);
                player.hurtMarked = true;
                player.fallDistance = 0;
                return true;
            }
    ));

    public static final CustomAbility WIND_DASH = register(new CustomAbility(
            "wind_dash", "Wind Dash",
            "Propels the wielder forward on a gale current while launching nearby enemies skyward.",
            "slot_1", 80, ActivationHand.MAIN_HAND, ElementRegistry.WIND,
            CustomSoundManager.WIND_DASH, 5.0, 80,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                Vec3 look = player.getLookAngle();
                player.setDeltaMovement(look.x * 1.9, Math.max(0.45, look.y * 1.1), look.z * 1.9);
                player.hurtMarked = true;
                player.fallDistance = 0;
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 80, 1));
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0));

                CustomParticleManager.spawnRing(level, player.position(), ParticleTypes.CLOUD, 3.0, 20);
                AABB area = player.getBoundingBox().inflate(4.0);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                    target.setDeltaMovement(target.getDeltaMovement().add(0, 0.75, 0));
                    target.hurtMarked = true;
                    target.hurtServer(level, player.damageSources().flyIntoWall(), 6.0f);
                }
                return true;
            }
    ));

    public static final CustomAbility POISON_STRIKE = register(new CustomAbility(
            "poison_strike", "Poison Strike",
            "Executes a venomous fang thrust that inflicts potent Poison II and Oozing.",
            "slot_1", 90, ActivationHand.MAIN_HAND, ElementRegistry.POISON,
            CustomSoundManager.POISON_STRIKE, 5.0, 120,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                Vec3 front = player.getEyePosition().add(player.getLookAngle().scale(2.5));
                CustomParticleManager.spawnBurst(level, front, ParticleTypes.ITEM_SLIME, 25, 0.8, 0.1);

                AABB area = player.getBoundingBox().expandTowards(player.getLookAngle().scale(4.5)).inflate(1.5);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                    target.hurtServer(level, player.damageSources().magic(), 7.5f);
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 140, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.OOZING, 140, 0));
                }
                return true;
            }
    ));

    public static final CustomAbility LIGHT_BURST = register(new CustomAbility(
            "light_burst", "Light Burst",
            "Detonates a solar nova that heals allies and sears hostile creatures with radiant light.",
            "slot_1", 150, ActivationHand.BOTH, ElementRegistry.LIGHT,
            CustomSoundManager.LIGHT_BURST, 7.0, 100,
            ctx -> true,
            ctx -> {
                ServerPlayer player = ctx.player();
                ServerLevel level = ctx.level();
                player.heal(6.0f);
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                CustomParticleManager.spawnRing(level, player.position().add(0, 0.5, 0), ParticleTypes.END_ROD, 5.0, 32);
                CustomParticleManager.spawnBurst(level, player.getEyePosition(), ParticleTypes.END_ROD, 20, 0.5, 0.15);

                AABB area = player.getBoundingBox().inflate(7.0, 3.0, 7.0);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                    target.hurtServer(level, player.damageSources().magic(), 9.0f);
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                }
                return true;
            }
    ));

    public static CustomAbility register(CustomAbility ability) {
        if (!AssetValidator.validateAbility(ability)) {
            return null;
        }
        if (ABILITIES.containsKey(ability.id())) {
            CustomArsenalMod.LOGGER.warn("Duplicate CustomAbility ID '{}' ignored.", ability.id());
            return ABILITIES.get(ability.id());
        }
        ABILITIES.put(ability.id(), ability);
        return ability;
    }

    public static CustomAbility get(String id) {
        return ABILITIES.get(id);
    }

    public static Collection<CustomAbility> getAll() {
        return Collections.unmodifiableCollection(ABILITIES.values());
    }

    public static int size() {
        return ABILITIES.size();
    }

    public static void init() {
    }
}
