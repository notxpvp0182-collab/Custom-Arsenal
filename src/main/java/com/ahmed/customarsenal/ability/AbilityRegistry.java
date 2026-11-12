package com.ahmed.customarsenal.ability;

import com.ahmed.customarsenal.CustomArsenalMod;
import com.ahmed.customarsenal.element.CustomElement;
import com.ahmed.customarsenal.util.AssetValidator;
import com.ahmed.customarsenal.util.CustomParticleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public final class AbilityRegistry {
    private static final Map<String, CustomAbility> ABILITIES = new LinkedHashMap<>();

    public static List<CustomAbility> createWeaponKit(
            String itemPrefix,
            CustomElement element,
            String shiftLeftName, int shiftLeftCd, String shiftLeftType, float shiftLeftPower,
            String rightName, int rightCd, String rightType, float rightPower,
            String shiftRightName, int shiftRightCd, String shiftRightType, float shiftRightPower,
            String ultName, int ultCd, String ultType, float ultPower
    ) {
        List<CustomAbility> kit = new ArrayList<>(4);
        kit.add(buildAbility(itemPrefix + "_sleft", shiftLeftName, AbilityTrigger.SHIFT_LEFT_CLICK, shiftLeftCd, element, shiftLeftType, shiftLeftPower));
        kit.add(buildAbility(itemPrefix + "_right", rightName, AbilityTrigger.RIGHT_CLICK, rightCd, element, rightType, rightPower));
        kit.add(buildAbility(itemPrefix + "_sright", shiftRightName, AbilityTrigger.SHIFT_RIGHT_CLICK, shiftRightCd, element, shiftRightType, shiftRightPower));
        kit.add(buildAbility(itemPrefix + "_ult", ultName, AbilityTrigger.ULTIMATE_KEY, ultCd, element, ultType, ultPower));
        return kit;
    }

    private static CustomAbility buildAbility(
            String id,
            String displayName,
            AbilityTrigger trigger,
            int cooldownTicks,
            CustomElement element,
            String archetype,
            float power
    ) {
        return register(new CustomAbility(
                id,
                displayName,
                trigger.getShortLabel(),
                trigger.getSlotId(),
                cooldownTicks,
                ActivationHand.MAIN_HAND,
                element,
                element.resolveSound(),
                12.0,
                60,
                ctx -> true,
                ctx -> executeArchetype(ctx, element, archetype, power)
        ));
    }

    private static boolean executeArchetype(AbilityContext ctx, CustomElement element, String archetype, float power) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        Vec3 look = player.getLookAngle();
        Vec3 eye = player.getEyePosition();
        Vec3 pos = player.position();

        switch (archetype) {
            case "SLASH" -> {
                for (int i = 1; i <= 5; i++) {
                    Vec3 p = eye.add(look.scale(i * 1.1));
                    CustomParticleManager.spawnBurst(level, p, element.particle(), 8, 0.5, 0.05);
                    CustomParticleManager.spawnBurst(level, p, ParticleTypes.SWEEP_ATTACK, 1, 0.1, 0.0);
                }
                AABB box = player.getBoundingBox().expandTowards(look.scale(6.0)).inflate(1.8);
                for (LivingEntity t : getTargets(level, player, box)) {
                    damageAndApplyElement(level, player, t, element, power);
                    t.knockback(0.55, -look.x, -look.z);
                }
                return true;
            }
            case "DASH" -> {
                Vec3 vel = new Vec3(look.x * 1.95, Math.max(0.32, look.y * 0.85), look.z * 1.95);
                player.setDeltaMovement(vel);
                player.hurtMarked = true;
                player.fallDistance = 0;
                CustomParticleManager.spawnSpiral(level, pos, element.particle(), 1.4, 2.0, 20);
                AABB box = player.getBoundingBox().expandTowards(look.scale(5.5)).inflate(1.5);
                for (LivingEntity t : getTargets(level, player, box)) {
                    damageAndApplyElement(level, player, t, element, power);
                }
                return true;
            }
            case "TELEPORT" -> {
                Vec3 end = eye.add(look.scale(15.0));
                BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                Vec3 dest = hit.getType() != HitResult.Type.MISS ? hit.getLocation().subtract(look.scale(0.8)) : end;
                CustomParticleManager.spawnLine(level, eye, dest, element.particle(), 22);
                player.teleportTo(dest.x, dest.y, dest.z);
                player.fallDistance = 0;
                CustomParticleManager.spawnBurst(level, dest, ParticleTypes.REVERSE_PORTAL, 28, 0.5, 0.12);
                AABB box = AABB.ofSize(dest, 5.0, 4.0, 5.0);
                for (LivingEntity t : getTargets(level, player, box)) {
                    damageAndApplyElement(level, player, t, element, power);
                }
                return true;
            }
            case "WAVE" -> {
                CustomParticleManager.spawnRing(level, pos, element.particle(), 3.0, 22);
                CustomParticleManager.spawnRing(level, pos, element.particle(), 6.0, 36);
                AABB box = player.getBoundingBox().inflate(6.5, 2.5, 6.5);
                for (LivingEntity t : getTargets(level, player, box)) {
                    damageAndApplyElement(level, player, t, element, power);
                    Vec3 push = t.position().subtract(pos).normalize();
                    t.knockback(0.95, -push.x, -push.z);
                }
                return true;
            }
            case "SLAM" -> {
                CustomParticleManager.spawnRing(level, pos, ParticleTypes.CAMPFIRE_COSY_SMOKE, 4.5, 28);
                CustomParticleManager.spawnBurst(level, pos.add(0, 0.3, 0), element.particle(), 35, 1.8, 0.12);
                AABB box = player.getBoundingBox().inflate(6.0, 3.0, 6.0);
                for (LivingEntity t : getTargets(level, player, box)) {
                    damageAndApplyElement(level, player, t, element, power);
                    t.setDeltaMovement(t.getDeltaMovement().add(0, 0.68, 0));
                    t.hurtMarked = true;
                }
                return true;
            }
            case "PROJECTILE" -> {
                Vec3 targetPos = eye.add(look.scale(20.0));
                BlockHitResult hit = level.clip(new ClipContext(eye, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                Vec3 impact = hit.getLocation();
                CustomParticleManager.spawnLine(level, eye.add(0, -0.15, 0), impact, element.particle(), 26);
                CustomParticleManager.spawnBurst(level, impact, element.particle(), 20, 0.5, 0.1);

                AABB box = player.getBoundingBox().expandTowards(look.scale(18.0)).inflate(1.8);
                for (LivingEntity t : getTargets(level, player, box)) {
                    damageAndApplyElement(level, player, t, element, power);
                    CustomParticleManager.spawnBurst(level, t.position().add(0, 1.0, 0), element.particle(), 16, 0.4, 0.08);
                }
                return true;
            }
            case "ARROW_BARRAGE" -> {
                for (int i = -1; i <= 1; i++) {
                    Vec3 dir = look.yRot(i * 0.14f);
                    Vec3 beamEnd = eye.add(dir.scale(22.0));
                    CustomParticleManager.spawnLine(level, eye.add(0, -0.1, 0), beamEnd, element.particle(), 22);
                    Entity arrow = EntityType.ARROW.create(level, EntitySpawnReason.TRIGGERED);
                    if (arrow != null) {
                        arrow.setPos(eye.x + dir.x, eye.y - 0.1 + dir.y, eye.z + dir.z);
                        arrow.setDeltaMovement(dir.scale(2.8));
                        if ("fire".equals(element.id())) {
                            arrow.igniteForSeconds(5.0f);
                        }
                        level.addFreshEntity(arrow);
                    }
                }
                AABB box = player.getBoundingBox().expandTowards(look.scale(20.0)).inflate(2.5);
                for (LivingEntity t : getTargets(level, player, box)) {
                    damageAndApplyElement(level, player, t, element, power);
                }
                return true;
            }
            case "SPEAR_THROW" -> {
                Vec3 end = eye.add(look.scale(22.0));
                CustomParticleManager.spawnLine(level, eye, end, element.particle(), 30);
                AABB box = player.getBoundingBox().expandTowards(look.scale(22.0)).inflate(1.6);
                for (LivingEntity t : getTargets(level, player, box)) {
                    damageAndApplyElement(level, player, t, element, power);
                    if ("lightning".equals(element.id())) {
                        spawnLightning(level, player, t.position());
                    }
                }
                return true;
            }
            case "VORTEX" -> {
                Vec3 center = eye.add(look.scale(5.0));
                CustomParticleManager.spawnSpiral(level, center.subtract(0, 1.0, 0), element.particle(), 3.2, 3.0, 32);
                AABB box = AABB.ofSize(center, 8.5, 6.0, 8.5);
                for (LivingEntity t : getTargets(level, player, box)) {
                    Vec3 pull = center.subtract(t.position()).normalize().scale(0.8);
                    t.setDeltaMovement(t.getDeltaMovement().add(pull));
                    t.hurtMarked = true;
                    damageAndApplyElement(level, player, t, element, power);
                }
                return true;
            }
            case "SHIELD_BASTION" -> {
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 160, 2));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 160, 3));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                CustomParticleManager.spawnSpiral(level, pos, element.particle(), 2.2, 2.5, 28);
                AABB box = player.getBoundingBox().inflate(5.0);
                for (LivingEntity t : getTargets(level, player, box)) {
                    Vec3 push = t.position().subtract(pos).normalize();
                    t.knockback(1.3, -push.x, -push.z);
                    damageAndApplyElement(level, player, t, element, power * 0.6f);
                }
                return true;
            }
            case "GRAPPLE" -> {
                Vec3 end = eye.add(look.scale(30.0));
                BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                if (hit.getType() == HitResult.Type.MISS) return false;
                Vec3 anchor = hit.getLocation();
                CustomParticleManager.spawnLine(level, eye, anchor, ParticleTypes.CRIT, 28);
                Vec3 pull = anchor.subtract(pos).normalize().scale(2.25).add(0, 0.42, 0);
                player.setDeltaMovement(pull);
                player.hurtMarked = true;
                player.fallDistance = 0;
                return true;
            }
            case "ULTIMATE_CATACLYSM" -> {
                CustomParticleManager.spawnRing(level, pos, element.particle(), 4.0, 28);
                CustomParticleManager.spawnRing(level, pos, element.particle(), 8.0, 42);
                CustomParticleManager.spawnSpiral(level, pos, element.particle(), 3.5, 4.0, 40);

                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 1));
                if ("light".equals(element.id()) || "water".equals(element.id())) {
                    player.heal(8.0f);
                }

                AABB ultArea = player.getBoundingBox().inflate(10.0, 5.0, 10.0);
                for (LivingEntity t : getTargets(level, player, ultArea)) {
                    damageAndApplyElement(level, player, t, element, power);

                    switch (element.id()) {
                        case "lightning" -> spawnLightning(level, player, t.position());
                        case "fire" -> {
                            t.igniteForSeconds(8.0f);
                            BlockPos bp = t.blockPosition();
                            if (level.getBlockState(bp).isAir()) {
                                level.setBlockAndUpdate(bp, Blocks.FIRE.defaultBlockState());
                            }
                        }
                        case "ice" -> {
                            t.setTicksFrozen(300);
                            t.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 160, 3));
                        }
                        case "void", "shadow" -> {
                            Vec3 centerPull = pos.add(look.scale(4.0)).subtract(t.position()).normalize().scale(1.1);
                            t.setDeltaMovement(centerPull.x, 0.55, centerPull.z);
                            t.hurtMarked = true;
                            t.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0));
                        }
                        case "earth", "wind" -> {
                            t.setDeltaMovement(t.getDeltaMovement().add(0, 0.95, 0));
                            t.hurtMarked = true;
                        }
                    }
                    CustomParticleManager.spawnBurst(level, t.position().add(0, 1.0, 0), element.particle(), 22, 0.6, 0.15);
                }
                return true;
            }
            default -> {
                CustomParticleManager.spawnRing(level, pos, element.particle(), 4.0, 24);
                return true;
            }
        }
    }

    private static List<LivingEntity> getTargets(ServerLevel level, ServerPlayer player, AABB area) {
        return level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(e));
    }

    private static void damageAndApplyElement(ServerLevel level, ServerPlayer player, LivingEntity target, CustomElement element, float amount) {
        target.hurtServer(level, player.damageSources().playerAttack(player), amount + element.bonusElementalDamage());
        element.applyElementalHit(target, player);
    }

    private static void spawnLightning(ServerLevel level, ServerPlayer player, Vec3 pos) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (bolt != null) {
            bolt.setPos(pos.x, pos.y, pos.z);
            bolt.setCause(player);
            level.addFreshEntity(bolt);
        }
    }

    public static CustomAbility register(CustomAbility ability) {
        if (!AssetValidator.validateAbility(ability)) {
            return null;
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
