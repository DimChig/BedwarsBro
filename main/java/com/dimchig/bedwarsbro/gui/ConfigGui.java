package com.dimchig.bedwarsbro.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import com.dimchig.bedwarsbro.ColorCodesManager;
import com.dimchig.bedwarsbro.Main;

public class ConfigGui extends GuiConfig {

    @Mod.Instance
    private static Main asInstance;

    public ConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), Main.MODID, false, false, ColorCodesManager.replaceColorCodesInString("&eКонфиг &7для &cBedwars&fBro &7| &b&lНаводи мышкой на названия! &7| &2&ltrue &7= &aВключить&7, &4&lfalse &7= &cВыключить"));
    }

    private static List<IConfigElement> getConfigElements() {
        return new ArrayList<IConfigElement>(new ConfigElement(asInstance.getClientConfig()).getChildElements());
    }
}
