package me.paypur.tconjei.jei;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tools.stats.*;

import java.util.List;

import static me.paypur.tconjei.TConJEI.MOD_ID;

public class HarvestStatsCategory extends AbstractMaterialStatsCategory {

    public HarvestStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 0, 0, 16, 16);
        this.title = Component.translatable("tconjei.tool_stats.harvest");
        this.recipeType = TConJEIPlugin.HARVEST_STATS;
        this.statsIds = List.of(HeadMaterialStats.ID, StatlessMaterialStats.BINDING.getIdentifier(), HandleMaterialStats.ID);
        this.tag = TinkerTags.Items.HARVEST;
    }

}
