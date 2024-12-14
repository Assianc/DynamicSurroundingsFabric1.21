package org.orecruncher.dsurround.config.libraries.impl;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.orecruncher.dsurround.config.SyntheticBiome;
import org.orecruncher.dsurround.config.biome.BiomeInfo;
import org.orecruncher.dsurround.config.biome.biometraits.BiomeTraits;
import org.orecruncher.dsurround.config.data.BiomeConfigRule;
import org.orecruncher.dsurround.config.libraries.IBiomeLibrary;
import org.orecruncher.dsurround.config.libraries.IReloadEvent;
import org.orecruncher.dsurround.lib.Guard;
import org.orecruncher.dsurround.lib.registry.RegistryUtils;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.lib.resources.ResourceUtilities;
import org.orecruncher.dsurround.lib.scripting.Script;
import org.orecruncher.dsurround.runtime.BiomeConditionEvaluator;
import org.orecruncher.dsurround.mixinutils.IBiomeExtended;
import org.orecruncher.dsurround.lib.GameUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class BiomeLibrary implements IBiomeLibrary {

    private static final String FILE_NAME = "biomes.json";
    private static final Codec<List<BiomeConfigRule>> CODEC = Codec.list(BiomeConfigRule.CODEC);

    private final IModLog logger;
    private final BiomeConditionEvaluator biomeConditionEvaluator;

    private final Map<SyntheticBiome, BiomeInfo> internalBiomes = new EnumMap<>(SyntheticBiome.class);

    private final ObjectArray<BiomeConfigRule> biomeConfigs = new ObjectArray<>(64);

    private int version = 0;

    public BiomeLibrary(IModLog logger) {
        this.logger = logger;
        this.biomeConditionEvaluator = new BiomeConditionEvaluator(this, logger);
    }

    @Override
    public void reload(ResourceUtilities resourceUtilities, IReloadEvent.Scope scope) {
        this.version++;

        if (scope == IReloadEvent.Scope.TAGS) {
            this.logger.info("[BiomeLibrary] received tag update notification; version is now %d", this.version);
            return;
        }

        this.internalBiomes.clear();
        this.biomeConfigs.clear();
        this.biomeConditionEvaluator.reset();

        var findResults = resourceUtilities.findModResources(CODEC, FILE_NAME);
        findResults.forEach(result -> this.biomeConfigs.addAll(result.resourceContent()));

        this.biomeConfigs.sort(Comparator.comparingInt(BiomeConfigRule::priority));

        for (var b : SyntheticBiome.values())
            initializeSyntheticBiome(b);

        this.logger.info("[BiomeLibrary] %d biome configs loaded; version is now %d", this.biomeConfigs.size(), this.version);
    }

    private void initializeSyntheticBiome(SyntheticBiome biome) {
        String match = "@" + biome.getName();
        var traits = BiomeTraits.createFrom(biome.getTraits());
        var info = new BiomeInfo(this.version, biome.getId(), biome.getName(), traits);

        for (var c : this.biomeConfigs) {
            if (c.biomeSelector().asString().equalsIgnoreCase(match)) {
                info.update(c);
            }
        }

        this.internalBiomes.put(biome, info);
    }

    private static Registry<Biome> getActiveRegistry() {
        return GameUtils.getWorld()
                .map(world -> world.registryAccess().registryOrThrow(Registries.BIOME))
                .orElseThrow();
    }

    @Override
    public BiomeInfo getBiomeInfo(Biome biome) {
        var info = ((IBiomeExtended) (Object) biome).dsurround_getInfo();
        if (info != null && info.getVersion() == this.version)
            return info;

        ResourceLocation id;
        String name;

        if (info != null) {
            id = info.getBiomeId();
            name = info.getBiomeName();
        } else {
            id = getBiomeId(biome);
            name = getBiomeName(id);
        }

        var traits = BiomeTraits.createFrom(id, biome);

        final var result = new BiomeInfo(this.version, id, name, traits, biome);
        ((IBiomeExtended) (Object) biome).dsurround_setInfo(result);

        this.applyTraits(biome, result);

        Guard.execute(() -> applyRuleConfigs(biome, result));
        return result;
    }

    @Override
    public BiomeInfo getBiomeInfo(SyntheticBiome biome) {
        return this.internalBiomes.get(biome);
    }

    @Override
    public Object eval(Biome biome, Script script) {
        var info = this.getBiomeInfo(biome);
        return this.biomeConditionEvaluator.eval(biome, info, script);
    }

    private void applyTraits(Biome biome, BiomeInfo info) {
        this.getNonSyntheticBiomeRules(rule -> !rule.traits().isEmpty())
                .forEach(rule -> {
                    try {
                        var applies = this.biomeConditionEvaluator.check(biome, info, rule.biomeSelector());
                        if (applies) {
                            info.mergeTraits(rule);
                        }
                    } catch (Exception ex) {
                        this.logger.warn("Unable to apply traits from [%s]", rule.toString());
                    }
                });
    }

    private void applyRuleConfigs(Biome biome, BiomeInfo info) {
        this.getNonSyntheticBiomeRules(rule -> rule.traits().isEmpty())
                .forEach(rule -> {
                    try {
                        var applies = this.biomeConditionEvaluator.check(biome, info, rule.biomeSelector());
                        if (applies) {
                            try {
                                info.update(rule);
                            } catch (final Throwable t) {
                                this.logger.warn("Unable to process biome sound configuration [%s]", rule.toString());
                            }
                        }
                    } catch (Throwable t) {
                        this.logger.error(t, "Unexpected error processing biome %s", info.getBiomeId());
                    }
                });

        info.trim();
    }

    private Stream<BiomeConfigRule> getNonSyntheticBiomeRules(Predicate<BiomeConfigRule> filter) {
        return this.biomeConfigs.stream()
                .filter(c -> !c.biomeSelector().asString().startsWith("@"))
                .filter(filter);
    }

    private static ResourceLocation getBiomeId(Biome biome) {
        return RegistryUtils.getRegistryEntry(Registries.BIOME, biome)
                .flatMap(holder -> holder.unwrapKey())
                .map(key -> key.location())
                .orElseThrow(() -> new IllegalStateException("Cannot get biome ID"));
    }

    @Override
    public String getBiomeName(ResourceLocation id) {
        final String fmt = String.format("biome.%s.%s", id.getNamespace(), id.getPath());
        return Language.getInstance().getOrDefault(fmt);
    }

    @Override
    public Stream<String> dump() {
        var realBiomes = getActiveRegistry()
                .stream()
                .map(this::getBiomeInfo)
                .map(BiomeInfo::toString)
                .sorted();

        var fakeBiomes = this.internalBiomes.values()
                .stream()
                .map(BiomeInfo::toString)
                .sorted();

        return Stream.of(realBiomes, fakeBiomes).flatMap(Function.identity());
    }
}
