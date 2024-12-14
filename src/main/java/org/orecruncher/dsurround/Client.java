package org.orecruncher.dsurround;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.orecruncher.dsurround.config.libraries.*;
import org.orecruncher.dsurround.config.libraries.impl.*;
import org.orecruncher.dsurround.effects.particles.ParticleSheets;
import org.orecruncher.dsurround.gui.overlay.OverlayManager;
import org.orecruncher.dsurround.gui.keyboard.KeyBindings;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.config.ConfigurationData;
import org.orecruncher.dsurround.lib.di.Container;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.events.HandlerPriority;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.lib.logging.ModLog;
import org.orecruncher.dsurround.eventing.ClientState;
import org.orecruncher.dsurround.lib.resources.ResourceUtilities;
import org.orecruncher.dsurround.lib.version.IVersionChecker;
import org.orecruncher.dsurround.lib.version.VersionChecker;
import org.orecruncher.dsurround.sound.IAudioPlayer;
import org.orecruncher.dsurround.sound.MinecraftAudioPlayer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class Client {

    public static Configuration Config;

    private final IModLog logger;
    private CompletableFuture<Optional<Component>> versionInfo;
    private final Container container;

    public Client() {
        this.logger = Library.LOGGER;

        this.logger.info("[%s] Bootstrapping", Constants.MOD_ID);

        Library.initialize();

        ContainerManager.getRootContainer().registerFactory(SoundManager.class, GameUtils::getSoundManager);

        this.logger.info("[%s] Boostrap completed", Constants.MOD_ID);

        this.container = new Container();

        this.container
                .registerSingleton(IVersionChecker.class, VersionChecker.class)
                .registerSingleton(ITagLibrary.class, TagLibrary.class)
                .registerSingleton(IBiomeLibrary.class, BiomeLibrary.class)
                .registerSingleton(IDimensionLibrary.class, DimensionLibrary.class)
                .registerSingleton(IBlockLibrary.class, BlockLibrary.class)
                .registerSingleton(IItemLibrary.class, ItemLibrary.class)
                .registerSingleton(IEntityEffectLibrary.class, EntityEffectLibrary.class)
                .registerSingleton(IAudioPlayer.class, MinecraftAudioPlayer.class);

        if (Config.logging.enableModUpdateChatMessage) {
            this.versionInfo = CompletableFuture.supplyAsync(() -> {
                try {
                    var checker = ContainerManager.resolve(IVersionChecker.class);
                    return checker.getUpdateMessage();
                } catch (Exception e) {
                    Library.LOGGER.error(e, "Error checking for updates");
                    return Optional.empty();
                }
            });
        } else {
            this.versionInfo = CompletableFuture.completedFuture(Optional.empty());
        }

        KeyBindings.register();

        Library.LOGGER.info("[%s] Client initialization complete", Constants.MOD_ID);
    }

    public void initializeClient() {
        this.logger.info("[%s] Client initializing", Constants.MOD_ID);

        Config = ConfigurationData.getConfig(Configuration.class);
        if (this.logger instanceof ModLog ml) {
            ml.setDebug(Config.logging.enableDebugLogging);
            ml.setTraceMask(Config.logging.traceMask);
        }

        Configuration.CONFIG_CHANGED.register(cfg -> {
            if (cfg instanceof Configuration config) {
                if (this.logger instanceof ModLog ml) {
                    ml.setDebug(config.logging.enableDebugLogging);
                    ml.setTraceMask(config.logging.traceMask);
                }
            }
        });

        Handlers.registerHandlers();

        ClientState.STARTED.register(this::onComplete, HandlerPriority.VERY_HIGH);
        ClientState.ON_CONNECT.register(this::onConnect, HandlerPriority.LOW);

        ContainerManager.getRootContainer()
                .registerSingleton(Config)
                .registerSingleton(Config.logging)
                .registerSingleton(Config.soundSystem)
                .registerSingleton(Config.enhancedSounds)
                .registerSingleton(Config.soundOptions)
                .registerSingleton(Config.blockEffects)
                .registerSingleton(Config.entityEffects)
                .registerSingleton(Config.footstepAccents)
                .registerSingleton(Config.particleTweaks)
                .registerSingleton(Config.compassAndClockOptions)
                .registerSingleton(Config.otherOptions)
                .registerSingleton(IConditionEvaluator.class, ConditionEvaluator.class)
                .registerSingleton(IVersionChecker.class, VersionChecker.class)
                .registerSingleton(ITagLibrary.class, TagLibrary.class)
                .registerSingleton(ISoundLibrary.class, SoundLibrary.class)
                .registerSingleton(IBiomeLibrary.class, BiomeLibrary.class)
                .registerSingleton(IDimensionLibrary.class, DimensionLibrary.class)
                .registerSingleton(IBlockLibrary.class, BlockLibrary.class)
                .registerSingleton(IItemLibrary.class, ItemLibrary.class)
                .registerSingleton(IEntityEffectLibrary.class, EntityEffectLibrary.class)
                .registerSingleton(IAudioPlayer.class, MinecraftAudioPlayer.class)
                .registerSingleton(OverlayManager.class);

        ParticleSheets.register();

        this.logger.info("[%s] Finalization complete", Constants.MOD_ID);
    }

    public void onComplete(Minecraft client) {
        try {
            var versionQueryResult = this.versionInfo.get();
            if (versionQueryResult.isPresent()) {
                var message = versionQueryResult.get();
                Library.LOGGER.info("Update available: %s", message.getString());
                GameUtils.getPlayer().ifPresent(player -> 
                    player.sendSystemMessage(message)
                );
            } else if(Config.logging.enableModUpdateChatMessage) {
                Library.LOGGER.info("The mod version is current");
            }
        } catch (Throwable t) {
            Library.LOGGER.error(t, "Unable to process version information");
        }
    }

    private void onConnect(Minecraft minecraftClient) {
        try {
            var versionQueryResult = this.versionInfo.get();
            if (versionQueryResult.isPresent()) {
                var message = versionQueryResult.get();
                this.logger.info("Update available: %s", message.getString());
                GameUtils.getPlayer().ifPresent(player -> 
                    player.sendSystemMessage(message)
                );
            } else if(Config.logging.enableModUpdateChatMessage) {
                this.logger.info("The mod version is current");
            }
        } catch (Throwable t) {
            this.logger.error(t, "Unable to process version information");
        }
    }
}
