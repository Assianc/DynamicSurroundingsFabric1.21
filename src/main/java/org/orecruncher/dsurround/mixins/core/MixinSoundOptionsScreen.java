package org.orecruncher.dsurround.mixins.core;

import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.orecruncher.dsurround.gui.sound.IndividualSoundControlScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen extends Screen {

    protected MixinSoundOptionsScreen(Component title) {
        super(title);
    }

    @Inject(method = "m_96307_", at = @At("RETURN"))
    private void addCustomButton(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(
            Component.translatable("dsurround.text.soundconfig.button"),
            button -> this.minecraft.setScreen(new IndividualSoundControlScreen(this, true))
        ).pos(this.width / 2 - 155, this.height - 27).size(150, 20).build());
    }
}
