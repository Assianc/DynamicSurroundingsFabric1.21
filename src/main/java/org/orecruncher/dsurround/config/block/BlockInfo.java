package org.orecruncher.dsurround.config.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.orecruncher.dsurround.config.data.BlockConfigRule;

public class BlockInfo {
    private final ResourceLocation blockId;
    private final String soundProfile;
    private final boolean isOccluder;

    public BlockInfo(ResourceLocation blockId, String soundProfile, boolean isOccluder) {
        this.blockId = blockId;
        this.soundProfile = soundProfile;
        this.isOccluder = isOccluder;
    }

    public ResourceLocation getBlockId() {
        return this.blockId;
    }

    public String getSoundProfile() {
        return this.soundProfile;
    }

    public boolean isOccluder() {
        return this.isOccluder;
    }

    public static BlockInfo createFrom(BlockState state, BlockConfigRule rule) {
        return new BlockInfo(
            rule.getBlockId(),
            rule.getSoundProfile().orElse("default"),
            rule.getIsOccluder().orElse(false)
        );
    }
}