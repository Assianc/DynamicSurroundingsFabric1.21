package org.orecruncher.dsurround.gui.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.sound.SoundInstanceHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndividualSoundControlList extends ObjectSelectionList<IndividualSoundControlListEntry> {

    private final List<IndividualSoundControlListEntry> entries = new ArrayList<>();

    public IndividualSoundControlList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraft, width, height, top, bottom, itemHeight);

        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);

        var soundManager = GameUtils.getSoundManager();
        var soundEvents = soundManager.getAvailableSounds();
        
        for (ResourceLocation sound : soundEvents) {
            this.entries.add(new IndividualSoundControlListEntry(this, sound));
        }

        this.entries.sort((a, b) -> a.getSoundLocation().toString().compareTo(b.getSoundLocation().toString()));
        this.replaceEntries(this.entries);
    }

    @Override
    public int getRowWidth() {
        return this.width - 40;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6;
    }

    public void setSearchFilter(String filter) {
        if (filter == null || filter.isEmpty()) {
            this.replaceEntries(this.entries);
        } else {
            var filtered = this.entries.stream()
                    .filter(entry -> entry.getSoundLocation().toString().contains(filter.toLowerCase()))
                    .toList();
            this.replaceEntries(filtered);
        }
    }

    public void playSound(ResourceLocation sound) {
        SoundInstanceHandler.playSound(sound);
    }
}