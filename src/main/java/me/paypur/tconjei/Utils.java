package me.paypur.tconjei;

import me.paypur.tconjei.jei.MaterialStatsWrapper;
import slimeknights.tconstruct.library.materials.MaterialRegistry;

import java.util.List;

public class Utils {

    public static List<MaterialStatsWrapper> getMaterialWrappers() {
        return MaterialRegistry.getInstance().getVisibleMaterials()
                .stream()
                .map(MaterialStatsWrapper::new)
                .toList();
    }

    public static boolean inBox(double mX, double mY, float x, float y, float w, float h) {
        return (x <= mX && mX <= x + w && y <= mY && mY <= y + h);
    }

}
