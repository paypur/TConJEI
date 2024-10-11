package me.paypur.tconjei.mixin;

import me.paypur.tconjei.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemClassMixin {
    // TODO: some items for a material aren't included when they probably should
    // ice and fire (doesn't even appear in this function)
    // - silver ingot
    // - silver nugget
    // - wither bone
    @Inject(method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", at = @At("HEAD"))
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced, CallbackInfo ci) {
        Item key = pStack.getItem();
        if (Utils.allMaterialsTooltip.containsKey(key)) {
            pTooltipComponents.add(Utils.allMaterialsTooltip.get(key));
        }
    }

}
