package net.silentchaos512.torchbandolier.setup;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.torchbandolier.TorchBandolier;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(TorchBandolier.MOD_ID);

    public static Supplier<DataComponentType<Block>> TORCH = REGISTRAR.registerComponentType(
            "torch",
            builder -> builder
                    .persistent(BuiltInRegistries.BLOCK.byNameCodec())
                    .networkSynchronized(ByteBufCodecs.registry(Registries.BLOCK))
    );

    public static final Supplier<DataComponentType<Integer>> TORCH_COUNT = REGISTRAR.registerComponentType(
            "torch_count",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    public static final Supplier<DataComponentType<Boolean>> AUTOFILL = REGISTRAR.registerComponentType(
            "autofill",
            builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
    );
}
