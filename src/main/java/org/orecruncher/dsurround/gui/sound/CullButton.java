package org.orecruncher.dsurround.gui.sound;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.lib.util.ResourceUtils;

public class CullButton extends Button {
    private static final ResourceLocation BUTTON_TEXTURE = ResourceUtils.createResourceLocation(Constants.MOD_ID, "textures/gui/sound_cull.png");
    private static final int TEXTURE_WIDTH = 32;
    private static final int TEXTURE_HEIGHT = 32;
    private static final int BUTTON_WIDTH = 20;
    private static final int BUTTON_HEIGHT = 20;

    private boolean isCulled;

    public CullButton(int x, int y, OnPress onPress) {
        super(Button.builder(Component.empty(), onPress)
                .pos(x, y)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT));
        this.isCulled = false;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int u = this.isCulled ? BUTTON_WIDTH : 0;
        int v = this.isHovered() ? BUTTON_HEIGHT : 0;

        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        graphics.blit(BUTTON_TEXTURE, this.getX(), this.getY(), u, v, BUTTON_WIDTH, BUTTON_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public void setCulled(boolean culled) {
        this.isCulled = culled;
    }

    public boolean isCulled() {
        return this.isCulled;
    }

    public boolean toggle() {
        this.isCulled = !this.isCulled;
        return this.isCulled;
    }
}
