package org.orecruncher.dsurround.processing;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import org.orecruncher.dsurround.Configuration;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.mixinutils.ILivingEntityExtended;

import java.util.Collection;

public class PotionParticleSuppressionHandler extends AbstractClientHandler {

    private static final int HIDE_PARTICLE_SENTINEL = -1;

    public PotionParticleSuppressionHandler(Configuration config, IModLog logger) {
        super("Player Handler", config, logger);
    }

    @Override
    public void process(final Player player) {
        if (GameUtils.isInGame()) {
            int color = getPotionParticleColor(player);

            if (color == 0)
                return;

            if (this.config.particleTweaks.suppressPlayerParticles) {
                final boolean hide = GameUtils.isFirstPersonView();
                if (hide) {
                    if (color > 0)
                        suppressPotionParticles(player);
                } else {
                    if (color < 0)
                        unsuppressPotionParticles(player);
                }
            } else if (color < 0) {
                unsuppressPotionParticles(player);
            }
        }
    }

    private static int getPotionParticleColor(Player player) {
        var accessor = (ILivingEntityExtended)player;
        return accessor.dsurround_getPotionSwirlColor();
    }

    private static void suppressPotionParticles(Player player) {
        var accessor = (ILivingEntityExtended)player;
        accessor.dsurround_setPotionSwirlColor(HIDE_PARTICLE_SENTINEL);
    }

    private static void unsuppressPotionParticles(Player player) {
        var accessor = (ILivingEntityExtended)player;
        var effects = player.getActiveEffects();
        int color = calculateEffectsColor(effects);
        accessor.dsurround_setPotionSwirlColor(color);
    }

    public static boolean suppressParticles() {
        if (!Configuration.getInstance().particleTweaks.suppressPlayerPotionParticles)
            return false;

        var player = GameUtils.getPlayer();
        if (player.isEmpty())
            return false;

        var effects = player.get().getActiveEffects();
        return !effects.isEmpty() && hasVisibleParticles(effects);
    }

    private static boolean hasVisibleParticles(Collection<MobEffectInstance> effects) {
        for (var effect : effects) {
            if (effect.isVisible())
                return true;
        }
        return false;
    }

    public static boolean isPotion(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof PotionItem || stack.is(Items.POTION));
    }

    private static int calculateEffectsColor(Collection<MobEffectInstance> effects) {
        if (effects.isEmpty()) return 0;
        
        int r = 0;
        int g = 0;
        int b = 0;
        int total = 0;

        for (MobEffectInstance effect : effects) {
            if (effect.isVisible()) {
                int color = effect.getEffect().getColor();
                r += (color >> 16) & 0xFF;
                g += (color >> 8) & 0xFF;
                b += color & 0xFF;
                total++;
            }
        }

        if (total == 0) return 0;

        r = r / total & 0xFF;
        g = g / total & 0xFF;
        b = b / total & 0xFF;

        return (r << 16) | (g << 8) | b;
    }

    public static int getPotionColor(ItemStack stack) {
        if (!isPotion(stack)) return 0;
        var effects = PotionItem.getEffects(stack);
        return calculateEffectsColor(effects);
    }
}
