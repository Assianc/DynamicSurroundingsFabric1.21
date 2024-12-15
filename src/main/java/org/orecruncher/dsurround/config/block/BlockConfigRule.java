package org.orecruncher.dsurround.config.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.orecruncher.dsurround.lib.util.ResourceUtils;

import java.util.Optional;

public class BlockConfigRule {
    public static final Codec<BlockConfigRule> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("blockId").forGetter(BlockConfigRule::getBlockId),
            Codec.STRING.optionalFieldOf("soundProfile").forGetter(BlockConfigRule::getSoundProfile),
            Codec.BOOL.optionalFieldOf("isOccluder").forGetter(BlockConfigRule::getIsOccluder)
        ).apply(instance, BlockConfigRule::new));

    private final ResourceLocation blockId;
    private final Optional<String> soundProfile;
    private final Optional<Boolean> isOccluder;

    public BlockConfigRule(ResourceLocation blockId, Optional<String> soundProfile, Optional<Boolean> isOccluder) {
        this.blockId = blockId;
        this.soundProfile = soundProfile;
        this.isOccluder = isOccluder;
    }

    public ResourceLocation getBlockId() {
        return this.blockId;
    }

    public Optional<String> getSoundProfile() {
        return this.soundProfile;
    }

    public Optional<Boolean> getIsOccluder() {
        return this.isOccluder;
    }
} 