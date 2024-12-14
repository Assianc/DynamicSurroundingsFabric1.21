package org.orecruncher.dsurround.lib.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class ResourceUtils {
    private ResourceUtils() {}

    @NotNull
    public static ResourceLocation createResourceLocation(@NotNull String namespace, @NotNull String path) {
        return ResourceLocation.tryParse(namespace + ":" + path);
    }

    @NotNull
    public static ResourceLocation createResourceLocation(@NotNull String path) {
        return ResourceLocation.tryParse(path);
    }
} 