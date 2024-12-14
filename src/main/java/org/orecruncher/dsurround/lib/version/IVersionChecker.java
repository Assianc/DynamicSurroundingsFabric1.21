package org.orecruncher.dsurround.lib.version;

import net.minecraft.network.chat.Component;
import java.util.Optional;

public interface IVersionChecker {
    Optional<Component> getUpdateMessage();
}
