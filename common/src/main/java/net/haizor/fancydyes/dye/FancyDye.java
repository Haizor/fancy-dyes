package net.haizor.fancydyes.dye;

import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.Optional;

public interface FancyDye {
    String ROOT_DYE_TAG = new ResourceLocation(FancyDyes.MOD_ID, "dyes").toString();
    String SECONDARY_DYE_TAG = "secondary";
    String PRIMARY_DYE_TAG = "primary";

    TagKey<Item> DIAGONAL_SCROLL = TagKey.create(Registries.ITEM, FancyDyes.id("diagonal"));
    TagKey<Item> DYEABLE_PRIMARY = TagKey.create(Registries.ITEM, FancyDyes.id("dyeable_primary"));
    TagKey<Item> DYEABLE_SECONDARY = TagKey.create(Registries.ITEM, FancyDyes.id("dyeable_secondary"));

    String getShaderType();

    default ResourceLocation toId() {
        return FancyDyes.DYES_REGISTRAR.getId(this);
    }

    default ResourceLocation createId(boolean secondary) {
        return this.toId().withPath(s -> "%s%s_dye".formatted(secondary ? "small_" : "", s));
    }

    default Vector3f getColor() { return new Vector3f(1f); }
    default void setupRenderState() {}
    default void resetRenderState() {

    }

    default String toIdString() {
        return this.toId().toString();
    }

    static FancyDye fromId(ResourceLocation id) {
        return FancyDyes.DYES_REGISTRAR.get(id);
    }

    default Item getItem(boolean secondary) {
        return BuiltInRegistries.ITEM.get(this.createId(secondary));
    }

    static Optional<FancyDye> getDye(ItemStack stack, boolean secondary) {
        if (stack.isEmpty()) return Optional.empty();

        if (secondary && stack.getItem() instanceof FancyDyeItem dyeItem) {
            return Optional.of(dyeItem.dye.get());
        }

        CompoundTag tag = stack.getTagElement(ROOT_DYE_TAG);
        if (tag == null) return Optional.empty();
        String key = secondary ? SECONDARY_DYE_TAG : PRIMARY_DYE_TAG;
        if (!tag.contains(key)) return Optional.empty();
        FancyDye dye = FancyDye.fromId(new ResourceLocation(tag.getString(key)));
        if (dye == null) return Optional.empty();
        return Optional.of(dye);
    }

    static void setDye(ItemStack stack, FancyDye dye, boolean secondary) {
        String key = secondary ? SECONDARY_DYE_TAG : PRIMARY_DYE_TAG;
        CompoundTag root = stack.getOrCreateTagElement(ROOT_DYE_TAG);
        if (dye == null) {
            root.remove(key);
            if (root.isEmpty()) {
                stack.removeTagKey(ROOT_DYE_TAG);
                return;
            }
        } else {
            root.putString(key, dye.toId().toString());
        }
        stack.addTagElement(ROOT_DYE_TAG, root);
    }
}
