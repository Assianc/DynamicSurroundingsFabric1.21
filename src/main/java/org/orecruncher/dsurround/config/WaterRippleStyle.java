package org.orecruncher.dsurround.config;

import net.minecraft.resources.ResourceLocation;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.lib.util.ResourceUtils;

public enum WaterRippleStyle {

    NONE(ResourceUtils.createResourceLocation(Constants.MOD_ID, "none"), 0, false, false),
    PIXELATED_CIRCLE(ResourceUtils.createResourceLocation(Constants.MOD_ID, "pixelated_circle"), 16, true, true);

    private final ResourceLocation id;
    private final int maxAge;
    private final boolean doScaling;
    private final boolean doAlpha;

    WaterRippleStyle(ResourceLocation id, int maxAge, boolean doScaling, boolean doAlpha) {
        this.id = id;
        this.maxAge = maxAge;
        this.doScaling = doScaling;
        this.doAlpha = doAlpha;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ResourceLocation getTexture() {
        return ResourceUtils.createResourceLocation(Constants.MOD_ID, "textures/particles/" + this.name().toLowerCase() + ".png");
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public boolean doScaling() {
        return this.doScaling;
    }

    public boolean doAlpha() {
        return this.doAlpha;
    }

    public float getU1(int age) {
        return 0.0F;
    }

    public float getU2(int age) {
        return 1.0F;
    }

    public float getV1(int age) {
        return 0.0F;
    }

    public float getV2(int age) {
        return 1.0F;
    }
}
