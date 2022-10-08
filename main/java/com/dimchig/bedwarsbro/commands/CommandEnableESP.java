package com.dimchig.bedwarsbro.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Pattern;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class CommandEnableESP extends CommandBase {

	public static String command_text = "bwesp";
	
	public CommandEnableESP() {
		this.command_text = this.command_text.replace("/", "");
	}
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

	@Override
	public String getCommandName() {
		return this.command_text;
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Toggle bwesp";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		Main.playerFocus.STATE = !Main.playerFocus.STATE; 
		Main.playerFocus.isT_Active = false;
		ChatSender.addText((Main.playerFocus.STATE ? "&a" : "&c") + "ESP " + (Main.playerFocus.STATE ? "включен" : "выключен"));
	}
}