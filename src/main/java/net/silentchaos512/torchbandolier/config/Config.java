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

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.lib.config.ConfigBaseNew;
import net.silentchaos512.lib.config.ConfigOption;
import net.silentchaos512.lib.event.Greetings;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;
import net.silentchaos512.torchbandolier.TorchBandolier;

public final class Config extends ConfigBaseNew {
    public static final Config INSTANCE = new Config();

    @ConfigOption(name = "Max Torch Count", category = Configuration.CATEGORY_GENERAL)
    @ConfigOption.RangeInt(value = 1024, min = 0)
    @ConfigOption.Comment("The number of torches a torch bandolier can carry.")
    public static int maxTorchCount;

    private Config() {
        super(TorchBandolier.MOD_ID);
    }

    @Override
    public void load() {
        try {
            super.load();
        } catch (Exception ex) {
            TorchBandolier.log.fatal("Could not load configuration file!");
            Greetings.addMessage(player -> new TextComponentString(TextFormatting.RED + "[Torch Bandolier] Could not" +
                    " load configuration file! The mod may not work correctly. See log for details"));
            TorchBandolier.log.catching(ex);
        }
    }

    @Override
    public I18nHelper i18n() {
        return TorchBandolier.i18n;
    }

    @Override
    public LogHelper log() {
        return TorchBandolier.log;
    }
}
