package org.orecruncher.dsurround.gui.overlay.plugins;

import net.minecraft.util.Mth;
import org.orecruncher.dsurround.eventing.ClientEventHooks;
import org.orecruncher.dsurround.eventing.CollectDiagnosticsEvent;
import org.orecruncher.dsurround.gui.overlay.IDiagnosticPlugin;
import org.orecruncher.dsurround.lib.events.HandlerPriority;
import org.orecruncher.dsurround.lib.math.TimerEMA;
import org.orecruncher.dsurround.eventing.ClientState;

public class ClientProfilerPlugin implements IDiagnosticPlugin {

    private final TimerEMA clientTick = new TimerEMA("Client Tick");
    private final TimerEMA lastTick = new TimerEMA("Last Tick");
    private long lastTickMark = -1;
    private long timeMark = 0;
    private float tps = 0;

    public ClientProfilerPlugin() {
        ClientEventHooks.COLLECT_DIAGNOSTICS.register(this::onCollect, HandlerPriority.VERY_HIGH);
        ClientState.TICK_START.register(this::tickStart, HandlerPriority.VERY_HIGH);
        ClientState.TICK_END.register(this::tickEnd, HandlerPriority.VERY_LOW);
    }

    private void tickStart(float partialTick) {
        this.timeMark = System.nanoTime();
        if (this.lastTickMark != -1) {
            this.lastTick.update(this.timeMark - this.lastTickMark);
            this.tps = Mth.clamp((float) (50F / this.lastTick.getMSecs() * 20F), 0F, 20F);
        }
        this.lastTickMark = this.timeMark;
    }

    private void tickEnd(float partialTick) {
        final long delta = System.nanoTime() - this.timeMark;
        this.clientTick.update(delta);
    }

    public void onCollect(CollectDiagnosticsEvent event) {
        event.add(CollectDiagnosticsEvent.Section.Systems, String.format("Client TPS: %.2f", this.tps));
        event.add(CollectDiagnosticsEvent.Section.Systems, this.clientTick.toString());
        event.add(CollectDiagnosticsEvent.Section.Systems, this.lastTick.toString());
    }
}
