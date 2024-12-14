package org.orecruncher.dsurround.effects.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.orecruncher.dsurround.config.WaterRippleStyle;

public class WaterRippleParticle extends Particle {

    private final ParticleRenderType renderType;
    private final WaterRippleStyle rippleStyle;
    private float texU1;
    private float texU2;
    private float texV1;
    private float texV2;
    private float scaledWidth;

    protected WaterRippleParticle(ClientLevel world, double x, double y, double z, WaterRippleStyle rippleStyle) {
        super(world, x, y, z);

        this.rippleStyle = rippleStyle;
        this.renderType = new DsurroundParticleRenderType(rippleStyle.getTexture());

        this.lifetime = 12;
        this.gravity = 0;
        this.hasPhysics = false;

        this.x = x;
        this.y = y;
        this.z = z;

        this.scaledWidth = 0.0F;
        this.alpha = 1.0F;

        if (rippleStyle.doScaling()) {
            this.quadSize = 0.0F;
        } else {
            this.quadSize = 1.0F;
        }

        this.updateTexture();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return this.renderType;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.updateTexture();
        }
    }

    private void updateTexture() {
        float ageRatio = (float) this.age / (float) this.lifetime;

        if (this.rippleStyle.doScaling()) {
            this.quadSize = Mth.lerp(ageRatio, 0.0F, 1.0F);
        }

        if (this.rippleStyle.doAlpha()) {
            this.alpha = 1.0F - ageRatio;
        }

        this.texU1 = 0.0F;
        this.texU2 = 1.0F;
        this.texV1 = 0.0F;
        this.texV2 = 1.0F;

        this.scaledWidth = this.quadSize * 0.5F;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camera.getPosition().x);
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camera.getPosition().y);
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camera.getPosition().z);

        vertexConsumer.vertex(x - this.scaledWidth, y, z + this.scaledWidth)
                .uv(this.texU1, this.texV2)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();

        vertexConsumer.vertex(x + this.scaledWidth, y, z + this.scaledWidth)
                .uv(this.texU2, this.texV2)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();

        vertexConsumer.vertex(x + this.scaledWidth, y, z - this.scaledWidth)
                .uv(this.texU2, this.texV1)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();

        vertexConsumer.vertex(x - this.scaledWidth, y, z - this.scaledWidth)
                .uv(this.texU1, this.texV1)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final WaterRippleStyle rippleStyle;

        public Provider(WaterRippleStyle style) {
            this.rippleStyle = style;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WaterRippleParticle(world, x, y, z, this.rippleStyle);
        }
    }
}
