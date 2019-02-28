/*
 * TorchBandolier -- Config
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.torchbandolier.config;

import net.minecraftforge.fml.loading.FMLPaths;
import net.silentchaos512.utils.config.ConfigSpecWrapper;
import net.silentchaos512.utils.config.IntValue;

public final class Config {
    private static final ConfigSpecWrapper WRAPPER = ConfigSpecWrapper.create(
            FMLPaths.CONFIGDIR.get().resolve("torchbandolier-common.toml"));

    public static final General GENERAL = new General(WRAPPER);

    public static class General {
        public final IntValue maxTorchCount;

        General(ConfigSpecWrapper wrapper) {
            maxTorchCount = wrapper
                    .builder("general.maxTorchCount")
                    .comment("The number of torches a torch bandolier can store")
                    .defineInRange(1024, 0, Integer.MAX_VALUE);
        }
    }

    private Config() {}

    public static void init() {
        WRAPPER.validate();
        WRAPPER.validate();
    }
}
