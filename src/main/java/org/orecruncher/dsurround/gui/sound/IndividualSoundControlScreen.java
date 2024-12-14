package org.orecruncher.dsurround.gui.sound;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.orecruncher.dsurround.lib.GameUtils;

public class IndividualSoundControlScreen extends Screen {
    private final Screen parent;
    private final boolean showBackgroundTexture;
    private IndividualSoundControlList soundList;

    public IndividualSoundControlScreen(Screen parent, boolean showBackgroundTexture) {
        super(Component.translatable("dsurround.text.soundconfig.title"));
        this.parent = parent;
        this.showBackgroundTexture = showBackgroundTexture;
    }

    @Override
    protected void init() {
        this.soundList = new IndividualSoundControlList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.addWidget(this.soundList);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.showBackgroundTexture) {
            this.renderBackground(graphics, mouseX, mouseY, partialTick);
        }

        this.soundList.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        GameUtils.getMC().setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}