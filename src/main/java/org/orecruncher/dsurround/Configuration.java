package org.orecruncher.dsurround;

import org.orecruncher.dsurround.lib.events.Event;

public class Configuration {
    private static Configuration INSTANCE;

    public final Logging logging = new Logging();
    public final SoundSystem soundSystem = new SoundSystem();
    public final EnhancedSounds enhancedSounds = new EnhancedSounds();
    public final SoundOptions soundOptions = new SoundOptions();
    public final BlockEffects blockEffects = new BlockEffects();
    public final EntityEffects entityEffects = new EntityEffects();
    public final FootstepAccents footstepAccents = new FootstepAccents();
    public final ParticleTweaks particleTweaks = new ParticleTweaks();
    public final CompassAndClockOptions compassAndClockOptions = new CompassAndClockOptions();
    public final OtherOptions otherOptions = new OtherOptions();

    public static final Event<Configuration> CONFIG_CHANGED = new Event<>();

    public static Configuration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Configuration();
        }
        return INSTANCE;
    }

    public static class Flags {
        public static final int RESOURCE_LOADING = 1;
        public static final int SOUND_PROCESSING = 2;
        public static final int PARTICLE_PROCESSING = 4;
        public static final int BIOME_PROCESSING = 8;
    }

    public static class Logging {
        public boolean enableDebugLogging = false;
        public boolean enableModUpdateChatMessage = true;
        public boolean registerCommands = true;
        public boolean filteredTagView = true;
        public int traceMask = 0;
    }

    public static class SoundSystem {
        public boolean enableSoundSystem = true;
        public int cullInterval = 20;
        public int backgroundThreadWorkers = 3;
    }

    public static class EnhancedSounds {
        public boolean enableEnhancedSounds = true;
        public float thunderVolume = 1.0F;
    }

    public static class SoundOptions {
        public boolean enableBiomeSounds = true;
        public boolean enableFootstepSounds = true;
        public boolean enableWeatherSounds = true;
        public boolean enableWaterSounds = true;
        public float ambientVolumeScaling = 100.0F;
    }

    public static class BlockEffects {
        public boolean enableBlockEffects = true;
        public boolean enableWaterRipples = true;
        public boolean enableWaterfalls = true;
        public boolean enableWaterfallSounds = true;
        public boolean enableSteam = true;
        public boolean enableFireJets = true;
        public boolean flameJetEnabled = true;
        public boolean bubbleColumnEnabled = true;
        public boolean firefliesEnabled = true;
        public boolean steamColumnEnabled = true;
        public boolean enableWaterfallParticles = true;
        public WaterRippleStyle waterRippleStyle = WaterRippleStyle.PIXELATED_CIRCLE;
    }

    public static class EntityEffects {
        public boolean enableEntityEffects = true;
        public boolean enableBowEffect = true;
        public boolean enableBreathEffect = true;
        public boolean enablePotionParticles = true;
        public boolean enableBowPull = true;
        public boolean enablePlayerToolbarEffect = true;
        public boolean enableSwingEffect = true;
        public boolean enableBrushStepEffect = true;
    }

    public static class FootstepAccents {
        public boolean enableFootstepAccents = true;
        public float footstepAccentVolume = 0.5F;
    }

    public static class ParticleTweaks {
        public boolean suppressPlayerParticles = true;
        public boolean suppressPlayerPotionParticles = true;
        public boolean suppressPotionParticles = true;
    }

    public static class CompassAndClockOptions {
        public boolean enableCompass = true;
        public boolean enableClock = true;
    }

    public static class OtherOptions {
        public boolean enableLightLevelOverlay = false;
        public boolean enableChunkBorders = false;
    }
}
