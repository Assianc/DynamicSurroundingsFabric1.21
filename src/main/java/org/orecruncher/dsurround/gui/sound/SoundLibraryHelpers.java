package org.orecruncher.dsurround.gui.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.orecruncher.dsurround.Client;
import org.orecruncher.dsurround.config.IndividualSoundConfigEntry;
import org.orecruncher.dsurround.lib.GameUtils;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
final class SoundLibraryHelpers {

    public static Collection<IndividualSoundConfigEntry> getSortedSoundConfigurations() {

        final SortedMap<String, IndividualSoundConfigEntry> map = new TreeMap<>();

        // Get a list of all the sounds and set to default.  Each gets its own state in case caller
        // wants to manipulate.
        for (final SoundEvent event : Registry.SOUND_EVENT) {
            IndividualSoundConfigEntry entry = IndividualSoundConfigEntry.createDefault(event);
            map.put(entry.id, entry);
        }

        // Override with the defaults from configuration.  Make a copy of the original, so it doesn't change.
        for (IndividualSoundConfigEntry entry : Client.SoundConfig.getIndividualSoundConfigs()) {
            map.put(entry.id, entry);
        }

        final Comparator<IndividualSoundConfigEntry> iscComparator = Comparator.comparing(isc -> isc.id);
        return map.values().stream().sorted(iscComparator).collect(Collectors.toList());
    }

    public static ConfigSoundInstance playSound(IndividualSoundConfigEntry entry) {
        ConfigSoundInstance sound = new ConfigSoundInstance(new Identifier(entry.id), entry.volumeScale);
        GameUtils.getSoundHander().play(sound);
        return sound;
    }

    public static void stopSound(ConfigSoundInstance sound) {
        GameUtils.getSoundHander().stop(sound);
    }

    public static boolean isPlaying(ConfigSoundInstance sound) {
        return GameUtils.getSoundHander().isPlaying(sound);
    }
}