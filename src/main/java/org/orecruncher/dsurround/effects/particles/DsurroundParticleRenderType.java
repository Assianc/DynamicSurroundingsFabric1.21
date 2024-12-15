package org.orecruncher.dsurround.effects.particles;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.systems.RenderSystem;

public class DsurroundParticleRenderType implements ParticleRenderType {
    private final VertexFormat format;
    private final TextureAtlas textureAtlas;

    public DsurroundParticleRenderType(VertexFormat format, TextureAtlas textureAtlas) {
        this.format = format;
        this.textureAtlas = textureAtlas;
    }

    @Override
    public void begin(BufferBuilder buffer, TextureManager textureManager) {
        RenderSystem.setShaderTexture(0, this.textureAtlas.getId());
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }

    @Override
    public void end(BufferBuilder buffer) {
        buffer.end();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    @Override
    public String toString() {
        return "dsurround_particle";
    }
}