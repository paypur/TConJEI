package me.paypur.tconjei;

public class Utils {

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

    public static boolean inBox(double mX, double mY, float x, float y, float w, float h) {
        return (x <= mX && mX <= x + w && y <= mY && mY <= y + h);
    }

}
