package org.orecruncher.dsurround.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.gui.ColorPalette;
import org.orecruncher.dsurround.lib.util.ResourceUtils;

public class CompassOverlay {
    private static final ResourceLocation COMPASS_TEXTURE = ResourceUtils.createResourceLocation(Constants.MOD_ID, "textures/compass.png");
    private static final float TEXTURE_SIZE = 256F;
    private static final float TEXTURE_HALF = TEXTURE_SIZE / 2F;
    private static final float TEXTURE_QUARTER = TEXTURE_SIZE / 4F;

    private final float width;
    private final float height;
    private final float halfWidth;
    private final float halfHeight;

    public CompassOverlay(float width, float height) {
        this.width = width;
        this.height = height;
        this.halfWidth = width / 2F;
        this.halfHeight = height / 2F;
    }

    public void render(GuiGraphics graphics, float partialTicks) {
        var player = GameUtils.getPlayer().orElse(null);
        if (player == null)
            return;

        var mc = GameUtils.getMC();
        var window = mc.getWindow();

        float angle = 180F - Mth.wrapDegrees(player.getYRot());
        float scale = 1.0F;

        float x = window.getGuiScaledWidth() / 2F;
        float y = 12F;
        float z = 0F;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, COMPASS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, z);
        pose.scale(scale, scale, 1F);

        // Draw the base texture
        drawTexturedRect(pose.last().pose(), -this.halfWidth, -this.halfHeight, this.width, this.height,
                TEXTURE_QUARTER, 0F, TEXTURE_HALF, TEXTURE_QUARTER);

        // Draw the sliding part
        float slideX = -this.halfWidth - (angle / 180F * TEXTURE_HALF / 2F);
        drawTexturedRect(pose.last().pose(), slideX, -this.halfHeight, TEXTURE_SIZE, this.height,
                0F, TEXTURE_QUARTER, TEXTURE_SIZE, TEXTURE_HALF);

        pose.popPose();

        // Draw the pointer
        drawTexturedRect(pose.last().pose(), x - 3F, y - 3F, 6F, 6F,
                TEXTURE_QUARTER + 24F, 0F, TEXTURE_QUARTER + 30F, 6F);
    }

    private static void drawTexturedRect(Matrix4f matrix4f, float x1, float y1, float width, float height,
                                       float u1, float v1, float u2, float v2) {
        float x2 = x1 + width;
        float y2 = y1 + height;
        float z = 0F;

        var bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, x1, y2, z).uv(u1 / TEXTURE_SIZE, v2 / TEXTURE_SIZE).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y2, z).uv(u2 / TEXTURE_SIZE, v2 / TEXTURE_SIZE).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y1, z).uv(u2 / TEXTURE_SIZE, v1 / TEXTURE_SIZE).endVertex();
        bufferBuilder.vertex(matrix4f, x1, y1, z).uv(u1 / TEXTURE_SIZE, v1 / TEXTURE_SIZE).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }
}
