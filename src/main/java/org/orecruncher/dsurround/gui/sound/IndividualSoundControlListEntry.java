package org.orecruncher.dsurround.gui.sound;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.orecruncher.dsurround.config.IndividualSoundConfigEntry;
import org.orecruncher.dsurround.config.libraries.ISoundLibrary;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.di.ContainerManager;
import org.orecruncher.dsurround.lib.gui.ColorPalette;
import org.orecruncher.dsurround.lib.gui.GuiHelpers;
import org.orecruncher.dsurround.lib.gui.TextWidget;
import org.orecruncher.dsurround.lib.platform.IPlatform;
import org.orecruncher.dsurround.sound.IAudioPlayer;
import org.orecruncher.dsurround.sound.SoundMetadata;

import java.util.*;

public class IndividualSoundControlListEntry extends ObjectSelectionList.Entry<IndividualSoundControlListEntry> {

    private static final ISoundLibrary SOUND_LIBRARY = ContainerManager.resolve(ISoundLibrary.class);
    private static final IAudioPlayer AUDIO_PLAYER = ContainerManager.resolve(IAudioPlayer.class);
    private static final IPlatform PLATFORM = Library.PLATFORM;

    private static final int TOOLTIP_WIDTH = 300;

    private static final Style STYLE_MOD_NAME = Style.EMPTY.withColor(ColorPalette.GOLD);
    private static final Style STYLE_ID = Style.EMPTY.withColor(ColorPalette.SLATEGRAY);
    private static final Style STYLE_CATEGORY = Style.EMPTY.withColor(ColorPalette.FRESH_AIR);
    private static final Style STYLE_SUBTITLE = Style.EMPTY.withColor(ColorPalette.APRICOT).withItalic(true);
    private static final Style STYLE_CREDIT_NAME = Style.EMPTY.withColor(ColorPalette.GREEN);
    private static final Style STYLE_CREDIT_AUTHOR = Style.EMPTY.withColor(ColorPalette.WHITE);
    private static final Style STYLE_CREDIT_LICENSE = Style.EMPTY.withItalic(true).withColor(ColorPalette.MC_DARKAQUA);
    private static final Style STYLE_HELP = Style.EMPTY.withItalic(true).withColor(ColorPalette.KEY_LIME);

    private static final FormattedCharSequence VANILLA_CREDIT = Component.translatable("dsurround.text.soundconfig.vanilla").getVisualOrderText();
    private static final Collection<Component> VOLUME_HELP = GuiHelpers.getTrimmedTextCollection("dsurround.text.soundconfig.volume.help", TOOLTIP_WIDTH, STYLE_HELP);
    private static final Collection<Component> PLAY_HELP = GuiHelpers.getTrimmedTextCollection("dsurround.text.soundconfig.play.help", TOOLTIP_WIDTH, STYLE_HELP);
    private static final Collection<Component> CULL_HELP = GuiHelpers.getTrimmedTextCollection("dsurround.text.soundconfig.cull.help", TOOLTIP_WIDTH, STYLE_HELP);
    private static final Collection<Component> BLOCK_HELP = GuiHelpers.getTrimmedTextCollection("dsurround.text.soundconfig.block.help", TOOLTIP_WIDTH, STYLE_HELP);
    private static final int CONTROL_SPACING = 3;

    private final IndividualSoundControlList parent;
    private final ResourceLocation soundLocation;
    private final Component displayName;
    private final SoundPlayButton playButton;
    private final BlockButton blockButton;
    private final CullButton cullButton;

    private final List<AbstractWidget> children = new ArrayList<>();
    private final List<FormattedCharSequence> cachedToolTip = new ArrayList<>();

    private ConfigSoundInstance soundPlay;

