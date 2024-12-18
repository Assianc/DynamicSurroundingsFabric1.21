package org.orecruncher.dsurround.lib;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.packs.repository.PackRepository;

import java.util.Objects;
import java.util.Optional;

public final class GameUtils {
    private GameUtils() {
    }

    public static Optional<Player> getPlayer() {
        return Optional.ofNullable(getMC().player);
    }

    public static Optional<ClientLevel> getWorld() {
        return Optional.ofNullable(getMC().level);
    }

    public static Minecraft getMC() {
        return Objects.requireNonNull(Minecraft.getInstance());
    }

    public static Optional<Screen> getCurrentScreen() {
        return Optional.ofNullable(getMC().screen);
    }

    public static void setScreen(Screen screen) {
        getMC().setScreen(screen);
    }

    public static SoundManager getSoundManager() {
        return Objects.requireNonNull(getMC().getSoundManager());
    }

    public static ParticleEngine getParticleManager() {
        return Objects.requireNonNull(getMC().particleEngine);
    }

    public static TextureManager getTextureManager() {
        return Objects.requireNonNull(getMC().getTextureManager());
    }

    public static Font getTextRenderer() {
        return Objects.requireNonNull(getMC().font);
    }

    public static StringSplitter getTextHandler() {
        return Objects.requireNonNull(getMC().font.getSplitter());
    }

    public static Options getGameSettings() {
        return Objects.requireNonNull(getMC().options);
    }

    public static boolean isInGame() {
        return getWorld().isPresent() && getPlayer().isPresent();
    }

    public static boolean isThirdPersonView() {
        return getGameSettings().getCameraType() != CameraType.FIRST_PERSON;
    }

    public static boolean isFirstPersonView() {
        return !isThirdPersonView();
    }

    public static String getServerBrand() {
        var mc = getMC();
        if (mc.player != null && mc.player.connection != null && mc.player.connection.getConnection() != null) {
            return mc.player.connection.getConnection().serverBrand;
        }
        return "Unknown";
    }

    public static PackRepository getResourceManager() {
        return getMC().getResourcePackRepository();
    }

    public static boolean isPaused() {
        return getMC().isPaused();
    }
}