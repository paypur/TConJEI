package me.paypur.tconjei;

public class TConJEIUtils {
    public static String[] colonSplit(String str, boolean needColon) {
        String[] strings;
        if (str.contains(":")) {
            strings = str.split(":");
            if (needColon)
                strings[0] += ":";
        } else if (str.contains("：")) {
            strings = str.split("：");
            if (needColon)
                strings[0] += "：";
        } else {
            strings = new String[] {str, ""};
        }
        return strings;
    }
}