    public IndividualSoundControlListEntry(IndividualSoundControlList parent, ResourceLocation soundLocation) {
        this.parent = parent;
        this.soundLocation = soundLocation;
        this.displayName = Component.literal(soundLocation.toString());
        this.playButton = new SoundPlayButton(0, 0, button -> this.parent.playSound(this.soundLocation));
        this.blockButton = new BlockButton(0, 0, button -> {});
        this.cullButton = new CullButton(0, 0, button -> {});

        this.children.add(new TextWidget(0, 0, 200, GameUtils.getTextRenderer().lineHeight, this.displayName, GameUtils.getTextRenderer()));
        this.children.add(this.playButton);
        this.children.add(this.blockButton);
        this.children.add(this.cullButton);
    }

    public ResourceLocation getSoundLocation() {
        return this.soundLocation;
    }

    @Override
    public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
        int textColor = isHovered ? ColorPalette.WHITE.getRGB() : ColorPalette.GRAY.getRGB();
        graphics.drawString(graphics.minecraft.font, this.displayName, left + 24, top + 6, textColor);

        this.playButton.setX(left);
        this.playButton.setY(top);
        this.playButton.render(graphics, mouseX, mouseY, partialTick);

        this.blockButton.setX(left + width - 48);
        this.blockButton.setY(top);
        this.blockButton.render(graphics, mouseX, mouseY, partialTick);

        this.cullButton.setX(left + width - 24);
        this.cullButton.setY(top);
        this.cullButton.render(graphics, mouseX, mouseY, partialTick);

