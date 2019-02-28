package net.silentchaos512.torchbandolier.util;

import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.silentchaos512.lib.util.generator.RecipeGenerator;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.init.ModItems;

public final class GenRecipes {
    private GenRecipes() {}

    public static void generateAll() {
        RecipeGenerator.create(new ResourceLocation(TorchBandolier.MOD_ID, "torch_bandolier"), RecipeGenerator.ShapedBuilder
                .create(ModItems.torchBandolier)
                .layout("###", "/o/", "###")
                .key('#', Items.LEATHER)
                .key('/', Tags.Items.RODS_WOODEN)
                .key('o', Tags.Items.GEMS_DIAMOND)
        );
    }
}
