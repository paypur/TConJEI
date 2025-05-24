package me.paypur.tconjei.client;

import me.paypur.tconjei.ColorProvider;
import me.paypur.tconjei.TConJEI;
import me.paypur.tconjei.Utils;
import me.paypur.tconjei.jei.MaterialStatsWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.tools.item.RepairKitItem;

import static me.paypur.tconjei.TConJEI.*;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler {

    // TODO: might have problems if server changes and valid materials change too
    // runs on reload too
    @SubscribeEvent
    public static void onLogin(RecipesUpdatedEvent event) {
        allMaterialsTooltip.clear();

        if (ClientConfig.ENABLE_TOOLTIP.get()) {
            for (MaterialStatsWrapper wrapper : Utils.getMaterialWrappers()) {
                int h = wrapper.hasStats(HARVEST_STAT_IDS) ? 1 : 0;
                int r = wrapper.hasStats(RANGED_STAT_IDS) ? 1 : 0;
                int a = wrapper.hasStats(ARMOR_STAT_IDS) ? 1 : 0;

                int flag = h << 2 | r << 1 | a;

                if (flag == 0) {
                    continue;
                }

                int tier = wrapper.material().getTier();

                MutableComponent component = Component.translatable("tconjei.tooltip.tier", tier)
                        .withStyle(style -> style.withColor(ColorProvider.getTierColor(tier).orElse(0xAAAAAA)))
                        .append((switch (flag) {
                            case 0b001 -> Component.translatable("tconjei.tooltip.armor");
                            case 0b010 -> Component.translatable("tconjei.tooltip.ranged");
                            case 0b011 -> Component.translatable("tconjei.tooltip.ranged_armor");
                            case 0b100 -> Component.translatable("tconjei.tooltip.harvest");
                            case 0b101 -> Component.translatable("tconjei.tooltip.harvest_armor");
                            case 0b110 -> Component.translatable("tconjei.tooltip.harvest_ranged");
                            case 0b111 -> Component.translatable("tconjei.tooltip.harvest_ranged_armor");
                            default -> Component.empty();
                        })
                        .withStyle(ChatFormatting.GRAY));

                for (ItemStack stack : wrapper.getInputs()) {
                    if (!(stack.getItem() instanceof RepairKitItem)) {
                        TConJEI.allMaterialsTooltip.put(stack.getItem(), component);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onToolTip(ItemTooltipEvent event) {
        if (ClientConfig.ENABLE_TOOLTIP.get()) {
            Item key = event.getItemStack().getItem();
            if (TConJEI.allMaterialsTooltip.containsKey(key)) {
                event.getToolTip().add(TConJEI.allMaterialsTooltip.get(key));
            }
        }
    }

}
