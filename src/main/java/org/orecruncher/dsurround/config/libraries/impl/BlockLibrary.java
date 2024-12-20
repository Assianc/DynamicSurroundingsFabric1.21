package org.orecruncher.dsurround.config.libraries.impl;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.orecruncher.dsurround.config.block.BlockConfigRule;
import org.orecruncher.dsurround.config.libraries.IBlockLibrary;
import org.orecruncher.dsurround.config.libraries.IReloadEvent;
import org.orecruncher.dsurround.config.libraries.ITagLibrary;
import org.orecruncher.dsurround.lib.registry.RegistryUtils;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.lib.resources.ResourceUtilities;
import org.orecruncher.dsurround.mixinutils.IBlockStateExtended;
import org.orecruncher.dsurround.lib.GameUtils;

import java.util.*;
import java.util.stream.Stream;

public final class BlockLibrary implements IBlockLibrary {
    private static final String FILE_NAME = "blocks.json";
    private static final Codec<List<BlockConfigRule>> CODEC = Codec.list(BlockConfigRule.CODEC);

    private final IModLog logger;
    private final ITagLibrary tagLibrary;
    private final ObjectArray<BlockConfigRule> blockConfigs = new ObjectArray<>(64);
    private int version = 0;
    private final Map<ResourceLocation, BlockInfo> blocks = new HashMap<>();

    public BlockLibrary(IModLog logger, ITagLibrary tagLibrary) {
        this.logger = logger;
        this.tagLibrary = tagLibrary;
    }

    @Override
    public void reload(ResourceUtilities resourceUtilities, IReloadEvent.Scope scope) {
        if (scope == IReloadEvent.Scope.TAGS) {
            this.version++;
            this.logger.info("[BlockLibrary] received tag update notification; version is now %d", this.version);
            return;
        }

        this.blockConfigs.clear();
        var findResults = resourceUtilities.findModResources(CODEC, FILE_NAME);
        findResults.forEach(result -> this.blockConfigs.addAll(result.resourceContent()));

        this.version++;
        this.logger.info("[BlockLibrary] %d block configs loaded; version is now %d", this.blockConfigs.size(), this.version);
    }

    @Override
    public BlockInfo getBlockInfo(BlockState state) {
        var info = ((IBlockStateExtended)(Object)state).dsurround_getBlockInfo();
        if (info != null && info.getVersion() == this.version)
            return info;

        var id = getBlockId(state.getBlock());
        var result = new BlockInfo(this.version, state);

        for (var rule : this.blockConfigs) {
            try {
                if (matches(state, rule)) {
                    result.update(rule);
                }
            } catch (Throwable t) {
                this.logger.error(t, "Unable to process block config rule [%s] for block %s", rule.toString(), id.toString());
            }
        }

        ((IBlockStateExtended)(Object)state).dsurround_setBlockInfo(result);
        return result;
    }

    private boolean matches(BlockState state, BlockConfigRule rule) {
        var block = state.getBlock();
        var blockId = getBlockId(block);

        if (rule.selector().equals(blockId.toString()))
            return true;

        return this.tagLibrary.is(TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.tryParse(rule.selector())), state);
    }

    private static ResourceLocation getBlockId(Block block) {
        return RegistryUtils.getRegistryEntry(BuiltInRegistries.BLOCK, block)
                .flatMap(holder -> holder.unwrapKey())
                .map(key -> key.location())
                .orElseThrow(() -> new IllegalStateException("Cannot get block ID"));
    }

    @Override
    public Stream<String> dump() {
        return GameUtils.getWorld()
                .map(world -> world.registryAccess().registryOrThrow(BuiltInRegistries.BLOCK))
                .map(registry -> registry.entrySet())
                .orElseGet(Collections::emptySet)
                .stream()
                .map(kvp -> formatBlockOutput(kvp.getKey().location(), kvp.getValue()))
                .sorted();
    }

    @Override
    public Stream<String> dumpBlocks(boolean detailed) {
        return dump();
    }

    @Override
    public Stream<String> dumpBlockConfigRules() {
        return this.blockConfigs.stream().map(Object::toString);
    }

    private String formatBlockOutput(ResourceLocation id, Block block) {
        var tags = RegistryUtils.getRegistryEntry(BuiltInRegistries.BLOCK, block)
                .map(e -> {
                    var t = this.tagLibrary.streamTags(e);
                    return this.tagLibrary.asString(t);
                })
                .orElse("null");

        return id.toString() + "\nTags: " + tags + "\n";
    }

    @Override
    public void clear() {
        this.blocks.clear();
    }

    @Override
    public void reload() {
        clear();
        for (var block : BuiltInRegistries.BLOCK) {
            var blockId = RegistryUtils.getBlockId(block);
            registerBlock(blockId, block);
        }
    }

    @Override
    public Optional<BlockInfo> get(ResourceLocation id) {
        return Optional.ofNullable(this.blocks.get(id));
    }

    @Override
    public Collection<BlockInfo> getBlocks() {
        return this.blocks.values();
    }

    @Override
    public void registerBlock(ResourceLocation id, Block block) {
        if (!this.blocks.containsKey(id)) {
            var info = new BlockInfo(id);
            this.blocks.put(id, info);
        }
    }

    @Override
    public void dumpBlockStates() {
        var blockStates = new ArrayList<String>();
        for (var block : BuiltInRegistries.BLOCK) {
            var blockId = RegistryUtils.getBlockId(block);
            var states = block.getStateDefinition().getPossibleStates();
            for (var state : states) {
                blockStates.add(String.format("%s[%s]", blockId, getPropertyString(state)));
            }
        }
        Collections.sort(blockStates);
        for (var state : blockStates) {
            System.out.println(state);
        }
    }

    private String getPropertyString(BlockState state) {
        var properties = new ArrayList<String>();
        for (var property : state.getProperties()) {
            properties.add(String.format("%s=%s", property.getName(), state.getValue(property)));
        }
        Collections.sort(properties);
        return String.join(",", properties);
    }
}
