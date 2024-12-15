package org.orecruncher.dsurround.lib.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class ResourceUtils {
    private ResourceUtils() {
    }

    @NotNull
    public static ResourceLocation createPath(@NotNull final String namespace, @NotNull final String path) {
        return ResourceLocation.of(namespace, path);
    }

    @NotNull
    public static ResourceLocation createPath(@NotNull final String path) {
        return ResourceLocation.tryParse(path);
    }

    @NotNull
    public static String convertPath(@NotNull final String path) {
        return path.contains(":") ? path : "minecraft:" + path;
    }
} 