        for (final AbstractWidget w : this.children)
            w.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.playButton.isMouseOver(mouseX, mouseY)) {
            this.playButton.onClick(mouseX, mouseY);
            return true;
        }
        if (this.blockButton.isMouseOver(mouseX, mouseY)) {
            this.blockButton.onClick(mouseX, mouseY);
            return true;
        }
        if (this.cullButton.isMouseOver(mouseX, mouseY)) {
            this.cullButton.onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public Component getNarration() {
        return this.displayName;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        // TODO:  What?
        return this.children;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        AbstractWidget child = this.findChild(mouseX, mouseY);
        if (child != null)
            return child.mouseReleased(mouseX, mouseY, button);
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        AbstractWidget child = this.findChild(mouseX, mouseY);
        if (child != null)
            return child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount) {
        AbstractWidget child = this.findChild(mouseX, mouseY);
        if (child != null)
            return child.mouseScrolled(mouseX, mouseY, hAmount, vAmount);
        return false;
    }

    private AbstractWidget findChild(double mouseX, double mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            for (AbstractWidget e : this.children) {
                if (e.isMouseOver(mouseX, mouseY)) {
                    return e;
                }
            }
        }
        return null;
    }

    protected void toggleBlock(Button button) {
        if (button instanceof BlockButton bb) {
            this.parent.config.block = bb.toggle();
        }
    }

    protected void toggleCull(Button button) {
        if (button instanceof CullButton cb)
            this.parent.config.cull = cb.toggle();
    }

    protected void play(final Button button) {
        if (button instanceof SoundPlayButton sp) {
            if (this.soundPlay == null) {
                this.soundPlay = this.playSound(this.parent.config);
                sp.play();
            } else {
                AUDIO_PLAYER.stop(this.soundPlay);
                this.soundPlay = null;
                sp.stop();
            }
        }
    }

    protected ConfigSoundInstance playSound(IndividualSoundConfigEntry entry) {
        var metadata = SOUND_LIBRARY.getSoundMetadata(entry.soundEventId);
        ConfigSoundInstance sound = ConfigSoundInstance.create(entry.soundEventId, metadata.getCategory(), () -> entry.volumeScale / 100F);
        AUDIO_PLAYER.play(sound);
        return sound;
    }

    @Override
    public void close() {
        if (this.soundPlay != null) {
            AUDIO_PLAYER.stop(this.soundPlay);
            this.soundPlay = null;
        }
    }

    public void tick() {
        if (this.soundPlay != null && this.playButton != null) {
            if (!AUDIO_PLAYER.isPlaying(this.soundPlay)) {
                this.soundPlay = null;
                this.playButton.stop();
            }
        }
    }

    protected List<FormattedCharSequence> getToolTip(final int mouseX, final int mouseY) {
        // Cache the static part of the tooltip if needed
        if (this.cachedToolTip.isEmpty()) {
            ResourceLocation id = this.parent.config.soundEventId;
            this.resolveDisplayName(id.getNamespace())
                    .ifPresent(name -> {
                        FormattedCharSequence modName = FormattedCharSequence.forward(Objects.requireNonNull(ChatFormatting.stripFormatting(name)), STYLE_MOD_NAME);
                        this.cachedToolTip.add(modName);
                    });

            @SuppressWarnings("ConstantConditions")
            FormattedCharSequence soundLocationId = FormattedCharSequence.forward(id.toString(), STYLE_ID);

            this.cachedToolTip.add(soundLocationId);

            SoundMetadata metadata = SOUND_LIBRARY.getSoundMetadata(id);
            if (metadata != null) {
                if (!metadata.getTitle().equals(Component.empty()))
                    this.cachedToolTip.add(metadata.getTitle().getVisualOrderText());

                this.cachedToolTip.add(Component.literal(metadata.getCategory().toString()).withStyle(STYLE_CATEGORY).getVisualOrderText());

                if (!metadata.getSubTitle().equals(Component.empty())) {
                    this.cachedToolTip.add(metadata.getSubTitle().copy().withStyle(STYLE_SUBTITLE).getVisualOrderText());
                }

                if (!metadata.getCredits().isEmpty()) {
                    for (var credit : metadata.getCredits()) {
                        this.cachedToolTip.add(Component.empty().getVisualOrderText());
                        this.cachedToolTip.add(credit.name().copy().withStyle(STYLE_CREDIT_NAME).getVisualOrderText());
                        this.cachedToolTip.add(credit.author().copy().withStyle(STYLE_CREDIT_AUTHOR).getVisualOrderText());
                        if (credit.webSite().isPresent()) {
                            this.cachedToolTip.add(credit.webSite().get().copy().withStyle(STYLE_CREDIT_AUTHOR).getVisualOrderText());
                        }
                        this.cachedToolTip.add(credit.license().copy().withStyle(STYLE_CREDIT_LICENSE).getVisualOrderText());
                    }
                }
            }

            if (id.getNamespace().equals("minecraft")) {
                this.cachedToolTip.add(VANILLA_CREDIT);
            }
        }

        List<FormattedCharSequence> generatedTip = new ArrayList<>(this.cachedToolTip);

        Collection<Component> toAppend = null;
        if (this.playButton.isMouseOver(mouseX, mouseY)) {
            toAppend = PLAY_HELP;
        } else if (this.blockButton.isMouseOver(mouseX, mouseY)) {
            toAppend = BLOCK_HELP;
        } else if (this.cullButton.isMouseOver(mouseX, mouseY)) {
            toAppend = CULL_HELP;
        }

        if (toAppend != null) {
            generatedTip.add(FormattedCharSequence.EMPTY);
            toAppend.forEach(e -> generatedTip.add(e.getVisualOrderText()));
        }

        return generatedTip;
    }

    private Optional<String> resolveDisplayName(String namespace) {
        var displayName = PLATFORM.getModDisplayName(namespace);
        if (displayName.isPresent())
            return displayName;

        // Could be a resource pack
        return GameUtils.getResourceManager().listPacks()
                .filter(pack -> pack.getNamespaces(PackType.CLIENT_RESOURCES).contains(namespace))
                .map(PackResources::packId)
                .findAny();
    }

    /**
     * Retrieves the updated data from the entry
     *
     * @return Updated IndividualSoundControl data
     */
    public IndividualSoundConfigEntry getData() {
        return this.parent.config;
    }

}