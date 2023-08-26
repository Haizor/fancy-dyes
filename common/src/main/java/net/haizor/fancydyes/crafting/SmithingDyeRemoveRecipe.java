package net.haizor.fancydyes.crafting;

import com.google.gson.JsonObject;
import dev.architectury.registry.registries.RegistrySupplier;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
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

public class SmithingDyeRemoveRecipe implements SmithingRecipe {
    public final ResourceLocation id;
    final Ingredient base;
    public final boolean secondary;

    public SmithingDyeRemoveRecipe(ResourceLocation id, Ingredient base, boolean secondary) {
        this.id = id;
        this.base = base;
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
        return itemStack.is(secondary ? FancyDyes.SMALL_DYE_BOTTLE.get() : FancyDyes.DYE_BOTTLE.get());
    }

    public boolean templateCheck(ItemStack stack, FancyDye dye) {
        return (stack == null || stack.isEmpty() || stack.is(dye.getItem(secondary)));
    }

    @Override
    public boolean matches(Container container, Level level) {
        return this.isBaseIngredient(container.getItem(1)) && this.isAdditionIngredient(container.getItem(2));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack itemStack = container.getItem(1);
        if (base.test(itemStack) && isAdditionIngredient(container.getItem(2))) {
            Optional<FancyDye> dye = FancyDye.getDye(itemStack, secondary);
            if (dye.isEmpty()) return ItemStack.EMPTY;
            if (!templateCheck(container.getItem(0), dye.get())) {
                return ItemStack.EMPTY;
            }

            ItemStack copy = itemStack.copy();
            FancyDye.setDye(copy, null, secondary);
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

    public static final RegistrySupplier<RecipeSerializer<?>> SERIALIZER = FancyDyes.SERIALIZERS.register("smithing_fancy_dye_remove", Serializer::new);

    public static class Serializer implements RecipeSerializer<SmithingDyeRemoveRecipe> {
        @Override
        public SmithingDyeRemoveRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            Ingredient base = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "base"));
            boolean secondary = jsonObject.get("secondary").getAsBoolean();
            return new SmithingDyeRemoveRecipe(resourceLocation, base, secondary);
        }

        @Override
        public SmithingDyeRemoveRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            Ingredient base = Ingredient.fromNetwork(friendlyByteBuf);
            boolean secondary = friendlyByteBuf.readBoolean();

            return new SmithingDyeRemoveRecipe(resourceLocation, base, secondary);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, SmithingDyeRemoveRecipe recipe) {
            recipe.base.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeBoolean(recipe.secondary);
        }
    }
}
