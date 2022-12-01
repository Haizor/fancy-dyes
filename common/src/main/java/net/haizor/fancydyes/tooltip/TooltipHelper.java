package net.haizor.fancydyes.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
import net.haizor.fancydyes.mixin.KeyMappingAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TooltipHelper {
    public static List<Component> extended(Supplier<List<Component>> extended) {
        Minecraft mc = Minecraft.getInstance();
        if (!InputConstants.isKeyDown(mc.getWindow().getWindow(), ((KeyMappingAccessor)mc.options.keyShift).getKey().getValue())) {
            List<Component> list = new ArrayList<>();
            String[] split = Component.translatable("gui.tooltip.extend").getString().split("(#keybind)");
            list.add(
                Component.literal(split[0]).withStyle(ChatFormatting.GRAY).append(
                Component.keybind("key.sneak").withStyle(ChatFormatting.YELLOW).append(
                Component.translatable(split.length >= 2 ? split[1] : "").withStyle(ChatFormatting.GRAY)
            )));
            return list;
        } else {
            return extended.get();
        }
    }

    public static List<Component> wrap(String str, Consumer<MutableComponent> formatter) {
        List<Component> list = new ArrayList<>();

        int l = str.length();
        int i = 0;
        String[] split = str.split(" ");

        while (l > 0) {
            String line = "";
            int charCount = 0;
            while (charCount < 24 && i < split.length) {
                String curr = split[i];
                if (i != split.length - 1) curr += " ";
                line += curr;
                i++;
                charCount += curr.length();
            }
            l -= charCount;
            MutableComponent component = Component.literal("  %s".formatted(line));
            formatter.accept(component);
            list.add(component);
        }
        return list;
    }
}
