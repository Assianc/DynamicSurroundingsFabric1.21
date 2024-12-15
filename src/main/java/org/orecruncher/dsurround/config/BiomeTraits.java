package org.orecruncher.dsurround.config;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.tags.BiomeTags;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public enum BiomeTraits {
    NONE,
    HOT,
    COLD,
    SPARSE,
    DENSE,
    WET,
    DRY,
    SAVANNA,
    CONIFEROUS,
    JUNGLE,
    SPOOKY,
    DEAD,
    LUSH,
    MUSHROOM,
    MAGICAL,
    RARE,
    PLATEAU,
    MODIFIED,
    OCEAN,
    RIVER,
    WATER,
    BEACH,
    VOID,
    UNDERGROUND,
    FOREST,
    MESA,
    PLAINS,
    MOUNTAIN,
    HILLS,
    SWAMP,
    SANDY,
    SNOWY,
    WASTELAND,
    TAIGA,
    NETHER,
    END;

    public static Set<BiomeTraits> createFromBiome(Biome biome) {
        Set<BiomeTraits> traits = EnumSet.noneOf(BiomeTraits.class);

        // Add basic climate traits
        if (biome.getBaseTemperature() > 0.95F) {
            traits.add(HOT);
        } else if (biome.getBaseTemperature() < 0.15F) {
            traits.add(COLD);
        }

        // Add precipitation-based traits
        if (biome.getPrecipitation() == Biome.Precipitation.RAIN) {
            traits.add(WET);
        } else if (biome.getPrecipitation() == Biome.Precipitation.NONE) {
            traits.add(DRY);
        } else if (biome.getPrecipitation() == Biome.Precipitation.SNOW) {
            traits.add(SNOWY);
        }

        // Add special effects based traits
        BiomeSpecialEffects effects = biome.getSpecialEffects();
        if (effects.getFogColor() > 0) {
            traits.add(SPOOKY);
        }

        // Add biome category based traits
        switch (biome.getBiomeCategory()) {
            case BEACH -> traits.add(BEACH);
            case DESERT -> {
                traits.add(HOT);
                traits.add(DRY);
                traits.add(SANDY);
            }
            case EXTREME_HILLS -> traits.add(MOUNTAIN);
            case FOREST -> traits.add(FOREST);
            case ICY -> {
                traits.add(COLD);
                traits.add(SNOWY);
            }
            case JUNGLE -> {
                traits.add(HOT);
                traits.add(WET);
                traits.add(DENSE);
                traits.add(JUNGLE);
            }
            case MESA -> traits.add(MESA);
            case MUSHROOM -> traits.add(MUSHROOM);
            case NETHER -> {
                traits.add(HOT);
                traits.add(DRY);
                traits.add(NETHER);
            }
            case OCEAN -> {
                traits.add(OCEAN);
                traits.add(WATER);
            }
            case PLAINS -> traits.add(PLAINS);
            case RIVER -> {
                traits.add(RIVER);
                traits.add(WATER);
            }
            case SAVANNA -> {
                traits.add(HOT);
                traits.add(SAVANNA);
            }
            case SWAMP -> {
                traits.add(WET);
                traits.add(SWAMP);
            }
            case TAIGA -> {
                traits.add(COLD);
                traits.add(CONIFEROUS);
                traits.add(TAIGA);
            }
            case THE_END -> {
                traits.add(COLD);
                traits.add(DRY);
                traits.add(END);
            }
            case UNDERGROUND -> traits.add(UNDERGROUND);
            case MOUNTAIN -> traits.add(MOUNTAIN);
            case NONE -> traits.add(NONE);
        }

        return traits;
    }

    public static Set<BiomeTraits> createFrom(Collection<BiomeTraits> traits) {
        return EnumSet.copyOf(traits);
    }
}
