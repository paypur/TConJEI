package me.paypur.tconjei;

import me.paypur.tconjei.jei.MaterialStatsWrapper;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.materials.MaterialRegistry;

import java.util.HashMap;
import java.util.List;

public class Utils {

    public static HashMap<String, Component> allMaterialsTooltip = new HashMap<>();

    public static List<MaterialStatsWrapper> getMaterialWrappers() {
        return MaterialRegistry.getInstance().getVisibleMaterials()
                .stream()
                .map(MaterialStatsWrapper::new)
                .toList();
    }

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
