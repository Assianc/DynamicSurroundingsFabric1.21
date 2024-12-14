package org.orecruncher.dsurround;

import org.orecruncher.dsurround.lib.events.Event;

public class Configuration {
    private static Configuration INSTANCE;

    public final LoggingConfig logging = new LoggingConfig();
    public final SoundSystemConfig soundSystem = new SoundSystemConfig();
    public final EnhancedSoundsConfig enhancedSounds = new EnhancedSoundsConfig();
    public final SoundOptionsConfig soundOptions = new SoundOptionsConfig();
    public final BlockEffectsConfig blockEffects = new BlockEffectsConfig();
    public final EntityEffectsConfig entityEffects = new EntityEffectsConfig();
    public final FootstepAccentsConfig footstepAccents = new FootstepAccentsConfig();
    public final ParticleTweaksConfig particleTweaks = new ParticleTweaksConfig();
    public final CompassAndClockOptionsConfig compassAndClockOptions = new CompassAndClockOptionsConfig();
    public final OtherOptionsConfig otherOptions = new OtherOptionsConfig();

    public static final Event<Configuration> CONFIG_CHANGED = new Event<>();

    public static Configuration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Configuration();
        }
        return INSTANCE;
    }

    public static class LoggingConfig {
        public boolean enableDebugLogging = false;
        public boolean enableModUpdateChatMessage = true;
        public int traceMask = 0;
    }

    public static class SoundSystemConfig {
        public boolean enableSoundSystem = true;
        public int cullInterval = 20;
        public int backgroundThreadWorkers = 3;
    }

    public static class EnhancedSoundsConfig {
        public boolean enableEnhancedSounds = true;
        public float thunderVolume = 1.0F;
    }

    public static class SoundOptionsConfig {
        public boolean enableBiomeSounds = true;
        public boolean enableFootstepSounds = true;
        public boolean enableWeatherSounds = true;
        public boolean enableWaterSounds = true;
    }

    public static class BlockEffectsConfig {
        public boolean enableBlockEffects = true;
        public boolean enableWaterRipples = true;
        public boolean enableWaterfalls = true;
        public boolean enableSteam = true;
        public boolean enableFireJets = true;
    }

    public static class EntityEffectsConfig {
        public boolean enableEntityEffects = true;
        public boolean enableBowEffect = true;
        public boolean enableBreathEffect = true;
        public boolean enablePotionParticles = true;
    }

    public static class FootstepAccentsConfig {
        public boolean enableFootstepAccents = true;
        public float footstepAccentVolume = 0.5F;
    }

    public static class ParticleTweaksConfig {
        public boolean suppressPlayerParticles = true;
        public boolean suppressPlayerPotionParticles = true;
        public boolean suppressPotionParticles = true;
    }

    public static class CompassAndClockOptionsConfig {
        public boolean enableCompass = true;
        public boolean enableClock = true;
    }

    public static class OtherOptionsConfig {
        public boolean enableLightLevelOverlay = false;
        public boolean enableChunkBorders = false;
    }
}
