package net.haizor.fancydyes.crafting;

import com.google.gson.JsonObject;
import dev.architectury.registry.registries.RegistrySupplier;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SmithingDyeRecipe implements SmithingRecipe {
    public final ResourceLocation id;
    final Ingredient base;
    final FancyDye dye;
    public final boolean secondary;

    public SmithingDyeRecipe(ResourceLocation id, Ingredient base, FancyDye dye, boolean secondary) {
        this.id = id;
        this.base = base;
        this.dye = dye;
        this.secondary = secondary;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isBaseIngredient(ItemStack itemStack) {
        return base.test(itemStack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack itemStack) {
        return itemStack.getItem() instanceof FancyDyeItem dyeItem && dyeItem.dye.get().equals(dye) && dyeItem.secondary == secondary;
    }

    public boolean templateCheck(ItemStack stack) {
        return (stack == null || stack.isEmpty() || stack.is(secondary ? FancyDyes.SMALL_DYE_BOTTLE.get() : FancyDyes.DYE_BOTTLE.get()));
    }

    @Override
    public boolean matches(Container container, Level level) {
        return templateCheck(container.getItem(0)) && this.isBaseIngredient(container.getItem(1)) && this.isAdditionIngredient(container.getItem(2));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack itemStack = container.getItem(1);
        if (base.test(itemStack) && container.getItem(2).getItem() instanceof FancyDyeItem item) {
            FancyDye dye = item.dye.get();
            boolean isSecondary = item.secondary;

            if (isSecondary && ArmorTrim.getTrim(registryAccess, itemStack).isEmpty()) {
                return ItemStack.EMPTY;
            }

            if (FancyDye.getDye(itemStack, isSecondary).filter(d -> d.equals(dye)).isPresent()) {
                return ItemStack.EMPTY;
            }

            ItemStack copy = itemStack.copy();
            FancyDye.setDye(copy, dye, isSecondary);
            copy.setCount(1);
            return copy;
        }
        return ItemStack.EMPTY;
    }

    //TODO: this.
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        Optional<Holder.Reference<TrimMaterial>> optional2;
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
        Optional<Holder.Reference<TrimPattern>> optional = registryAccess.registryOrThrow(Registries.TRIM_PATTERN).holders().findFirst();
        if (optional.isPresent() && (optional2 = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolder(TrimMaterials.IRON)).isPresent()) {
            ArmorTrim armorTrim = new ArmorTrim((Holder<TrimMaterial>)optional2.get(), (Holder<TrimPattern>)optional.get());
            ArmorTrim.setTrim(registryAccess, itemStack, armorTrim);
        }

        FancyDye.setDye(itemStack, dye, secondary);

        return itemStack;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER.get();
    }

    public static void register() {}

    public static final RegistrySupplier<RecipeSerializer<?>> SERIALIZER = FancyDyes.SERIALIZERS.register("smithing_fancy_dye", () -> new Serializer());

    public static class Serializer implements RecipeSerializer<SmithingDyeRecipe> {
        @Override
        public SmithingDyeRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            Ingredient base = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "base"));
            FancyDye dye = FancyDyes.DYES_REGISTRAR.get(new ResourceLocation(GsonHelper.getNonNull(jsonObject, "dye").getAsString()));
            boolean secondary = jsonObject.get("secondary").getAsBoolean();
            return new SmithingDyeRecipe(resourceLocation, base, dye, secondary);
        }

        @Override
        public SmithingDyeRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            Ingredient base = Ingredient.fromNetwork(friendlyByteBuf);
            FancyDye dye = FancyDyes.DYES_REGISTRAR.get(friendlyByteBuf.readResourceLocation());
            boolean secondary = friendlyByteBuf.readBoolean();

            return new SmithingDyeRecipe(resourceLocation, base, dye, secondary);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, SmithingDyeRecipe recipe) {
            recipe.base.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeResourceLocation(recipe.dye.toId());
            friendlyByteBuf.writeBoolean(recipe.secondary);
        }
    }
}
