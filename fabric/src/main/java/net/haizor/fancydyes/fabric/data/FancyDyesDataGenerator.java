package net.haizor.fancydyes.fabric.data;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.haizor.fancydyes.FancyDyeUtil;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.crafting.SmithingDyeRecipe;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.dye.FancyDyeColor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FancyDyesDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(Language::new);
        pack.addProvider(Models::new);
        pack.addProvider(Recipes::new);
    }

    private static class Recipes extends FabricRecipeProvider {
        public Recipes(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void buildRecipes(Consumer<FinishedRecipe> exporter) {
            createBasicColorRecipes(exporter);

            createSpecialRecipes(exporter);

            for (FancyDye dye : FancyDyes.DYES_REGISTRAR) {
                createDyeSmithingRecipe(dye, exporter);
                createSizeConversionRecipe(dye, exporter);
            }

            new ShapedRecipeBuilder(RecipeCategory.DECORATIONS, FancyDyes.DYE_BOTTLE.get(), 8)
                .define('x', Blocks.GLASS.asItem())
                .unlockedBy("glass", FabricRecipeProvider.has(Blocks.GLASS.asItem()))
                .unlockedBy("self", FabricRecipeProvider.has(FancyDyes.DYE_BOTTLE.get()))
                .pattern("x x")
                .pattern("x x")
                .pattern(" x ")
                .save(exporter);

            new ShapelessRecipeBuilder(RecipeCategory.DECORATIONS, FancyDyes.SMALL_DYE_BOTTLE.get(), 2)
                .requires(FancyDyes.DYE_BOTTLE.get())
                .unlockedBy("dye_bottle", group(FancyDyes.DYE_BOTTLE.get(), FancyDyes.SMALL_DYE_BOTTLE.get()))
                .save(exporter, FancyDyes.SMALL_DYE_BOTTLE.getId().withSuffix("_from_large"));

            new ShapelessRecipeBuilder(RecipeCategory.DECORATIONS, FancyDyes.DYE_BOTTLE.get(), 1)
                .requires(FancyDyes.SMALL_DYE_BOTTLE.get(), 2)
                .unlockedBy("dye_bottle", group(FancyDyes.DYE_BOTTLE.get(), FancyDyes.SMALL_DYE_BOTTLE.get()))
                .save(exporter, FancyDyes.DYE_BOTTLE.getId().withSuffix("_from_small"));
        }

        public void createSpecialRecipes(Consumer<FinishedRecipe> exporter) {
            createDyeRecipe(exporter, FancyDyes.RAINBOW.get(), null, Items.RED_DYE, Items.ORANGE_DYE, Items.YELLOW_DYE, Items.LIME_DYE, Items.BLUE_DYE, Items.PURPLE_DYE);
            createDyeRecipe(exporter, FancyDyes.BRIGHT_RAINBOW.get(), null, Items.RED_DYE, Items.ORANGE_DYE, Items.YELLOW_DYE, Items.LIME_DYE, Items.BLUE_DYE, Items.PURPLE_DYE, Items.GLOW_INK_SAC);
            createBrightnessConversionRecipe(exporter, FancyDyes.RAINBOW.get(), FancyDyes.BRIGHT_RAINBOW.get());

            createDyeRecipe(exporter, FancyDyes.AURORA.get(), Items.AMETHYST_SHARD, Items.CYAN_DYE, Items.BLUE_DYE, Items.PURPLE_DYE, Items.AMETHYST_SHARD);
            createDyeRecipe(exporter, FancyDyes.BRIGHT_AURORA.get(), Items.AMETHYST_SHARD, Items.CYAN_DYE, Items.BLUE_DYE, Items.PURPLE_DYE, Items.AMETHYST_SHARD, Items.GLOW_INK_SAC);
            createBrightnessConversionRecipe(exporter, FancyDyes.AURORA.get(), FancyDyes.BRIGHT_AURORA.get());

            createDyeRecipe(exporter, FancyDyes.FLAME.get(), Items.BLAZE_POWDER, Items.RED_DYE, Items.ORANGE_DYE, Items.YELLOW_DYE, Items.BLAZE_POWDER);
            createDyeRecipe(exporter, FancyDyes.BRIGHT_FLAME.get(), Items.BLAZE_POWDER, Items.RED_DYE, Items.ORANGE_DYE, Items.YELLOW_DYE, Items.BLAZE_POWDER, Items.GLOW_INK_SAC);
            createBrightnessConversionRecipe(exporter, FancyDyes.FLAME.get(), FancyDyes.BRIGHT_FLAME.get());

            createDyeRecipe(exporter, FancyDyes.GREEN_FLAME.get(), Items.BLAZE_POWDER, Items.YELLOW_DYE, Items.GREEN_DYE, Items.LIME_DYE, Items.BLAZE_POWDER);
            createDyeRecipe(exporter, FancyDyes.BRIGHT_GREEN_FLAME.get(), Items.BLAZE_POWDER, Items.YELLOW_DYE, Items.GREEN_DYE, Items.LIME_DYE, Items.BLAZE_POWDER, Items.GLOW_INK_SAC);
            createBrightnessConversionRecipe(exporter, FancyDyes.GREEN_FLAME.get(), FancyDyes.BRIGHT_GREEN_FLAME.get());

            createDyeRecipe(exporter, FancyDyes.BLUE_FLAME.get(), Items.BLAZE_POWDER, Items.CYAN_DYE, Items.LIGHT_BLUE_DYE, Items.BLUE_DYE, Items.BLAZE_POWDER);
            createDyeRecipe(exporter, FancyDyes.BRIGHT_BLUE_FLAME.get(), Items.BLAZE_POWDER, Items.CYAN_DYE, Items.LIGHT_BLUE_DYE, Items.BLUE_DYE, Items.BLAZE_POWDER, Items.GLOW_INK_SAC);
            createBrightnessConversionRecipe(exporter, FancyDyes.BLUE_FLAME.get(), FancyDyes.BRIGHT_BLUE_FLAME.get());

            createDyeRecipe(exporter, FancyDyes.TRANS.get(), null, Items.PINK_DYE, Items.LIGHT_BLUE_DYE, Items.WHITE_DYE);
            createDyeRecipe(exporter, FancyDyes.BRIGHT_TRANS.get(), null, Items.PINK_DYE, Items.LIGHT_BLUE_DYE, Items.WHITE_DYE, Items.GLOW_INK_SAC);
            createBrightnessConversionRecipe(exporter, FancyDyes.TRANS.get(), FancyDyes.BRIGHT_TRANS.get());

            createDyeRecipe(exporter, FancyDyes.ENBY.get(), null, Items.BLACK_DYE, Items.YELLOW_DYE, Items.WHITE_DYE, Items.PURPLE_DYE);
            createDyeRecipe(exporter, FancyDyes.BRIGHT_ENBY.get(), null, Items.BLACK_DYE, Items.YELLOW_DYE, Items.WHITE_DYE, Items.PURPLE_DYE, Items.GLOW_INK_SAC);
            createBrightnessConversionRecipe(exporter, FancyDyes.ENBY.get(), FancyDyes.BRIGHT_ENBY.get());
        }

        public void createSizeConversionRecipe(FancyDye dye, Consumer<FinishedRecipe> exporter) {
            Item primary = dye.getItem(false);
            Item secondary = dye.getItem(true);

            new ShapelessRecipeBuilder(RecipeCategory.DECORATIONS, secondary, 2)
                .requires(primary)
                .unlockedBy("dye_bottle", group(primary, secondary))
                .group(dye.toIdString())
                .save(exporter, dye.toId().withSuffix("_dye_from_small"));

            new ShapelessRecipeBuilder(RecipeCategory.DECORATIONS, primary, 1)
                .requires(secondary, 2)
                .unlockedBy("dye_bottle", group(primary, secondary))
                .group(dye.toIdString())
                .save(exporter, dye.toId().withSuffix("_dye_from_large"));
        }

        public void createBrightnessConversionRecipe(Consumer<FinishedRecipe> exporter, FancyDye normal, FancyDye bright) {
            Item normalItem = normal.getItem(false);
            Item brightItem = bright.getItem(false);

            new ShapelessRecipeBuilder(RecipeCategory.DECORATIONS, brightItem, 1)
                .requires(normalItem)
                .requires(Items.GLOW_INK_SAC)
                .unlockedBy("dye_bottle", group(normalItem, brightItem))
                .group(bright.toIdString())
                .save(exporter, bright.toId().withSuffix("_dye_from_normal"));

            new ShapelessRecipeBuilder(RecipeCategory.DECORATIONS, normalItem, 1)
                .requires(brightItem)
                .requires(Items.INK_SAC)
                .unlockedBy("dye_bottle", group(brightItem, normalItem))
                .group(normal.toIdString())
                .save(exporter, normal.toId().withSuffix("_dye_from_bright"));
        }

        public void createDyeRecipe(Consumer<FinishedRecipe> exporter, FancyDye dye, @Nullable ItemLike unlock, ItemLike... items) {
            ShapelessRecipeBuilder builder = new ShapelessRecipeBuilder(RecipeCategory.DECORATIONS, dye.getItem(false), 1)
                .requires(FancyDyes.DYE_BOTTLE.get())
                .unlockedBy("bottle_recipe", RecipeUnlockedTrigger.unlocked(FancyDyes.DYE_BOTTLE.getId()))
                .unlockedBy("self_large", FabricRecipeProvider.has(dye.getItem(false)))
                .unlockedBy("self_small", FabricRecipeProvider.has(dye.getItem(true)))
                .group(dye.toIdString());

            if (unlock != null) {
                builder.unlockedBy("ingredient", group(unlock));
            }

            for (ItemLike ingredient : items) {
                builder.requires(ingredient);
            }

            builder.save(exporter);
        }

        public void createBasicColorRecipes(Consumer<FinishedRecipe> exporter) {
            for (FancyDyeColor color : FancyDyeColor.values()) {
                FancyDye normal = FancyDyes.DYES_REGISTRAR.get(FancyDyes.id(color.toString()));
                FancyDye bright = FancyDyes.DYES_REGISTRAR.get(FancyDyes.id("bright_%s".formatted(color.toString())));

                Item normalItem = normal.getItem(false);
                Item brightItem = bright.getItem(false);

                new ShapelessRecipeBuilder(RecipeCategory.DECORATIONS, normalItem, 1)
                    .requires(FancyDyes.DYE_BOTTLE.get())
                    .requires(color.getVanillaDye())
                    .unlockedBy("dye", group(color.getVanillaDye(), normalItem, brightItem))
                    .unlockedBy("bottle_recipe", RecipeUnlockedTrigger.unlocked(FancyDyes.DYE_BOTTLE.getId()))
                    .group(normal.toIdString())
                    .save(exporter);

                new ShapelessRecipeBuilder(RecipeCategory.DECORATIONS, brightItem, 1)
                    .requires(FancyDyes.DYE_BOTTLE.get())
                    .requires(color.getVanillaDye())
                    .requires(Items.GLOW_INK_SAC)
                    .unlockedBy("dye", group(color.getVanillaDye(), Items.GLOW_INK_SAC, normalItem, brightItem))
                    .unlockedBy("bottle_recipe", RecipeUnlockedTrigger.unlocked(FancyDyes.DYE_BOTTLE.getId()))
                    .group(bright.toIdString())
                    .save(exporter);

                createBrightnessConversionRecipe(exporter, normal, bright);
            }
        }

        public void createDyeSmithingRecipe(FancyDye dye, Consumer<FinishedRecipe> exporter) {
            new SmithingDyeRecipeBuilder(
                RecipeCategory.DECORATIONS,
                Ingredient.of(FancyDye.DYEABLE_PRIMARY),
                dye,
                false
            )
            .unlocks("primary_dye", FabricRecipeProvider.has(dye.getItem(false)))
            .save(exporter, dye.toId().withSuffix("_dye_smithing_recipe"));

            new SmithingDyeRecipeBuilder(
                RecipeCategory.DECORATIONS,
                Ingredient.of(FancyDye.DYEABLE_SECONDARY),
                dye,
                true
            )
            .unlocks("secondary_dye", FabricRecipeProvider.has(dye.getItem(true)))
            .save(exporter, dye.toId().withPath("small_%s_dye_smithing_recipe"::formatted));
        }

        public CriterionTriggerInstance group(ItemLike... items) {
            return FabricRecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(items).build());
        }

        public static class SmithingDyeRecipeBuilder {
            private final RecipeCategory category;
            private final Ingredient base;
            private final FancyDye dye;
            private final boolean secondary;
            private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
            private final RecipeSerializer<?> type;

            public SmithingDyeRecipeBuilder(RecipeCategory recipeCategory, Ingredient base, FancyDye dye, boolean secondary) {
                this.category = recipeCategory;
                this.type = SmithingDyeRecipe.SERIALIZER.get();
                this.base = base;
                this.dye = dye;
                this.secondary = secondary;
            }

            public SmithingDyeRecipeBuilder unlocks(String string, CriterionTriggerInstance criterionTriggerInstance) {
                this.advancement.addCriterion(string, criterionTriggerInstance);
                return this;
            }

            public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
                this.ensureValid(resourceLocation);
                this.advancement.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
                consumer.accept(new Result(resourceLocation, this.type, this.base, this.dye, this.secondary, this.advancement, resourceLocation.withPrefix("recipes/" + this.category.getFolderName() + "/")));
            }

            private void ensureValid(ResourceLocation resourceLocation) {
                if (this.advancement.getCriteria().isEmpty()) {
                    throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
                }
            }

            public record Result(ResourceLocation id, RecipeSerializer<?> type, Ingredient base, FancyDye dye, boolean secondary, Advancement.Builder advancement, ResourceLocation advancementId) implements FinishedRecipe {
                public void serializeRecipeData(JsonObject jsonObject) {
                    jsonObject.add("base", this.base.toJson());
                    jsonObject.addProperty("dye", this.dye.toId().toString());
                    jsonObject.addProperty("secondary", this.secondary);
                }

                public ResourceLocation getId() {
                    return this.id;
                }

                public RecipeSerializer<?> getType() {
                    return this.type;
                }

                @Nullable
                public JsonObject serializeAdvancement() {
                    return this.advancement.serializeToJson();
                }

                @Nullable
                public ResourceLocation getAdvancementId() {
                    return this.advancementId;
                }
            }
        }
    }

    private static class Language extends FabricLanguageProvider {
        protected Language(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            translationBuilder.add("gui.fancydyes.tooltip.dyes", "Dyes");
            translationBuilder.add(FancyDyes.DYE_BOTTLE.get(), "Empty Dye Bottle");
            translationBuilder.add(FancyDyes.SMALL_DYE_BOTTLE.get(), "Small Empty Dye Bottle");
            translationBuilder.add("itemgroup.fancydyes.dyes", "Fancy Dyes");

            for (FancyDyeColor color : FancyDyeColor.values()) {
                forDyeColor(color, translationBuilder);
            }

            forDye(FancyDyes.RAINBOW.get(), "Rainbow", translationBuilder);
            forDye(FancyDyes.BRIGHT_RAINBOW.get(), "Bright Rainbow", translationBuilder);

            forDye(FancyDyes.AURORA.get(), "Aurora", translationBuilder);
            forDye(FancyDyes.BRIGHT_AURORA.get(), "Bright Aurora", translationBuilder);

            forDye(FancyDyes.FLAME.get(), "Flame", translationBuilder);
            forDye(FancyDyes.BRIGHT_FLAME.get(), "Bright Flame", translationBuilder);

            forDye(FancyDyes.GREEN_FLAME.get(), "Green Flame", translationBuilder);
            forDye(FancyDyes.BRIGHT_GREEN_FLAME.get(), "Bright Green Flame", translationBuilder);

            forDye(FancyDyes.BLUE_FLAME.get(), "Blue Flame", translationBuilder);
            forDye(FancyDyes.BRIGHT_BLUE_FLAME.get(), "Bright Blue Flame", translationBuilder);

            forDye(FancyDyes.TRANS.get(), "Trans", translationBuilder);
            forDye(FancyDyes.BRIGHT_TRANS.get(), "Bright Trans", translationBuilder);

            forDye(FancyDyes.ENBY.get(), "Enby", translationBuilder);
            forDye(FancyDyes.BRIGHT_ENBY.get(), "Bright Enby", translationBuilder);
        }

        public void forDye(FancyDye dye, String name, TranslationBuilder builder) {
            builder.add(dye.getItem(false), "%s Dye".formatted(name));
            builder.add(dye.getItem(true), "Small %s Dye".formatted(name));
        }

        public void forDyeColor(FancyDyeColor color, TranslationBuilder builder) {
            FancyDye solid = FancyDyes.DYES_REGISTRAR.get(FancyDyes.id(color.toString()));
            FancyDye bright = FancyDyes.DYES_REGISTRAR.get(FancyDyes.id("bright_%s".formatted(color.toString())));
            forDye(solid, FancyDyeUtil.capitalize(color.toString().replace('_', ' ')), builder);
            forDye(bright, "Bright %s".formatted(FancyDyeUtil.capitalize(color.toString().replace('_', ' '))), builder);
        }
    }

    private static class Models extends FabricModelProvider {
        public Models(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {}

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            itemModelGenerator.generateFlatItem(FancyDyes.DYE_BOTTLE.get(), ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(FancyDyes.SMALL_DYE_BOTTLE.get(), ModelTemplates.FLAT_ITEM);

            for (FancyDye dye : FancyDyes.DYES_REGISTRAR) {
                itemModelGenerator.generateLayeredItem(FancyDyes.id("item/%s_dye".formatted(dye.toId().getPath())), FancyDyes.id("item/dye_bottle"), FancyDyes.id("item/dye_bottle_inside"));
                itemModelGenerator.generateLayeredItem(FancyDyes.id("item/small_%s_dye".formatted(dye.toId().getPath())), FancyDyes.id("item/small_dye_bottle"), FancyDyes.id("item/small_dye_bottle_inside"));
            }
        }
    }
}
