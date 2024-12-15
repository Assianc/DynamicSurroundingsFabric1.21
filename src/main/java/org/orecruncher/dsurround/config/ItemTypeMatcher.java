package org.orecruncher.dsurround.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTypeMatcher {
    private final ResourceLocation id;
    private final boolean isTag;

    public ItemTypeMatcher(String id) {
        if (id.startsWith("#")) {
            this.id = new ResourceLocation(id.substring(1));
            this.isTag = true;
        } else {
            this.id = new ResourceLocation(id);
            this.isTag = false;
        }
    }

    public boolean matches(Item item) {
        if (this.isTag) {
            var tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), this.id);
            return item.builtInRegistryHolder().is(tagKey);
        }
        return BuiltInRegistries.ITEM.getKey(item).equals(this.id);
    }
}
