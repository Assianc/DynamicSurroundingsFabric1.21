package org.orecruncher.dsurround.config;

import net.minecraft.resources.ResourceLocation;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.lib.util.ResourceUtils;

public enum SyntheticBiome {
    UNDERGROUND("underground", BiomeTraits.UNDERGROUND),
    PLAYER("player", BiomeTraits.PLAYER),
    VILLAGE("village", BiomeTraits.VILLAGE),
    NONE("none", BiomeTraits.NONE),
    UNDER_RIVER("under_river", BiomeTraits.UNDER_RIVER),
    UNDER_OCEAN("under_ocean", BiomeTraits.UNDER_OCEAN),
    UNDER_DEEP_OCEAN("under_deep_ocean", BiomeTraits.UNDER_DEEP_OCEAN),
    UNDER_WATER("under_water", BiomeTraits.UNDER_WATER),
    INSIDE("inside", BiomeTraits.INSIDE),
    SPACE("space", BiomeTraits.SPACE),
    CLOUDS("clouds", BiomeTraits.CLOUDS);

    private final ResourceLocation id;
    private final String name;
    private final BiomeTraits traits;

    SyntheticBiome(String name, BiomeTraits traits) {
        this.name = name;
        this.traits = traits;
        this.id = ResourceUtils.createResourceLocation(Constants.MOD_ID, String.format("synthetic_biome/%s", name));
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BiomeTraits getTraits() {
        return this.traits;
    }
} 