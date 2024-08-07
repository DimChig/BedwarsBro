package com.dimchig.bedwarsbro.commands;

import java.io.File;
import java.io.IOException;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.stuff.HintsFinder;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandHintsFinderLookAtPlayer extends CommandBase {

	public static String command_text = "/bedwarsChatModLookAtPlayer";
	
	public CommandHintsFinderLookAtPlayer() {
		this.command_text = command_text.replace("/", "");
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
		return "Makes message rainbow";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length != 4) return;
		try {
			double posX = Double.parseDouble(args[0]);
			double posY = Double.parseDouble(args[1]);
			double posZ = Double.parseDouble(args[2]);
			String name = args[3];
			HintsFinder.findAndlookAtPlayer(posX, posY, posZ, name);
		} catch (Exception ex) {
			ChatSender.addText("&cInvalid arguments!");
		}
	}
}