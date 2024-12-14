package org.orecruncher.dsurround.mixins.core;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.orecruncher.dsurround.gui.overlay.OverlayManager;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinIngameHud {

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(GuiGraphics guiGraphics, float tickDelta, CallbackInfo ci) {
        ContainerManager.resolve(OverlayManager.class).render(guiGraphics, tickDelta);
    }
}
