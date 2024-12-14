package org.orecruncher.dsurround.lib;

import net.minecraft.resources.ResourceLocation;
import org.orecruncher.dsurround.lib.util.ResourceUtils;

import java.util.Optional;

public final class IdentityUtils {
    private IdentityUtils() {}

    public static Optional<ResourceLocation> resolveIdentifier(String identifierString) {
        if (identifierString == null || identifierString.isEmpty())
            return Optional.empty();

        try {
            return Optional.ofNullable(ResourceUtils.createResourceLocation(identifierString));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public static Optional<ResourceLocation> resolveIdentifier(String namespace, String path) {
        if (namespace == null || namespace.isEmpty() || path == null || path.isEmpty())
            return Optional.empty();

        try {
            return Optional.ofNullable(ResourceUtils.createResourceLocation(namespace, path));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
