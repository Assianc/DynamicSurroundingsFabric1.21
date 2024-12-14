package org.orecruncher.dsurround.mixins.core;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.orecruncher.dsurround.effects.entity.EntityEffectInfo;
import org.orecruncher.dsurround.mixinutils.ILivingEntityExtended;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements ILivingEntityExtended {

    @Unique
    private EntityEffectInfo dsurround_effectInfo;

    @Shadow protected boolean jumping;

    @Shadow @Final private static EntityDataAccessor<Integer> f_20962_;

    protected MixinLivingEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public EntityEffectInfo dsurround_getEffectInfo() {
        return this.dsurround_effectInfo;
    }

    @Override
    public void dsurround_setEffectInfo(@Nullable EntityEffectInfo info) {
        this.dsurround_effectInfo = info;
    }

    @Override
    public boolean dsurround_isJumping() {
        return this.jumping;
    }

    @Override
    public int dsurround_getPotionSwirlColor() {
        var entity = ((LivingEntity)(Object)this);
        return entity.getEntityData().get(f_20962_);
    }

    @Override
    public void dsurround_setPotionSwirlColor(int color) {
        var entity = ((LivingEntity)(Object)this);
        entity.getEntityData().set(f_20962_, color);
    }

    @Inject(method = "m_7254_", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        // Add your custom step sound logic here
    }

    @Inject(method = "m_6469_", at = @At("HEAD"))
    private void onCheckFallDamage(CallbackInfo ci) {
        // Add your custom fall sound logic here
    }
}
