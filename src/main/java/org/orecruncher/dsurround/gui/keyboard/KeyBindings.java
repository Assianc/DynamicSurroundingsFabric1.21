package org.orecruncher.dsurround.gui.keyboard;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.gui.sound.IndividualSoundControlScreen;
import org.orecruncher.dsurround.lib.platform.IPlatform;
import org.orecruncher.dsurround.lib.platform.Services;

public class KeyBindings {
    private static final String KEY_CATEGORY = "key.categories.dsurround";

    public static final KeyMapping modConfigurationMenu = new KeyMapping(
            "key.dsurround.modconfiguration",
            GLFW.GLFW_KEY_K,
            KEY_CATEGORY
    );

    public static final KeyMapping individualSoundConfigBinding = new KeyMapping(
            "key.dsurround.soundconfig",
            GLFW.GLFW_KEY_I,
            KEY_CATEGORY
    );

    public static final KeyMapping diagnosticHud = new KeyMapping(
            "key.dsurround.diagnostichud",
            GLFW.GLFW_KEY_L,
            KEY_CATEGORY
    );

    public static void register() {
        IPlatform platform = Services.PLATFORM;
        platform.registerKeyBinding(modConfigurationMenu.getName(), modConfigurationMenu.getKey().getValue(), KEY_CATEGORY);
        platform.registerKeyBinding(individualSoundConfigBinding.getName(), individualSoundConfigBinding.getKey().getValue(), KEY_CATEGORY);
        platform.registerKeyBinding(diagnosticHud.getName(), diagnosticHud.getKey().getValue(), KEY_CATEGORY);
    }

    public static void checkKeys() {
        while (modConfigurationMenu.consumeClick()) {
            var factory = Services.PLATFORM.getModConfigScreenFactory(Configuration.class);
            if (factory != null) {
                Minecraft.getInstance().setScreen(factory.apply(Minecraft.getInstance().screen));
            }
        }

        while (individualSoundConfigBinding.consumeClick()) {
            Minecraft.getInstance().setScreen(new IndividualSoundControlScreen(Minecraft.getInstance().screen));
        }

        while (diagnosticHud.consumeClick()) {
            // Toggle diagnostic HUD
            Configuration.getInstance().logging.enableDebugLogging = !Configuration.getInstance().logging.enableDebugLogging;
        }
    }
}
