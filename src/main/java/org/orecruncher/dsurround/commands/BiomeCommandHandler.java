package org.orecruncher.dsurround.commands;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.orecruncher.dsurround.config.libraries.IBiomeLibrary;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.scripting.Script;

public class BiomeCommandHandler {

    public static Component execute(ResourceLocation biomeIdentifier, String script) {
        var registryAccess = GameUtils.getWorld().registryAccess();
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        
        var biome = biomeRegistry.getOptional(biomeIdentifier);
        if (biome.isEmpty()) {
            return Component.translatable("dsurround.command.dsbiome.failure.unknown_biome", biomeIdentifier.toString());
        }
        
        var result = ContainerManager.resolve(IBiomeLibrary.class).eval(biome.get(), new Script(script));
        return Component.literal(result.toString());
    }
}
