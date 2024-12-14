package org.orecruncher.dsurround.lib.version;

import net.minecraft.network.chat.Component;
import java.util.Optional;

public class VersionChecker implements IVersionChecker {
    @Override
    public Optional<Component> getUpdateMessage() {
        // TODO: Implement version checking logic
        return Optional.empty();
    }
}
