package me.paypur.tconjei.client;

import me.paypur.tconjei.ColorManager;
import me.paypur.tconjei.TConJEI;
import me.paypur.tconjei.Utils;
import me.paypur.tconjei.jei.MaterialStatsWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static me.paypur.tconjei.TConJEI.*;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler {

    // TODO: might have problems if server changes and valid materials change too
    // runs on reload too
    @SubscribeEvent
    public static void onLogin(RecipesUpdatedEvent event) {
        if (!TConJEI.allMaterialsTooltip.isEmpty()) {
            return;
        }

        for (MaterialStatsWrapper wrapper : Utils.getMaterialWrappers()) {
            int h = wrapper.hasStats(HARVEST_STAT_IDS) ? 1 : 0;
            int r = wrapper.hasStats(RANGED_STAT_IDS) ? 1 : 0;

            int flag = h << 1 | r;

            if (flag == 0) {
                continue;
            }

            int tier = wrapper.material().getTier();

            MutableComponent component = new TranslatableComponent("tconjei.tooltip.tier", tier)
                .withStyle(style -> style.withColor(ColorManager.getTierColor(tier).orElse(0xAAAAAA)))
                .append((switch (flag) {
                    case 0b01 -> new TranslatableComponent("tconjei.tooltip.ranged");
                    case 0b10 -> new TranslatableComponent("tconjei.tooltip.harvest");
                    case 0b11 -> new TranslatableComponent("tconjei.tooltip.harvest_ranged");
                    default -> (MutableComponent) TextComponent.EMPTY;
                })
                .withStyle(ChatFormatting.GRAY));

            for (ItemStack stack : wrapper.getInputs()) {
                // exclude repair kits, doesn't effect 1.19.2
                if (stack.getDescriptionId().equals("item.tconstruct.repair_kit")) {
                    continue;
                }
                TConJEI.allMaterialsTooltip.put(stack.getItem(), component);
            }
        }
    }

}
