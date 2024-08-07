package me.paypur.tconjei;

import net.minecraft.world.item.Item;

import java.util.HashSet;

public class Utils {

    public static HashSet<Item> AllInputs = new HashSet<>();

    public static boolean inBox(double mX, double mY, float x, float y, float w, float h) {
        return (x <= mX && mX <= x + w && y <= mY && mY <= y + h);
    }

    public static String[] colonSplit(String str) {
        String[] strings;
        if (str.contains(":")) {
            strings = str.split(":");
            strings[0] += ":";
        } else if (str.contains("：")) {
            strings = str.split("：");
            strings[0] += "：";
        } else {
            strings = new String[] {str, ""};
        }
        return strings;
    }

}
