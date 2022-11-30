package net.haizor.fancydyes.forge.data;

import com.google.gson.JsonObject;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.data.FancyDyesData;
import net.haizor.fancydyes.dyes.AuroraDye;
import net.haizor.fancydyes.dyes.FancyDye;
import net.haizor.fancydyes.dyes.FlameDye;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class FancyDyesDataForge {
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, FancyDyes.MOD_ID);
    private static final RegistryObject<DyeLootModifier.Serializer> DYE_LOOT = GLM.register("dye_loot", DyeLootModifier.Serializer::new);

    public static void gatherData(GatherDataEvent event) {
//        event.getGenerator().addProvider(new FancyDyesData.Language(event.getGenerator()));
        event.getGenerator().addProvider(new ItemModels(event));
        event.getGenerator().addProvider(new Recipes(event));
        event.getGenerator().addProvider(new LootModifiers(event.getGenerator(), FancyDyes.MOD_ID));
    }

    public static class DyeLootModifier extends LootModifier {
        String dye;
        protected DyeLootModifier(LootItemCondition[] conditionsIn, String dye) {
            super(conditionsIn);
            this.dye = dye;
        }

        @NotNull
        @Override
        protected List<ItemStack> doApply(List<ItemStack> list, LootContext arg) {
            FancyDye fancyDye = FancyDyes.DYES.get(this.dye);
            if (fancyDye == null) return list;
            list.add(new ItemStack(FancyDye.getItem(fancyDye)));
            return list;
        }

        private static class Serializer extends GlobalLootModifierSerializer<DyeLootModifier> {
            @Override
            public DyeLootModifier read(ResourceLocation arg, JsonObject jsonObject, LootItemCondition[] args) {
                return new DyeLootModifier(args, GsonHelper.getAsString(jsonObject, "dye"));
            }

            @Override
            public JsonObject write(DyeLootModifier iGlobalLootModifier) {
                JsonObject object = makeConditions(iGlobalLootModifier.conditions);
                object.addProperty("dye", iGlobalLootModifier.dye);
                return object;
            }
        }
    }


    public static class ItemModels extends ItemModelProvider {
        public ItemModels(GatherDataEvent event) {
            super(event.getGenerator(), FancyDyes.MOD_ID, event.getExistingFileHelper());
        }

        @Override
        protected void registerModels() {
            ResourceLocation base = new ResourceLocation(FancyDyes.MOD_ID, "item/dye_bottle");
            for (String key : FancyDyes.DYES.keySet()) {
                this.getBuilder(key + "_dye").parent(new ModelFile.UncheckedModelFile(base));
            }
        }
    }

    public static class Recipes extends RecipeProvider {
        public Recipes(GatherDataEvent event) {
            super(event.getGenerator());
        }

        @Override
        protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
            FancyDyesData.Recipes.generateRecipes(consumer);
        }
    }

    public static class LootModifiers extends GlobalLootModifierProvider {
        public LootModifiers(DataGenerator gen, String modid) {
            super(gen, modid);
        }

        @Override
        protected void start() {
            add("flame_dye", DYE_LOOT.get(), new DyeLootModifier(new LootItemCondition[] {
                    LootTableIdCondition.builder(FlameDye.LOOT_TABLE).build(),
                    FlameDye.LOOT_CONDITION
            }, "flame"));

            add("aurora_dye", DYE_LOOT.get(), new DyeLootModifier(new LootItemCondition[] {
                    LootTableIdCondition.builder(AuroraDye.LOOT_TABLE).build(),
                    AuroraDye.LOOT_CONDITION
            }, "aurora"));
        }
    }
}
