package org.orecruncher.dsurround.effects.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WaterRippleParticle extends TextureSheetParticle {
    private final float scale;
    private float scaledWidth;

    protected WaterRippleParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);

        this.lifetime = 12;
        this.gravity = 0;
        this.hasPhysics = false;
        this.scale = 0.3F;

        if (this.random.nextInt(4) == 0) {
            this.scale = 0.0F;
        } else {
            this.scale = 1.0F;
        }

        this.setSize(0.2F, 0.2F);
        this.setColor(1.0F, 1.0F, 1.0F);
        this.setAlpha(0.4F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float ageRatio = (float) this.age / (float) this.lifetime;
            this.quadSize = Mth.lerp(ageRatio, 0.0F, this.scale);
            this.alpha = 0.4F * (1.0F - ageRatio);
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        this.scaledWidth = this.quadSize * 0.5F;

        Quaternionf rotation = new Quaternionf().rotateX((float) Math.PI * 0.5F);
        Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-this.scaledWidth, 0.0F, this.scaledWidth),
            new Vector3f(this.scaledWidth, 0.0F, this.scaledWidth),
            new Vector3f(this.scaledWidth, 0.0F, -this.scaledWidth),
            new Vector3f(-this.scaledWidth, 0.0F, -this.scaledWidth)
        };

        for (Vector3f vertex : vertices) {
            vertex.rotate(rotation);
            vertex.add(x, y, z);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        int light = this.getLightColor(partialTicks);
        vertexConsumer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
            .uv(u0, v1)
            .color(this.rCol, this.gCol, this.bCol, this.alpha)
            .uv2(light)
            .endVertex();
        vertexConsumer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
            .uv(u1, v1)
            .color(this.rCol, this.gCol, this.bCol, this.alpha)
            .uv2(light)
            .endVertex();
        vertexConsumer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
            .uv(u1, v0)
            .color(this.rCol, this.gCol, this.bCol, this.alpha)
            .uv2(light)
            .endVertex();
        vertexConsumer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
            .uv(u0, v0)
            .color(this.rCol, this.gCol, this.bCol, this.alpha)
            .uv2(light)
            .endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleSheets.WATER_RIPPLE;
    }
}
