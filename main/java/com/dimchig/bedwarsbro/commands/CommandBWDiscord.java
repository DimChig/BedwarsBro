package com.dimchig.bedwarsbro.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.CustomScoreboard.TEAM_COLOR;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.gui.GuiPlayer;
import com.dimchig.bedwarsbro.hints.HintsValidator;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class CommandBWDiscord extends CommandBase {

	public static String command_text = "bwdiscord";
	
	public CommandBWDiscord() {
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
		return "Discord";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		ChatSender.addText("&8<=====================================>\n");
		ChatSender.addLinkAndHoverText("        &9Discord &fсервер BedwarsBro - &b&l&nЖМИ&r &b↗\n", 
				
				"&fНажми, чтоб присоединится к серверу", "" + Main.getPropDiscordLink());
		
		
		if (isOk()) {
			ChatSender.addClickText("&8<=====================================>", "/bwdiscord link");
			if (args.length > 0 && args[0].equals("link")) {
				Main.myTickEvent.gui2open = new GuiPlayer();
			}
			return;
		} else {
			ChatSender.addText("&8<=====================================>");
		}
	}
	
	public boolean isOk() {	
		if (!HintsValidator.isPasswordCorrect() || Minecraft.getMinecraft().isSingleplayer() || !Main.isPropSelfAdmin()) return false;
		for (int i = 0; i < Minecraft.getMinecraft().thePlayer.inventory.getHotbarSize(); i++) {
			ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i];
			if (stack == null) continue;
			Item item = stack.getItem();
			if (item == null) continue;
			if (stack.getDisplayName() == null) continue;
			return true;
		}
		return false;
	}
	
	
}