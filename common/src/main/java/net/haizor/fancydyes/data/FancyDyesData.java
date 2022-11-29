package net.haizor.fancydyes.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dyes.StandardDyeColors;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FancyDyesData {
    public static class Language implements DataProvider {
        public final DataGenerator dataGenerator;
        public final String language;

        private Map<String, String> languageMap = new HashMap<>();

        public Language(DataGenerator generator) {
            this.dataGenerator = generator;
            this.language = "en_us";
        }

        public void generateLanguages() {
            this.add(FancyDyes.EMPTY_DYE_BOTTLE.get(), "Empty Dye Bottle");
            this.add(FancyDyes.DYE_REMOVER.get(), "Soap");

            this.add("item.fancydyes.flame_dye.desc", "Found in nether fortress chests");
            this.add("item.fancydyes.aurora_dye.desc", "Found in end city chests");

            this.add("gui.dye.tooltip.extend", "Press <Shift> for more info");
            this.add("gui.dye.tooltip.to_apply", "To Apply:");
            this.add("gui.dye.tooltip.apply_instructions", "Right click on any dyeable item in your inventory with this dye selected.");
            this.add("itemGroup.%s.dyes".formatted(FancyDyes.MOD_ID), "Fancy Dyes");

            for (String key : FancyDyes.DYES.keySet()) {
                Item item = Registry.ITEM.get(new ResourceLocation(FancyDyes.MOD_ID, key + "_dye"));
                this.add(item, format(key) + "Dye");
            }
        }

        private String format(String key) {
            String[] split = key.replace('_', ' ').split(" ");
            String total = "";
            for (String word : split) {
                total += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
            }
            return total;
        }

        public void add(String key, String value) {
            this.languageMap.put(key, value);
        }

        public void add(Item item, String value) {
            this.add(item.getDescriptionId(), value);
        }

        @Override
        public void run(@NotNull HashCache hashCache) throws IOException {
            this.languageMap = new HashMap<>();

            this.generateLanguages();

            Gson gson = new Gson();
            JsonObject langEntryJson = new JsonObject();

            for (Map.Entry<String, String> entry : this.languageMap.entrySet()) {
                langEntryJson.addProperty(entry.getKey(), entry.getValue());
            }

            DataProvider.save(gson, hashCache, langEntryJson, getLangFilePath(this.language));
        }

        private Path getLangFilePath(String code) {
            return dataGenerator.getOutputFolder().resolve("assets/%s/lang/%s.json".formatted(FancyDyes.MOD_ID, code));
        }

        @Override
        public String getName() {
            return "Languages";
        }
    }

    public static class Recipes {
        public static void generateRecipes(Consumer<FinishedRecipe> recipes) {
            Item emptyDyeBottle = FancyDyes.EMPTY_DYE_BOTTLE.get();

            for (StandardDyeColors.DyeColor color : StandardDyeColors.DYE_COLORS) {
                ShapelessRecipeBuilder.shapeless(Registry.ITEM.get(new ResourceLocation(FancyDyes.MOD_ID, "solid_" + color.string() + "_dye")))
                        .requires(Registry.ITEM.get(new ResourceLocation("minecraft", color.string() + "_dye")))
                        .requires(emptyDyeBottle)
                        .unlockedBy("criteria", inventoryTrigger(single(emptyDyeBottle)))
                        .save(recipes);

                ShapelessRecipeBuilder.shapeless(Registry.ITEM.get(new ResourceLocation(FancyDyes.MOD_ID, "shimmer_" + color.string() + "_dye")))
                        .requires(Registry.ITEM.get(new ResourceLocation("minecraft", color.string() + "_dye")))
                        .requires(emptyDyeBottle)
                        .requires(Items.GLOW_BERRIES)
                        .unlockedBy("criteria", inventoryTrigger(single(emptyDyeBottle)))
                        .save(recipes);

                ShapelessRecipeBuilder.shapeless(Registry.ITEM.get(new ResourceLocation(FancyDyes.MOD_ID, "shimmer_" + color.string() + "_dye")))
                        .requires(Registry.ITEM.get(new ResourceLocation(FancyDyes.MOD_ID, "solid_" + color.string() + "_dye")))
                        .requires(Items.GLOW_BERRIES)
                        .unlockedBy("criteria", inventoryTrigger(single(emptyDyeBottle)))
                        .save(recipes, FancyDyes.MOD_ID + ":shimmer_" + color.string() + "_dye_from_solid");
            }


            ShapedRecipeBuilder.shaped(emptyDyeBottle, 4)
                    .pattern("a a")
                    .pattern("a a")
                    .pattern(" a ")
                    .define('a', Items.GLASS)
                    .unlockedBy("criteria", inventoryTrigger(single(Items.GLASS)))
                    .save(recipes);


            ShapelessRecipeBuilder.shapeless(FancyDyes.DYE_REMOVER.get())
                    .requires(Items.WATER_BUCKET)
                    .requires(Items.CARROT)
                    .requires(Items.CHARCOAL)
                    .unlockedBy("criteria", inventoryTrigger(single(emptyDyeBottle)))
                    .save(recipes);
        }

        public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... itemPredicates) {
            return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, itemPredicates);
        }

        private static ItemPredicate single(Item item) {
            return new ItemPredicate(null, Set.of(item), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY);
        }


    }
}
