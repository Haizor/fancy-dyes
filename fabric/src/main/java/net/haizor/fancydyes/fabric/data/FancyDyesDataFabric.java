package net.haizor.fancydyes.fabric.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.data.FancyDyesData;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Set;

public class FancyDyesDataFabric implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
//        dataGenerator.addProvider(FancyDyesData.Language::new);
        dataGenerator.addProvider(Models::new);
        dataGenerator.addProvider(Recipes::new);
    }

    public static class Models extends FabricModelProvider {
        public Models(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators models) {
            ResourceLocation base = new ResourceLocation(FancyDyes.MOD_ID, "item/dye_bottle");
            for (String key : FancyDyes.DYES.keySet()) {
                models.modelOutput.accept(new ResourceLocation(FancyDyes.MOD_ID, "item/%s_dye".formatted(key)), new DelegatedModel(base));
            }
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {}
    }

    public static class Recipes extends RecipeProvider {
        public DataGenerator generator;
        public Recipes(DataGenerator dataGenerator) {
            super(dataGenerator);
            this.generator = dataGenerator;
        }

        @Override
        public void run(HashCache cache) {
            Path path = this.generator.getOutputFolder();
            Set<ResourceLocation> generatedRecipes = Sets.newHashSet();
            FancyDyesData.Recipes.generateRecipes(provider -> {
                ResourceLocation identifier = getRecipeIdentifier(provider.getId());

                if (!generatedRecipes.add(identifier)) {
                    throw new IllegalStateException("Duplicate recipe " + identifier);
                }

                JsonObject recipeJson = provider.serializeRecipe();
                ConditionJsonProvider[] conditions = FabricDataGenHelper.consumeConditions(provider);
                ConditionJsonProvider.write(recipeJson, conditions);

                saveRecipe(cache, recipeJson, path.resolve("data/" + identifier.getNamespace() + "/recipes/" + identifier.getPath() + ".json"));
                JsonObject advancementJson = provider.serializeAdvancement();

                if (advancementJson != null) {
                    ConditionJsonProvider.write(advancementJson, conditions);
                    saveAdvancement(cache, advancementJson, path.resolve("data/" + identifier.getNamespace() + "/advancements/" + provider.getAdvancementId().getPath() + ".json"));
                }
            });
        }

        protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
            return new ResourceLocation(FancyDyes.MOD_ID, identifier.getPath());
        }
    }
}
