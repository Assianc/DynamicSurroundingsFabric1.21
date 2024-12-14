package org.orecruncher.dsurround.config;

import net.minecraft.resources.ResourceLocation;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.lib.util.ResourceUtils;
import org.orecruncher.dsurround.sound.ISoundFactory;
import org.orecruncher.dsurround.sound.SoundFactoryBuilder;

public enum ItemClassType {
    NONE(null),
    AXE("axe"),
    BOOK("book"),
    BOW("bow"),
    POTION("potion"),
    CROSSBOW("crossbow"),
    SHIELD("shield"),
    SWORD("sword"),
    TOOL("tool");

    private final ResourceLocation toolBarSound;
    private final ResourceLocation swingSound;
    private final ISoundFactory toolBarSoundFactory;
    private final ISoundFactory swingSoundFactory;

    ItemClassType(String name) {
        if (name != null) {
            this.toolBarSound = ResourceUtils.createResourceLocation(Constants.MOD_ID, "toolbar." + name + ".equip");
            this.swingSound = ResourceUtils.createResourceLocation(Constants.MOD_ID, "toolbar." + name + ".swing");
            this.toolBarSoundFactory = SoundFactoryBuilder.create(this.toolBarSound).build();
            this.swingSoundFactory = SoundFactoryBuilder.create(this.swingSound).build();
        } else {
            this.toolBarSound = null;
            this.swingSound = null;
            this.toolBarSoundFactory = null;
            this.swingSoundFactory = null;
        }
    }

    public ISoundFactory getToolBarSound() {
        return this.toolBarSoundFactory;
    }

    public ISoundFactory getSwingSound() {
        return this.swingSoundFactory;
    }
}
