package me.paypur.tconjei;

import net.minecraft.network.chat.TextColor;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Optional;

public class ColorProvider {
    public static int TEXT = 0x3F3F3F;
    public static final int DURABILITY = ToolStats.DURABILITY.getColor().getValue();
    public static final int ARMOR = ToolStats.ARMOR.getColor().getValue();

    public static Optional<TextColor> getTierTextColor(int i) {
        MaterialId id = switch (i) {
            case 0, 1 -> MaterialId.tryParse("tconstruct:rock");
            case 2 -> MaterialId.tryParse("tconstruct:slimewood");
            case 3 -> MaterialId.tryParse("tconstruct:cobalt");
            case 4 -> MaterialId.tryParse("tconstruct:manyullyn");
            default -> null;
        };

        return id != null ? Optional.of(MaterialTooltipCache.getColor(id)) : Optional.empty();
    }

    public static Optional<Integer> getTierColor(int i) {
        return getTierTextColor(i).map(TextColor::getValue);
    }
}
