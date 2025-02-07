package me.paypur.tconjei.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_TOOLTIP;

    static{
        BUILDER.push("TConJEI client config");
        BUILDER.pop();

        ENABLE_TOOLTIP = BUILDER.comment("Enable tooltip under Tinker's materials").define("enable_tooltip", true);

        SPEC = BUILDER.build();
    }
}
