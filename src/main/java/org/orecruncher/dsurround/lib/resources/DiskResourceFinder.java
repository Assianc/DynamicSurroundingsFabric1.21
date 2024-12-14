package org.orecruncher.dsurround.lib.resources;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.lib.platform.IPlatform;
import org.orecruncher.dsurround.lib.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.orecruncher.dsurround.Configuration.Flags.RESOURCE_LOADING;

public class DiskResourceFinder extends AbstractResourceFinder {

    private final Path root;
    private final String namespace;
    private final String prefix;
    private final Collection<Path> namespacesOnDisk = new ArrayList<>();

    public DiskResourceFinder(IModLog logger, IPlatform platform, Path root, String namespace, String prefix) {
        super(logger);
        this.root = root;
        this.namespace = namespace;
        this.prefix = prefix;
        
        // Initialize namespacesOnDisk
        try {
            if (Files.exists(root)) {
                Files.list(root)
                    .filter(Files::isDirectory)
                    .forEach(this.namespacesOnDisk::add);
            }
        } catch (IOException e) {
            logger.error(e, "Failed to initialize namespacesOnDisk");
        }
    }

    public Collection<ResourceLocation> findResources(Predicate<ResourceLocation> filter) {
        List<ResourceLocation> result = new ArrayList<>();
        try {
            var rootPath = this.root.resolve(this.namespace).resolve(this.prefix);
            if (!Files.exists(rootPath))
                return result;

            Files.walk(rootPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        var relativePath = rootPath.relativize(path);
                        var assetPath = this.prefix + "/" + relativePath.toString().replace('\\', '/');
                        var location = ResourceUtils.createResourceLocation(this.namespace, assetPath);
                        if (location != null && filter.test(location)) {
                            result.add(location);
                        }
                    });
        } catch (IOException ignored) {
        }
        return result;
    }

    @Override
    public <T> Collection<DiscoveredResource<T>> find(Codec<T> codec, String assetPath) {
        // Fast optimization. The vast majority of users will not have local config information.
        if (this.namespacesOnDisk.isEmpty())
            return ImmutableList.of();

        Collection<DiscoveredResource<T>> result = new ObjectArray<>();

        var fileName = assetPath;
        if (!fileName.endsWith(".json"))
            fileName = fileName + ".json";

        // Namespaces on disk should have been collected/pruned so what remains
        // is what needs to be checked.
        for (var path : this.namespacesOnDisk) {
            var filePath = path.resolve(fileName);
            if (Files.exists(filePath)) {
                this.logger.debug(RESOURCE_LOADING, "[%s] - Processing %s file from disk", assetPath, filePath.toString());
                var namespace = path.getFileName().toString();
                var location = ResourceUtils.createResourceLocation(namespace, assetPath);
                if (location != null) {
                    try {
                        var content = Files.readString(filePath);
                        this.decode(location, content, codec).ifPresent(e -> result.add(new DiscoveredResource<>(namespace, e)));
                        this.logger.debug(RESOURCE_LOADING, "[%s] - Completed decode of %s", assetPath, filePath);
                    } catch (Throwable t) {
                        this.logger.error(t, "[%s] Unable to read resource stream for path %s", assetPath, location);
                    }
                }
            }
        }

        return result;
    }
}