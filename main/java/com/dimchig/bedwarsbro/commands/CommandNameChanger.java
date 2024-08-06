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

public class CommandNameChanger extends CommandBase {

	public static String command_text = "bwname";
	
	public CommandNameChanger() {
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
		return "Toggle bwname";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 2) {
			String player_name = args[0].trim();
			String player_new_name = args[1].trim();
			for (int i = 2; i < args.length; i++) {
				player_new_name += " " + args[i];
			}
			
			Main.chatListener.nickSpoof_name = "";
			Main.chatListener.nickSpoof_new_name = "";
			
			ChatSender.addText("&fSpoofed &e" + player_name + " &fon " + player_new_name);
			
			Main.chatListener.nickSpoof_name = player_name;
			Main.chatListener.nickSpoof_new_name = player_new_name;
		}
	}
}