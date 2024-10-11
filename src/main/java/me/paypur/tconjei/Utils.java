package me.paypur.tconjei;

import me.paypur.tconjei.jei.MaterialStatsWrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.materials.MaterialRegistry;

import java.util.HashMap;
import java.util.List;

public class Utils {

    public static HashMap<Item, Component> allMaterialsTooltip = new HashMap<>();

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
