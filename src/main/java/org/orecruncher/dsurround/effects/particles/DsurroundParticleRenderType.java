package org.orecruncher.dsurround.effects.particles;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class DsurroundParticleRenderType implements ParticleRenderType {
    private final ResourceLocation texture;
    private final VertexFormat format;

    public DsurroundParticleRenderType(ResourceLocation texture) {
        this(texture, DefaultVertexFormat.PARTICLE);
    }

    public DsurroundParticleRenderType(ResourceLocation texture, VertexFormat format) {
        this.texture = texture;
        this.format = format;
    }

    @Override
    public void begin(BufferBuilder buffer, TextureManager textureManager) {
        textureManager.bindForSetup(this.texture);
        buffer.begin(VertexFormat.Mode.QUADS, this.format);
    }

    @Override
    public void end(Tesselator tesselator) {
        tesselator.getBuilder().end();
        tesselator.end();
    }

    public VertexFormat getVertexFormat() {
        return this.format;
    }

    @Override
    public String toString() {
        return this.texture.toString();
    }
}