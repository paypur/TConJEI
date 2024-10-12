package me.paypur.tconjei;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.*;

import java.util.HashMap;
import java.util.List;

import static me.paypur.tconjei.TConJEI.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class TConJEI {

    public static final String MOD_ID = "tconjei";
    public static final List<MaterialStatsId> HARVEST_STAT_IDS = List.of(
            HeadMaterialStats.ID,
            StatlessMaterialStats.BINDING.getIdentifier(),
            HandleMaterialStats.ID
    );
    public static final List<MaterialStatsId> RANGED_STAT_IDS = List.of(
            LimbMaterialStats.ID,
            GripMaterialStats.ID,
            StatlessMaterialStats.BOWSTRING.getIdentifier()
    );
    public static final List<MaterialStatsId> ARMOR_STAT_IDS = List.of(
            PlatingMaterialStats.HELMET.getId(),
            PlatingMaterialStats.CHESTPLATE.getId(),
            PlatingMaterialStats.LEGGINGS.getId(),
            PlatingMaterialStats.BOOTS.getId(),
            PlatingMaterialStats.SHIELD.getId(),
            StatlessMaterialStats.MAILLE.getIdentifier(),
            StatlessMaterialStats.SHIELD_CORE.getIdentifier()
    );
    public static HashMap<Item, Component> allMaterialsTooltip = new HashMap<>();

}
