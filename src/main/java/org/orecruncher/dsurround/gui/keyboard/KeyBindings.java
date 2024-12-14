package org.orecruncher.dsurround.gui.keyboard;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.gui.overlay.DiagnosticsOverlay;
import org.orecruncher.dsurround.gui.sound.IndividualSoundControlScreen;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.platform.Services;
import org.orecruncher.dsurround.eventing.ClientState;

public class KeyBindings {

    public static final KeyMapping modConfigurationMenu;
    public static final KeyMapping individualSoundConfigBinding;
    public static final KeyMapping diagnosticHud;

    static {
        var platform = Library.PLATFORM;

        var modMenuKey = platform.isModLoaded("modmenu") ? InputConstants.UNKNOWN.getValue() : InputConstants.KEY_EQUALS;
        modConfigurationMenu = new KeyMapping(
                "dsurround.text.keybind.modConfigurationMenu",
                InputConstants.Type.KEYSYM,
                modMenuKey,
                "dsurround.text.keybind.section");

        individualSoundConfigBinding = new KeyMapping(
                "dsurround.text.keybind.individualSoundConfig",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                "dsurround.text.keybind.section");

        diagnosticHud = new KeyMapping(
                "dsurround.text.keybind.diagnosticHud",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                "dsurround.text.keybind.section");

        platform.registerKeyBinding(modConfigurationMenu);
        platform.registerKeyBinding(individualSoundConfigBinding);
        platform.registerKeyBinding(diagnosticHud);
    }

    public static void register() {
        ClientState.TICK_END.register(KeyBindings::handleMenuKeyPress);
    }

    private static void handleMenuKeyPress(Minecraft client) {
        if (GameUtils.getCurrentScreen().isPresent() || GameUtils.getPlayer().isEmpty())
            return;

        if (modConfigurationMenu.consumeClick()) {
            var factory = Services.PLATFORM.getModConfigScreenFactory(Configuration.class);
            if (factory.isPresent()) {
                GameUtils.setScreen(factory.get().create(GameUtils.getMC(), null));
            }
        } else if (individualSoundConfigBinding.consumeClick()) {
            GameUtils.setScreen(new IndividualSoundControlScreen(null, true));
        } else if (diagnosticHud.consumeClick()) {
            DiagnosticsOverlay.get().toggleDisplay();
        }
    }
}
