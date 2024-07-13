package me.paypur.tconjei;

public class TConJEIUtils {
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
