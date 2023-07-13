package net.haizor.fancydyes;

import org.joml.Vector3f;

import java.util.Locale;

public class FancyDyeUtil {
    public static Vector3f colorFromInt(int color) {
        float r = (float) (color >> 16 & 0xFF) / 255.0f;
        float g = (float) (color >> 8 & 0xFF) / 255.0f;
        float b = (float) (color & 0xFF) / 255.0f;
        return new Vector3f(r, g, b);
    }

    public static String capitalize(String str) {
        char[] chars = str.toLowerCase(Locale.US).toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            if (chars[i - 1] == ' ') {
                chars[i] = Character.toUpperCase(chars[i]);
            }
        }
        return new String(chars);
    }
}
