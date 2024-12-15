package org.orecruncher.dsurround.gui.overlay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.events.HandlerPriority;
import org.orecruncher.dsurround.lib.gui.ColorPalette;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.eventing.CollectDiagnosticsEvent;
import org.orecruncher.dsurround.eventing.ClientState;

public class DiagnosticsOverlay extends AbstractOverlay {
    private static final DiagnosticsOverlay INSTANCE = new DiagnosticsOverlay();
    private final IModLog logger;
    private final CollectDiagnosticsEvent reusableEvent = new CollectDiagnosticsEvent();
    private boolean isVisible = false;

    private DiagnosticsOverlay() {
        this.logger = ContainerManager.resolve(IModLog.class);
        ClientState.TICK_END.register(this::onTick, HandlerPriority.LOW);
    }

    public static DiagnosticsOverlay get() {
        return INSTANCE;
    }

    public void toggleDisplay() {
        this.isVisible = !this.isVisible;
    }

    private void onTick(float partialTick) {
        if (this.isVisible) {
            this.reusableEvent.clear();
            this.collectData();
        }
    }

    private void collectData() {
        var player = GameUtils.getPlayer();
        if (player.isEmpty())
            return;

        var serverBrand = GameUtils.getServerBrand();
        if (serverBrand != null) {
            this.reusableEvent.add(CollectDiagnosticsEvent.Section.Header, "Server Brand: %s", serverBrand);
        }

        // Add other diagnostic data collection here
    }

    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        if (!this.isVisible || graphics == null)
            return;

        var mc = GameUtils.getMC();
        if (mc.screen != null)
            return;

        var font = GameUtils.getTextRenderer();
        var lines = new ObjectArray<Component>();

        for (var section : CollectDiagnosticsEvent.Section.values()) {
            var text = this.reusableEvent.getSectionText(section);
            if (!text.isEmpty()) {
                if (section.addHeader()) {
                    lines.add(Component.literal("=== " + section.name() + " ===").withStyle(style -> style.withColor(ColorPalette.GOLD.getRGB())));
                }
                lines.addAll(text);
            }
        }

        if (lines.isEmpty())
            return;

        var y = 5;
        for (var line : lines) {
            graphics.drawString(font, line, 5, y, ColorPalette.WHITE.getRGB(), true);
            y += font.lineHeight + 1;
        }
    }
}
