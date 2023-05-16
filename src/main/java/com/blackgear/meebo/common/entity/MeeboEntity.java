package com.blackgear.meebo.common.entity;

import com.blackgear.meebo.common.entity.ai.FollowPlayerGoal;
import com.blackgear.meebo.common.registries.ModDataSerializers;
import com.blackgear.meebo.common.registries.ModSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class MeeboEntity extends Animal {
    private static final EntityDataAccessor<Optional<UUID>> DATA_PLAYER_BOUND = SynchedEntityData.defineId(MeeboEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<State> DATA_STATE = SynchedEntityData.defineId(MeeboEntity.class, ModDataSerializers.MEEBO_STATE.get());
    private static final EntityDataAccessor<Boolean> DATA_RENDER_BOUND = SynchedEntityData.defineId(MeeboEntity.class, EntityDataSerializers.BOOLEAN);
    private static final TargetingConditions MEEBO_TARGETS = TargetingConditions.forCombat().range(6.0D).ignoreLineOfSight().selector(entity -> entity instanceof Enemy);
    private int upsetTime;

    public MeeboEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.entityData.define(DATA_STATE, State.NORMAL);
        this.entityData.define(DATA_PLAYER_BOUND, Optional.empty());
        this.entityData.define(DATA_RENDER_BOUND, true);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(BlockPathTypes.WATER, -2.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25D, false));
        this.goalSelector.addGoal(2, new FollowPlayerGoal(this, 1.25D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Player.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, entity -> entity instanceof Enemy && !(entity instanceof Creeper)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.25F).add(Attributes.ATTACK_DAMAGE, 20.0D);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.getBoundedUUID() != null) {
            tag.putUUID("Bound", this.getBoundedUUID());
        }

        tag.putBoolean("Render", this.getRenderBound());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        UUID uuid;
        if (tag.hasUUID("Bound")) {
            uuid = tag.getUUID("Bound");
        } else {
            uuid = null;
        }

        if (uuid != null) {
            try {
                this.setBoundedUUID(uuid);
            } catch (Throwable ignored) {

            }
        }

        this.setRenderBound(tag.getBoolean("Render"));
    }

    @Nullable
    public UUID getBoundedUUID() {
        return this.entityData.get(DATA_PLAYER_BOUND).orElse(null);
    }

    public void setBoundedUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_PLAYER_BOUND, Optional.ofNullable(uuid));
    }

    public boolean getRenderBound() {
        return this.entityData.get(DATA_RENDER_BOUND);
    }

    public void setRenderBound(boolean render) {
        this.entityData.set(DATA_RENDER_BOUND, render);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide && this.isAlive()) {
            // applies attributes for meebo as entity
            if (!this.isNoAi() && this.getBoundedUUID() == null) {
                if (this.getState() == State.ANGRY || this.getState() == State.SAD) {
                    --this.upsetTime;
                    if (this.upsetTime < 0) {
                        this.transitionTo(State.NORMAL);
                    }
                }

                if (this.getState() != State.ANGRY && this.getState() != State.SAD) {
                    if (!this.level.getNearbyEntities(Mob.class, MEEBO_TARGETS, this, this.getBoundingBox().inflate(6.0D)).isEmpty()) {
                        this.transitionTo(State.SURPRISED);
                    } else {
                        this.transitionTo(State.NORMAL);
                    }
                }
            }

            if (this.getBoundedUUID() != null) {
                Entity entity = this.level.getPlayerByUUID(this.getBoundedUUID());
                if (entity != null) {
                    this.setPos(entity.position());
                }
            }
        }
    }

    @Override
    protected void pushEntities() {
        if (this.getBoundedUUID() == null) {
            super.pushEntities();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.getBoundedUUID() != null && !this.getRenderBound() ? EntityDimensions.fixed(0, 0) : super.getDimensions(pPose);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        this.transitionTo(State.ANGRY);

        if (source.getEntity() != null) {
            if (this.getBoundedUUID() != null && source.getEntity().getUUID() == this.getBoundedUUID()) {
                return false;
            }
        }

        if (source.getEntity() instanceof Player) {
            this.transitionTo(State.SAD);
        }

        return super.hurt(source, amount);
    }

    @Override
    public boolean isInvisible() {
        return super.isInvisible() || !this.getRenderBound() && this.getBoundedUUID() != null;
    }

    public void applyToMorph(Player player) {
        if (this.level != player.level) {
            this.level = player.level;
        }

        this.xo = player.xo;
        this.yo = player.yo;
        this.zo = player.zo;
        this.setPos(player.getX(), player.getY(), player.getZ());
        this.xOld = player.xOld;
        this.yOld = player.yOld;
        this.zOld = player.zOld;

        this.tickCount = player.tickCount;
        this.wasTouchingWater = player.isInWater();
        this.setOnGround(player.isOnGround());
        this.updateRotation(player);

        this.fallDistance = player.fallDistance;
        this.deathTime = player.deathTime;

        this.hurtTime = player.hurtTime;
        this.hurtMarked = player.hurtMarked;

        this.swinging = player.swinging;
        this.swingTime = player.swingTime;

        this.blocksBuilding = player.blocksBuilding;

        if(player.getSleepingPos().isPresent())
            this.setSleepingPos(player.getSleepingPos().get());

        this.fallFlyTicks = player.getFallFlyingTicks();

        this.setPose(player.getPose());
        this.setSwimming(player.isSwimming());

        this.setDeltaMovement(player.getDeltaMovement());
    }

    public void applyPostTick(Player player) {
        this.walkDist = player.walkDist;
        this.walkDistO = player.walkDistO;
        this.attackAnim = player.attackAnim;
        this.oAttackAnim = player.oAttackAnim;
        this.yBodyRot = player.yBodyRot;
        this.yBodyRotO = player.yBodyRotO;
        this.yHeadRot = player.yHeadRot;
        this.yHeadRotO = player.yHeadRotO;
    }

    public void updateRotation(Player player) {
        this.setXRot(player.getXRot());
        this.setYBodyRot(player.yBodyRot);
        this.setYRot(player.getYRot());
        this.xRotO = player.xRotO;
        this.yRotO = player.yRotO;
    }

    @Nullable @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Nullable @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return source.getEntity() instanceof Player ? ModSoundEvents.MEEBO_SAD.get() : ModSoundEvents.MEEBO_HURT.get();
    }

    @Nullable @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.MEEBO_DEATH.get();
    }

    @Nullable @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.MEEBO_IDLE.get();
    }

    public State getState() {
        return this.entityData.get(DATA_STATE);
    }

    private void setState(State state) {
        this.entityData.set(DATA_STATE, state);
    }

    public void copyState(MeeboEntity meebo) {
        this.transitionTo(meebo.getState());
    }

    public void transitionTo(State state) {
        if (state.getSound() != null && this.getState() != state) {
            this.level.playSound(null, this.blockPosition(), state.getSound(), SoundSource.NEUTRAL, this.getSoundVolume(), this.random.nextFloat() * 0.4F + 0.8F);
        }

        if ((state == State.ANGRY || state == State.SAD) && this.getBoundedUUID() == null) {
            this.upsetTime = 100;
        }

        this.setState(state);
    }

    public enum State {
        NORMAL(null),
        ANGRY(ModSoundEvents.MEEBO_ANGRY.get()),
        SURPRISED(ModSoundEvents.MEEBO_SURPRISED.get()),
        BIGEYE(ModSoundEvents.MEEBO_BIGEYE.get()),
        SAD(ModSoundEvents.MEEBO_SAD.get());

        private final @Nullable SoundEvent sound;

        State(@Nullable SoundEvent sound) {
            this.sound = sound;
        }

        public SoundEvent getSound() {
            return this.sound;
        }
    }
}